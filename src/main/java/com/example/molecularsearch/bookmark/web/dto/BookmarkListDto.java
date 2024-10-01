package com.example.molecularsearch.bookmark.web.dto;

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
public class BookmarkListDto {

    @JsonProperty("bookmarkList")
    private List<InfoBookmarkDto> bookmarkDtoList;  // 즐겨찾기 목록
}
