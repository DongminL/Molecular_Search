package com.example.molecularsearch.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchResultDto {

    private Long totalElements; // 검색 결과의 총 개수
    private Integer totalPages; // 전체 검색 페이지 수
    private Integer pageNumber; // 현재 페이지 번호
    private Integer pageSize;   // 페이지 당 크기
    private List<ChemInfoDto> searchResults;    // 검색 결과 리스트
}
