package com.example.imdbplus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImdbPlusApplication {
    private static final Logger mainLogger = LoggerFactory.getLogger(ImdbPlusApplication.class);
    public static void main(String[] args) {
        mainLogger.info("Sever Up and Running");

        SpringApplication.run(ImdbPlusApplication.class, args);
    }

}
