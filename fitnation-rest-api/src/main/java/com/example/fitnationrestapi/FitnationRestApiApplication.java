package com.example.fitnationrestapi;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.example.fitnationuser", "com.example.fitnationtainuser"})
@EnableJpaRepositories(basePackages = {"com.example.fitnationuser.repository", "com.example.fitnationtainuser.repository"})
public class FitnationRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitnationRestApiApplication.class, args);
    }

}
