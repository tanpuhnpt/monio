package com.tanpuh.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanpuh.gateway.dto.ErrorResponse;
import com.tanpuh.gateway.dto.IntrospectRequest;
import com.tanpuh.gateway.repo.AuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {
    private final ObjectMapper objectMapper;
    private final AuthClient authClient;
    private final PathPatternParser pathPatternParser = new PathPatternParser();

    private final String[] PUBLIC_ENDPOINTS = {"/auth/**", "/categories/**"};

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // step 1: bypass public endpoints
        // if true, then process to next filters
        if (isPublicEndpoint(exchange.getRequest()))
            return chain.filter(exchange);

        // step 2: get token from header
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer "))
            return unauthenticate(exchange.getResponse());

        String token = authHeader.substring(7);

        // step 3: delegate auth service to verify token
        // if isValid=true, then process to next filters
        // else return error UNAUTHENTICATED
        return authClient.introspect(new IntrospectRequest(token)).flatMap(responseEntity -> {
            var body = responseEntity.getBody();
            if (body != null && body.isValid()) return chain.filter(exchange);
            else return unauthenticate(exchange.getResponse());
        }).onErrorResume(throwable -> unauthenticate(exchange.getResponse()));
    }

    @Override
    public int getOrder() {
        // số càng nhỏ thì thứ tự càng lớn
        // vì các filter của cloud gateway > 0, nên set order = -1
        return -1;
    }

    private Mono<Void> unauthenticate(ServerHttpResponse response) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(1401)
                .message("Unauthenticated at gateway")
                .build();

        String body;

        try {
            body = objectMapper.writeValueAsString(errorResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    private boolean isPublicEndpoint(ServerHttpRequest request) {
        PathContainer path = request.getPath().pathWithinApplication();
        return Arrays.stream(PUBLIC_ENDPOINTS).anyMatch(pattern -> pathPatternParser.parse(pattern).matches(path));
    }
}
