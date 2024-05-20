package com.example.molecularsearch.search_log.web.dto;

import com.example.molecularsearch.search_log.entity.SearchLog;
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
public class SearchLogDto {

    @JsonProperty("id")
    private Long id;    // SearchLog 테이블의 PK

    @JsonProperty("log")
    private String log; // 검색어

    /* Entity -> Dto */
    public SearchLogDto toDto(SearchLog entity) {
        return SearchLogDto.builder()
                .id(entity.getId())
                .log(entity.getLog())
                .build();
    }
}
