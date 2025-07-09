package com.bookinline.bookinline.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;

public class SameSiteCookieFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        if (response instanceof HttpServletResponse) {
            HttpServletResponse res = (HttpServletResponse) response;
            Collection<String> headers = res.getHeaders("Set-Cookie");
            boolean firstHeader = true;
            for (String header : headers) {
                if (header.contains("XSRF-TOKEN") && !header.contains("SameSite=None")) {
                    String newHeader = header + "; SameSite=None; Secure";
                    if (firstHeader) {
                        res.setHeader("Set-Cookie", newHeader);
                        firstHeader = false;
                    } else {
                        res.addHeader("Set-Cookie", newHeader);
                    }
                }
            }
        }
    }
}
