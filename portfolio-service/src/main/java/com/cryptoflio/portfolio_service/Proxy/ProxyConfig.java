package com.cryptoflio.portfolio_service.Proxy;

import org.springframework.context.annotation.Bean;

import feign.Logger;


public class ProxyConfig {

    @Bean
    Logger.Level feignLoggerLevel(){return Logger.Level.FULL;}
}
