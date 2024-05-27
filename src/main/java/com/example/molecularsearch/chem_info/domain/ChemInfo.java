package com.example.molecularsearch.chem_info.domain;

import com.example.molecularsearch.chem_info.web.dto.ChemInfoDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document("ChemInfo")
public class ChemInfo {

    @Id
    private String id;    // ChemInfo PK

    private Long cid;   // 분자 고유 ID

    private String inpacName;   // 유기 화합물 이름

    private String molecularFormula; // 화학식

    private Double molecularWeight; // 분자량 (g/mol)

    private String canonicalSmiles; // 표준 SMILES

    @Indexed(name = "index_smiles", unique = true)  // Unique Index 생성
    private String isomericSmiles;  // 이성질체 SMILES (이성질체 : 분자식은 같지만 분자 구조가 다른 물질)

    private String inchi;   // 국제 화학 식별자

    private String inchiKey;    // InChI Key 값

    private String description; // 화합물에 대한 설명

    private String image2DUrl;   // 2D 이미지 경로

    private Map<String, ArrayList<?>> image3DConformer;   // 3D 이미지 정보

    private List<String> synonyms;    // 상위 5개의 synonyms 값
    
    /* Dto로 변환 */
    public ChemInfoDto toDto() {
        return ChemInfoDto.builder()
                .id(this.id)
                .cid(this.cid)
                .description(this.description)
                .image2DUrl(this.image2DUrl)
                .image3DConformer(this.image3DConformer)
                .canonicalSmiles(this.canonicalSmiles)
                .inchi(this.inchi)
                .inchiKey(this.inchiKey)
                .isomericSmiles(this.isomericSmiles)
                .inpacName(this.inpacName)
                .molecularFormula(this.molecularFormula)
                .molecularWeight(this.molecularWeight)
                .synonyms(this.synonyms)
                .build();
    }
}
