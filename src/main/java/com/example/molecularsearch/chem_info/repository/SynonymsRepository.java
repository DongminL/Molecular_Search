package com.example.molecularsearch.chem_info.repository;

import com.example.molecularsearch.chem_info.domain.Synonyms;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SynonymsRepository extends MongoRepository<Synonyms, String> {
}
