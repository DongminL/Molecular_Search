package com.example.molecularsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Builder
@Document("ChemInfo")
@NoArgsConstructor
@AllArgsConstructor
public class MongoTest {

    @Id
    private String id;    // ChemInfo PK

    private Long cid;   // 분자 고유 ID

    private String inpacName;   // 유기 화합물 이름

    private String molecularFormula; // 화학식

    private Double molecularWeight; // 분자량 (g/mol)

    private String canonicalSmiles; // 표준 SMILES

    @Indexed
    private String isomericSmiles;  // 이성질체 SMILES (이성질체 : 분자식은 같지만 분자 구조가 다른 물질)

    private String inchi;   // 국제 화학 식별자

    private String inchiKey;    // InChI Key 값

    private List<String> synonyms;    // 상위 5개의 synonyms 값

    private String description; // 화합물에 대한 설명

    private String image2DUrl;   // 2D 이미지 경로

    private String image3DUrl;   // 3D 이미지 경로
}
