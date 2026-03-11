package com.example.fitnationrestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.fitnationrestapi", "com.example.fitnationuser", "com.example.fitnationtrainer"})
@EntityScan(basePackages = {"com.example.fitnationuser", "com.example.fitnationtrainer"})
@EnableJpaRepositories(basePackages = {"com.example.fitnationuser.repository", "com.example.fitnationtrainer.repository"})
public class FitnationRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitnationRestApiApplication.class, args);
    }

}
