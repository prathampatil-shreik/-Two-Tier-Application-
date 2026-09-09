package com.shreik.twotierapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class TwoTierAppApplication {

    private static final Logger logger = LoggerFactory.getLogger(TwoTierAppApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TwoTierAppApplication.class, args);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationReady() {
        logger.info("Two-Tier Application started successfully on port 8080");
        logger.info("Database connection initialized via Spring Data JPA");
        logger.info("Health endpoint available at: /actuator/health");
        logger.info("API endpoints available at: /api/messages");
    }
}
