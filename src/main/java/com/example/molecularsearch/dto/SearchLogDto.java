package com.example.molecularsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchLogDto {

    @JsonProperty("id")
    private Long id;    // SearchLog 테이블의 PK

    @JsonProperty("log")
    private String log; // 검색어

    @Builder
    public SearchLogDto(Long id, String log) {
        this.id = id;
        this.log = log;
    }
}
