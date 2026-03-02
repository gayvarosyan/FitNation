package com.example.fitnationrestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example")
public class FitnationRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitnationRestApiApplication.class, args);
    }

}
