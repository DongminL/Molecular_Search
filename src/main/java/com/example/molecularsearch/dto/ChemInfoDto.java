package com.example.molecularsearch.dto;

import com.example.molecularsearch.entity.ChemInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Deserialize를 필요한 필드만 적용
public class ChemInfoDto {

    @JsonProperty("id")
    private String id;    // ChemInfo PK

    @JsonProperty("cid")
    private Long cid;   // 분자 고유 ID

    @JsonProperty("inpac_name")
    private String inpacName;   // 유기 화합물 이름

    @JsonProperty("molecular_formula")
    private String molecularFormula; // 화학식

    @JsonProperty("molecular_weight")
    private Double molecularWeight; // 분자량 (g/mol)

    @JsonProperty("isomeric_smiles")
    private String isomericSmiles;  // 이성질체 SMILES

    @JsonProperty("inchi")
    private String inchi;   // 국제 화학 식별자

    @JsonProperty("inchiKey")
    private String inchiKey;    // InChI Key 값

    @JsonProperty("canonical_smiles")
    private String canonicalSmiles; // 표준 SMILES

    @JsonProperty("synonyms")
    private List<String> synonyms;    // 비슷한 화합물들

    @JsonProperty("description")
    private String description; // 화합물에 대한 설명

    @JsonProperty("image_2D_url")
    private String image2DUrl;   // 2D 이미지 경로

    @JsonProperty("image_3D_url")
    private String image3DUrl;   // 3D 이미지 경로

    /* Dto -> Entity로 변환 */
    public ChemInfo toEntity() {
        return ChemInfo.builder()
                .cid(this.cid)
                .inpacName(this.inpacName)
                .molecularFormula(this.molecularFormula)
                .molecularWeight(this.molecularWeight)
                .inchi(this.inchi)
                .inchiKey(this.inchiKey)
                .canonicalSmiles(this.canonicalSmiles)
                .isomericSmiles(this.isomericSmiles)
                .description(this.description)
                .build();
    }

    /* Dto -> Entity로 변환 */
    public ChemInfo toEntity(List<String> synonyms) {
        return ChemInfo.builder()
                .cid(this.cid)
                .inpacName(this.inpacName)
                .molecularFormula(this.molecularFormula)
                .molecularWeight(this.molecularWeight)
                .inchi(this.inchi)
                .inchiKey(this.inchiKey)
                .canonicalSmiles(this.canonicalSmiles)
                .isomericSmiles(this.isomericSmiles)
                .synonyms(synonyms)
                .description(this.description)
                .build();
    }

    /* Entity -> Dto로 변환 */
    public ChemInfoDto(ChemInfo chemInfo, List<String> synonyms) {
        this.id = chemInfo.getId();
        this.cid = chemInfo.getCid();
        this.inpacName = chemInfo.getInpacName();
        this.molecularFormula = chemInfo.getMolecularFormula();
        this.molecularWeight = chemInfo.getMolecularWeight();
        this.isomericSmiles = chemInfo.getIsomericSmiles();
        this.inchi = chemInfo.getInchi();
        this.inchiKey = chemInfo.getInchiKey();
        this.canonicalSmiles = chemInfo.getCanonicalSmiles();
        this.synonyms = synonyms;
        this.description = chemInfo.getDescription();
        this.image2DUrl = chemInfo.getImage2DUrl();
        this.image3DUrl = chemInfo.getImage3DUrl();
    }

    /* Synonyms 정보 업데이트 */
    public void updateSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    /* Description 정보 업데이트 */
    public void updateDescription(String description) {
        this.description = description;
    }
}
