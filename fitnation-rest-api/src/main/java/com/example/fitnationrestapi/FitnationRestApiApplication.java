package com.example.fitnationrestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(
        basePackages = {
        "com.example.fitnationrestapi",
        "com.example.fitnationweb",
        "com.example.fitnationuser",
        "com.example.fitnationmembership",
        "com.example.fitnationtrainer",
        "com.example.fitnationbooking",
        "com.example.fitnationprogress",
        "com.example.fitnationcommon",
        "com.fitnationnutrition"},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.example\\.fitnationweb\\.FitnationWebApplication"))
@EntityScan(basePackages = {
        "com.example.fitnationuser",
        "com.example.fitnationmembership.model",
        "com.example.fitnationtrainer",
        "com.example.fitnationbooking",
        "com.example.fitnationprogress.model",
        "com.fitnationnutrition.model",
        "com.example.fitnationrestapi.entity"})
@EnableJpaRepositories(basePackages = {
        "com.example.fitnationuser.repository",
        "com.example.fitnationuser.payment",
        "com.example.fitnationmembership.repository",
        "com.example.fitnationuser.device",
        "com.example.fitnationtrainer.repository",
        "com.example.fitnationbooking.repository",
        "com.example.fitnationprogress.repository",
        "com.fitnationnutrition.repository",
        "com.example.fitnationrestapi.repository"})
@EnableScheduling
public class FitnationRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitnationRestApiApplication.class, args);
    }
}