package com.cryptofolio.api_gateway.filter;

import com.cryptofolio.api_gateway.util.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter {

    @Autowired
    private RouteValidator routeValidator;
    @Autowired
    private JwtService jwtService;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        HttpMethod method = request.getMethod();
        var headers = exchange.getRequest().getHeaders();

        if (method == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        if (routeValidator.isSecured.test(exchange.getRequest())){
            //header contains token or not
            if (!headers.containsKey(HttpHeaders.AUTHORIZATION)){

                return Mono.error( new RuntimeException("header does not contain AUTHORIZATION"));
//                throw new RuntimeException("header does not contain AUTHORIZATION");
            }

            String authHeader = headers.get(HttpHeaders.AUTHORIZATION).get(0);

            if (authHeader!=null && authHeader.startsWith("Bearer ")){
                authHeader= authHeader.substring(7);

            }
            String token = authHeader;

            try{
                jwtService.validateToken(token);

                exchange.getRequest()
                       .mutate()
                       .header("loggedInUser",jwtService.extractUsername(token))
                       .build();

            }catch (Exception e){
                System.out.println(e+" error");
                throw new RuntimeException(e.getMessage());
            }

        }

        return chain.filter(exchange);
    }

}
