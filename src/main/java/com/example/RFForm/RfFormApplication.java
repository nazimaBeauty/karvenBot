package com.example.RFForm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class RfFormApplication {

    public static void main(String[] args) {
        SpringApplication.run(RfFormApplication.class, args);
    }

}
