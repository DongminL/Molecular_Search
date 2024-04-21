package com.example.molecularsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SearchResultDto {

    private Long totalElements;

    private Integer totalPages;

    private Integer pageNumber;

    private Integer pageSize;

    private List<ChemInfoDto> searchResults;

    public SearchResultDto() {
        searchResults = new ArrayList<>();
    }
}
