package com.example.molecularsearch.search_log.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchLogListDto {

    @JsonProperty("searchLogList")
    private List<SearchLogDto> searchLogDtoList;   // 검색 기록 목록
}
