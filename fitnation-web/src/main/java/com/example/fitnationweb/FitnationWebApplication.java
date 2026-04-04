package com.example.fitnationweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.example.fitnationweb",
        "com.example.fitnationuser",
        "com.example.fitnationmembership",
        "com.example.fitnationtrainer",
        "com.example.fitnationbooking",
        "com.example.fitnationcommon",
        "com.fitnationnutrition"
})
public class FitnationWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitnationWebApplication.class, args);
    }
}
