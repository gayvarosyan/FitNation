package com.example.fitnationweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.example.fitnationweb",
        "com.example.fitnationuser",
        "com.example.fitnationmembership",
        "com.example.fitnationtrainer",
        "com.example.fitnationbooking",
        "com.example.fitnationcommon",
        "com.fitnationnutrition"})
@EntityScan(basePackages = {
        "com.example.fitnationuser",
        "com.example.fitnationmembership.model",
        "com.example.fitnationtrainer",
        "com.example.fitnationbooking",
        "com.fitnationnutrition.model"})
@EnableJpaRepositories(basePackages = {
        "com.example.fitnationuser.repository",
        "com.example.fitnationuser.payment",
        "com.example.fitnationmembership.repository",
        "com.example.fitnationtrainer.repository",
        "com.example.fitnationbooking.repository",
        "com.fitnationnutrition.repository"})
public class FitnationWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitnationWebApplication.class, args);
    }
}
