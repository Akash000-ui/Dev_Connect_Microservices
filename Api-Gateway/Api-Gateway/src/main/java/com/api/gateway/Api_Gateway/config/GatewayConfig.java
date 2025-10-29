package com.api.gateway.Api_Gateway.config;

import com.api.gateway.Api_Gateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private AuthenticationFilter authFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service Routes (No authentication for login/register)
                .route("auth-service", r -> r.path("/auth-service/**")
                        .filters(f -> f
                                .stripPrefix(1) // Remove /auth-service from path
                                .filter(authFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://auth-service"))

                // Profile Service Routes (Authentication required)
                .route("profile-service", r -> r.path("/profile-service/**")
                        .filters(f -> f
                                .stripPrefix(1) // Remove /profile-service from path
                                .filter(authFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://profile-service"))

                // Eureka Server Route (No authentication)
                .route("eureka-server", r -> r.path("/eureka/**")
                        .uri("http://localhost:8761"))

                .build();
    }
}