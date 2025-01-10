package com.cryptofolio.api_gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openAPiEndpoints = List.of(

            "/auth/register",
            "/auth/login",
            "/eureka",
            "/actuator",
            "/user/reset-password"

    );


    public Predicate<ServerHttpRequest> isSecured =
            request-> openAPiEndpoints
                    .stream()
                    .noneMatch(uri-> request.getURI().getPath().contains(uri));
}
