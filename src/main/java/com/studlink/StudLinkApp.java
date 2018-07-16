package com.studlink;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Startpoint
 */
@SpringBootApplication
@EnableAsync
public class StudLinkApp
{
    public static void main(String[] args) {
        SpringApplication.run(StudLinkApp.class, args);
    }
}