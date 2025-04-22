package com.bookinline.bookinline.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BookInline API")
                        .version("1.0")
                        .description("API to manage properties, bookings, and reviews for BookInline application.")
                        .contact(new Contact()
                                .name("Vladyslav Kokitko")
                                .email("kokitko.vladyslav@gmail.com")
                                .url("https://github.com/kokitko/bookinline"))
                        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT")));
    }
}
