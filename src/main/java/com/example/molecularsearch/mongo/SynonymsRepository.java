package com.example.molecularsearch.mongo;

import com.example.molecularsearch.entity.Synonyms;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SynonymsRepository extends MongoRepository<Synonyms, String> {
}
