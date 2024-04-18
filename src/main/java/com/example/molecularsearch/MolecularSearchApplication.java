package com.example.molecularsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableJpaAuditing
@EnableMongoAuditing
@EnableJpaRepositories(basePackages = "com.example.molecularsearch.repository")
@EnableMongoRepositories(basePackages = "com.example.molecularsearch.mongo")
@SpringBootApplication
public class MolecularSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(MolecularSearchApplication.class, args);
    }

}
