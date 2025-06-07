package com.bookinline.bookinline.security;

import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private final boolean enabled;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private RateLimitingFilter(@Value("${bucket4j.enabled:true}") boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String key = resolveKey(request);
        Bucket bucket = buckets.computeIfAbsent(key, this::createBucket);

        if (!enabled || bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests, try again later");
        }
    }
    private String resolveKey(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return request.getRemoteAddr();
        } else {
            return authentication.getName();
        }
    }
    private Bucket createBucket(String key) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Bandwidth limit;

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            limit = Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(10)));
        } else if (authentication.getAuthorities().stream().anyMatch(
                a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            limit = Bandwidth.classic(1000, Refill.greedy(1000, Duration.ofMinutes(10)));
        } else {
            limit = Bandwidth.classic(500, Refill.greedy(500, Duration.ofMinutes(10)));
        }

        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }
}
