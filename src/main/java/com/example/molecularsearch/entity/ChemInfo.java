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
@Table(indexes = {
        @Index(name = "index_smiles", columnList = "isomericSmiles", unique = true),   // isomericSmiles Column 기준으로 Unique Index 생성
        @Index(name = "index_formula", columnList = "molecularFormula") // molecularFormula에 대한 Index 생성
})
public class ChemInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // ChemInfo PK

    @Column(nullable = false)
    private Long cid;   // 분자 고유 ID

    @Column(length = 450)
    private String inpacName;   // 유기 화합물 이름

    @Column(nullable = false)
    private String molecularFormula; // 화학식

    @Column
    private Double molecularWeight; // 분자량 (g/mol)

    @Column
    private String canonicalSmiles; // 표준 SMILES

    @Column(nullable = false)
    private String isomericSmiles;  // 이성질체 SMILES (이성질체 : 분자식은 같지만 분자 구조가 다른 물질)

    @Column(nullable = false, length = 500)
    private String inchi;   // 국제 화학 식별자

    @Column(nullable = false)
    private String inchiKey;    // InChI Key 값

    @Column(length = 10000)
    private String synonyms;    // 비슷한 화합물들

    @Column(length = 5000)
    private String description; // 화합물에 대한 설명

    @Column
    private String image2DUrl;   // 2D 이미지 경로

    @Column
    private String image3DUrl;   // 3D 이미지 경로

    @Builder
    public ChemInfo(Long id, Long cid, String inpacName, String molecularFormula, Double molecularWeight, String isomericSmiles, String inchi, String inchiKey, String canonicalSmiles, String synonyms, String description, String image2DUrl, String image3DUrl) {
        this.id = id;
        this.cid = cid;
        this.inpacName = inpacName;
        this.molecularFormula = molecularFormula;
        this.molecularWeight = molecularWeight;
        this.isomericSmiles = isomericSmiles;
        this.inchi = inchi;
        this.inchiKey = inchiKey;
        this.canonicalSmiles = canonicalSmiles;
        this.synonyms = synonyms;
        this.description = description;
        this.image2DUrl = image2DUrl;
        this.image3DUrl = image3DUrl;
    }
}
