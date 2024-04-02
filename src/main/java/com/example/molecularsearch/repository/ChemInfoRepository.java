package com.example.molecularsearch.repository;

import com.example.molecularsearch.entity.ChemInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChemInfoRepository extends JpaRepository<ChemInfo, Long> {

}
