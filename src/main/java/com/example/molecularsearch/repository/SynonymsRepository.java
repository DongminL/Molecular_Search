package com.example.molecularsearch.repository;

import com.example.molecularsearch.entity.Synonyms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SynonymsRepository extends JpaRepository<Synonyms, Long> {
}
