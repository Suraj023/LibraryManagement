package com.library.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GatewayConfig {

    @Value("${gateway.routes.users.rateLimit:30}")
    private int usersRateLimit;

    @Value("${gateway.routes.books.rateLimit:60}")
    private int booksRateLimit;

    @Bean
    public List<RouteDefinition> routes() {
        return List.of(
                RouteDefinition.builder()
                        .id("users-api")
                        .pathPattern("/api/users/**")
                        .rateLimit(usersRateLimit)
                        .description("User management API — 30 requests/min per IP")
                        .build(),
                RouteDefinition.builder()
                        .id("books-api")
                        .pathPattern("/api/books/**")
                        .rateLimit(booksRateLimit)
                        .description("Book catalogue API — 60 requests/min per IP")
                        .build()
        );
    }
}