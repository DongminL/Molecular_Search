package com.example.molecularsearch.chem_info.repository;

import com.example.molecularsearch.chem_info.domain.ChemInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChemInfoRepository extends MongoRepository<ChemInfo, String> {

    Optional<ChemInfo> findByIsomericSmiles(String isomericSmiles); // 이성질체 SMILES로 분자 정보 조회
}
