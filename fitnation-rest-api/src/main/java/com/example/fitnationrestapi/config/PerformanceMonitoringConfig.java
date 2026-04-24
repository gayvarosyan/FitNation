package com.example.fitnationrestapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Slf4j
@Configuration
public class PerformanceMonitoringConfig {

    @Bean
    public Pageable defaultPageable() {
        return PageRequest.of(0, 20);
    }
}
