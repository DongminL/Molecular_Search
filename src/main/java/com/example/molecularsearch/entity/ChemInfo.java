package com.example.molecularsearch.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document("ChemInfo")
public class ChemInfo {

    @Id
    private String id;    // ChemInfo PK

    @Field
    private Long cid;   // 분자 고유 ID

    private String inpacName;   // 유기 화합물 이름

    @Indexed(name = "index_formula",unique = false) // Index 생성
    private String molecularFormula; // 화학식

    private Double molecularWeight; // 분자량 (g/mol)

    private String canonicalSmiles; // 표준 SMILES

    @Indexed(name = "index_smiles", unique = true)  // Unique Index 생성
    private String isomericSmiles;  // 이성질체 SMILES (이성질체 : 분자식은 같지만 분자 구조가 다른 물질)

    private String inchi;   // 국제 화학 식별자

    private String inchiKey;    // InChI Key 값

    private String description; // 화합물에 대한 설명

    private String image2DUrl;   // 2D 이미지 경로

    private String image3DUrl;   // 3D 이미지 경로

    private List<String> synonyms;    // 상위 5개의 synonyms 값
}
