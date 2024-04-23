package com.example.molecularsearch.repository;

import com.example.molecularsearch.entity.Synonyms;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SynonymsRepository extends MongoRepository<Synonyms, String> {
}
