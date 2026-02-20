package com.networkticketingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.networkticketingapp")
public class NetworkTicketingApplication {
    public static void main(String[] args) {
        SpringApplication.run(NetworkTicketingApplication.class, args);
    }
}

