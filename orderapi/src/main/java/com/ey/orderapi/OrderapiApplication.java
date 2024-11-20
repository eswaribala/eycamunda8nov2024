package com.ey.orderapi;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Deployment(resources ={ ("classpath*:/processes/**/*.bpmn"),("classpath*:/decisions/**/*.dmn")})
public class OrderapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderapiApplication.class, args);
    }

}
