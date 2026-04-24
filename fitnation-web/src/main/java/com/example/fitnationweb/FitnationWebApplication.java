package com.example.fitnationweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.example.fitnationweb",
        "com.example.fitnationuser",
        "com.example.fitnationmembership",
        "com.example.fitnationtrainer",
        "com.example.fitnationbooking",
        "com.example.fitnationcommon",
        "com.fitnationnutrition",
        "com.example.fitnationprogress"
})
@EntityScan(basePackages = {
        "com.example.fitnationuser",
        "com.example.fitnationmembership.model",
        "com.example.fitnationtrainer",
        "com.example.fitnationbooking",
        "com.fitnationnutrition.model",
        "com.example.fitnationprogress.model"
})
@EnableJpaRepositories(basePackages = {
        "com.example.fitnationuser.repository",
        "com.example.fitnationuser.payment",
        "com.example.fitnationuser.device",
        "com.example.fitnationmembership.repository",
        "com.example.fitnationtrainer.repository",
        "com.example.fitnationbooking.repository",
        "com.fitnationnutrition.repository",
        "com.example.fitnationprogress.repository"
})
public class FitnationWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitnationWebApplication.class, args);
    }
}
