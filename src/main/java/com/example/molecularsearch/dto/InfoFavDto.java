package com.example.molecularsearch.dto;

import com.example.molecularsearch.entity.InfoFav;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfoFavDto {

    @JsonProperty("id")
    private Long id;    // InfoFav PK

    @JsonProperty("chem_id")
    private String chemInfo;  // ChemInfo FK

    @JsonProperty("molecular_formula")
    private String molecularFormula;    // 분자식

    /* Entity -> Dto */
    public InfoFavDto toDto(InfoFav entity) {
        return InfoFavDto.builder()
                .id(entity.getId())
                .chemInfo(entity.getChemInfoId())
                .molecularFormula(entity.getMolecularFormula())
                .build();
    }
}
