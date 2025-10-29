package com.api.gateway.Api_Gateway.filter;

import com.api.gateway.Api_Gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Check if the request path is in the excluded paths
            if (isSecured(request)) {
                // Check if Authorization header is present
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                String token = authHeader.substring(7);

                try {
                    // Validate JWT token
                    if (!jwtUtil.validateToken(token)) {
                        return onError(exchange, HttpStatus.UNAUTHORIZED);
                    }

                    // Extract user information from JWT
                    String userId = jwtUtil.extractUserId(token);
                    String email = jwtUtil.extractEmail(token);

                    // Add user information to headers for downstream services
                    ServerHttpRequest modifiedRequest = exchange.getRequest()
                            .mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Email", email)
                            .build();

                    ServerWebExchange modifiedExchange = exchange.mutate()
                            .request(modifiedRequest)
                            .build();

                    log.info("✅ Authenticated request for user: {} to path: {}", userId, request.getPath());

                    return chain.filter(modifiedExchange);

                } catch (Exception e) {
                    log.error("❌ JWT validation failed: {}", e.getMessage());
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }
            }

            // For unsecured paths, just pass through
            return chain.filter(exchange);
        };
    }

    private boolean isSecured(ServerHttpRequest request) {
        String path = request.getPath().value();

        System.out.println("INCOMING REQUEST" + path);

        // Define unsecured paths (no authentication required)
        return !path.contains("/api/auth/google-auth") &&
                !path.contains("/api/auth/health") &&
                !path.contains("/api/auth/login") &&
                !path.contains("/eureka") &&
                !path.contains("/api/auth/register") &&
                !path.contains("/actuator");
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        String body = "{\"success\":false,\"message\":\"" +
                (httpStatus == HttpStatus.UNAUTHORIZED ? "Unauthorized access" : "Access denied") +
                "\",\"statusCode\":" + httpStatus.value() + "}";

        org.springframework.core.io.buffer.DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {
        // Configuration properties can be added here if needed
    }
}