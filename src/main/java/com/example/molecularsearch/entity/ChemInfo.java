package com.example.molecularsearch.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class ChemInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // ChemInfo PK

    @Column(nullable = false, unique = true)
    private Long cid;   // 분자 고유 ID

    @Column
    private String inpacName;   // 유기 화합물 이름

    @Column
    private String molecularFormula; // 화학식

    @Column
    private Double molecularWeight; // 분자량 (g/mol)

    @Column
    private String isomericSmiles;  // 이성질체 SMILES

    @Column
    private String inchi;   // 국제 화학 식별자

    @Column
    private String inchiKey;    // InChI Key 값

    @Column
    private String canonicalSmiles; // 표준 SMILES

    @Column
    private String imagePath;   // 이미지 경로

    @Builder
    public ChemInfo(Long id, Long cid, String inpacName, String molecularFormula, Double molecularWeight, String isomericSmiles, String inchi, String inchiKey, String canonicalSmiles, String imagePath) {
        this.id = id;
        this.cid = cid;
        this.inpacName = inpacName;
        this.molecularFormula = molecularFormula;
        this.molecularWeight = molecularWeight;
        this.isomericSmiles = isomericSmiles;
        this.inchi = inchi;
        this.inchiKey = inchiKey;
        this.canonicalSmiles = canonicalSmiles;
        this.imagePath = imagePath;
    }
}
