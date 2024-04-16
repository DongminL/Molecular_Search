package com.example.molecularsearch.dto;

import com.example.molecularsearch.entity.InfoFav;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfoFavDto {

    @JsonProperty
    private Long id;    // InfoFav PK

    @JsonProperty
    private Long chemInfo;  // ChemInfo FK

    @JsonProperty
    private String molecularFormula;    // 분자식

    @Builder
    public InfoFavDto(Long id, Long chemInfo, String molecularFormula) {
        this.id = id;
        this.chemInfo = chemInfo;
        this.molecularFormula = molecularFormula;
    }

    /* Entity -> Dto */
    public InfoFavDto toDto(InfoFav entity) {
        return InfoFavDto.builder()
                .id(entity.getId())
                .chemInfo(entity.getChemInfo().getId())
                .molecularFormula(entity.getMolecularFormula())
                .build();
    }
}
