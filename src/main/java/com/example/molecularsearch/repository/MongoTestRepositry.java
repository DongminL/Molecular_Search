package com.example.molecularsearch.repository;

import com.example.molecularsearch.entity.MongoTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoTestRepositry extends MongoRepository<MongoTest, String> {
    Optional<MongoTest> findByIsomericSmiles(String smiles);
}
