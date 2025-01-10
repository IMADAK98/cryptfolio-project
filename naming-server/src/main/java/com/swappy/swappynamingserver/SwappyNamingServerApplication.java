package com.swappy.swappynamingserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SwappyNamingServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwappyNamingServerApplication.class, args);
    }

}
