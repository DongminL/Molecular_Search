package com.example.molecularsearch.repository;

import com.example.molecularsearch.entity.ChemInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChemInfoRepository extends JpaRepository<ChemInfo, Long> {

    Optional<ChemInfo> findByIsomericSmiles(String isomericSmiles); // 이성질체 SMILES로 분자 정보 조회

    List<ChemInfo> findAllByInpacName(String inpacName);   // 이름으로 분자 정보 조회
    
    List<ChemInfo> findAllByMolecularFormula(String molecularFormula); // 화학식으로 분자 정보 조회

    boolean existsByInpacName(String inpacName);    // 이름으로 분자 정보 여부 확인

    boolean existsByMolecularFormula(String molecularFormula);  // 화학식으로 분자 정보 여부 확인

    boolean existsByIsomericSmiles(String isomericSmiles);  // 이성질체 SMILES로 분자 정보 여부 확인
}
