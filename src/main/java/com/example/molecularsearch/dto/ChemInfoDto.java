package com.example.molecularsearch.dto;

import com.example.molecularsearch.entity.ChemInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ChemInfoDto {

    @JsonProperty("cid")
    private Long cid;   // 분자 고유 ID

    @JsonProperty("inpac_name")
    private String inpacName;   // 유기 화합물 이름

    @JsonProperty("molecular_formula")
    private String molecularFormula; // 화학식

    @JsonProperty("molecular_weight")
    private Double molecularWeight; // 분자량 (g/mol)

    @JsonProperty("isomeric_smlies")
    private String isomericSmiles;  // 이성질체 SMILES

    @JsonProperty("inchi")
    private String inchi;   // 국제 화학 식별자

    @JsonProperty("inchiKey")
    private String inchiKey;    // InChI Key 값

    @JsonProperty("canonical_smlies")
    private String canonicalSmiles; // 표준 SMILES

    @Builder
    public ChemInfoDto(Long cid, String inpacName, String molecularFormula, Double molecularWeight, String isomericSmiles, String inchi, String inchiKey, String canonicalSmiles) {
        this.cid = cid;
        this.inpacName = inpacName;
        this.molecularFormula = molecularFormula;
        this.molecularWeight = molecularWeight;
        this.isomericSmiles = isomericSmiles;
        this.inchi = inchi;
        this.inchiKey = inchiKey;
        this.canonicalSmiles = canonicalSmiles;
    }

    /* Dto -> Entity로 변환 */
    public ChemInfo toEntity() {
        return ChemInfo.builder()
                .cid(this.getCid())
                .inpacName(this.getInpacName())
                .molecularFormula(this.getMolecularFormula())
                .molecularWeight(this.getMolecularWeight())
                .inchi(this.getInchi())
                .inchiKey(this.getInchiKey())
                .canonicalSmiles(this.getCanonicalSmiles())
                .isomericSmiles(this.getIsomericSmiles())
                .build();
    }
}
