package com.example.fitnationweb;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "com.example.fitnationweb",
        "com.example.fitnationuser",
        "com.example.fitnationmembership",
        "com.example.fitnationtrainer",
        "com.example.fitnationbooking",
        "com.example.fitnationcommon",
        "com.fitnationnutrition"})
public class FitnationWebApplication {

}
