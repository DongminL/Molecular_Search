package com.example.molecularsearch.bookmark.web.dto;

import com.example.molecularsearch.bookmark.entity.InfoBookmark;
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
public class InfoBookmarkDto {

    @JsonProperty("id")
    private Long id;    // InfoFav PK

    @JsonProperty("chem_id")
    private String chemInfo;  // ChemInfo FK

    @JsonProperty("molecular_formula")
    private String molecularFormula;    // 분자식

    /* Entity -> Dto */
    public InfoBookmarkDto toDto(InfoBookmark entity) {
        return InfoBookmarkDto.builder()
                .id(entity.getId())
                .chemInfo(entity.getChemInfoId())
                .molecularFormula(entity.getMolecularFormula())
                .build();
    }
}
