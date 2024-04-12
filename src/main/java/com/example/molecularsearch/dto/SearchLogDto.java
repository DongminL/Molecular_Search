package com.example.molecularsearch.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchLogDto {

    private Long id;    // SearchLog 테이블의 PK

    private String log; // 검색어

    @Builder
    public SearchLogDto(Long id, String log) {
        this.id = id;
        this.log = log;
    }
}
