package com.example.molecularsearch.search_log.web;

import com.example.molecularsearch.common.anotation.WithMockCustomUser;
import com.example.molecularsearch.common.documentation.RestDocsSetting;
import com.example.molecularsearch.search_log.service.SearchLogService;
import com.example.molecularsearch.search_log.web.dto.SearchLogDto;
import com.example.molecularsearch.search_log.web.dto.SearchLogListDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static com.example.molecularsearch.common.documentation.RestDocsFromatGenerator.tokenFormat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchLogController.class)
class SearchLogDocumentationTest extends RestDocsSetting {

    @MockBean
    private SearchLogService searchLogService;

    @Test
    @DisplayName("검색 기록 가져오기")
    @WithMockCustomUser
    void showSearchLog() throws Exception {
        // given
        SearchLogListDto response = objectMapper.readValue(new ClassPathResource("json/searchLogList.json").getFile(),
                SearchLogListDto.class);

        given(searchLogService.findSearchLog()).willReturn(response);

        // when
        String bearerToken = "Bearer Json Web Token";

        ResultActions result = mockMvc.perform(
                get("/api/get/log/search")
                        .header("Authorization", bearerToken)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.searchLogList[2].log").value(response.getSearchLogDtoList().get(2).getLog()))
                .andDo(document("search-log",
                        // JSON 값 예쁘게 출력
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 요청 헤더 설명
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).attributes(key("type").value("String"))
                                        .attributes(tokenFormat()).description("JWT (Your Token)")
                        ),
                        // 응답 값 설명
                        responseFields(
                                fieldWithPath("searchLogList[]").description("검색 결과 목록"),
                                fieldWithPath("searchLogList[].id").description("DB에 저장된 검색 기록 ID(PK)"),
                                fieldWithPath("searchLogList[].log").description("검색어")
                        )
                ));
    }

    @Test
    @DisplayName("검색 기록 단일 삭제")
    @WithMockCustomUser
    void removeSearchLog() throws Exception {
        // when
        Long searchLogId = 5L;
        String bearerToken = "Bearer Json Web Token";

        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/delete/log/search/{searchLogId}", searchLogId)
                        .header("Authorization", bearerToken)
        );

        doNothing().when(searchLogService).deleteSearchLog(searchLogId);

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("검색기록 삭제 완료"))
                .andDo(document("remove-one-log",
                        // JSON 값 예쁘게 출력
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 요청 헤더 설명
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).attributes(key("type").value("String"))
                                        .attributes(tokenFormat()).description("JWT (Your Token)")
                        ),
                        // 요청 파라미터 설명
                        pathParameters(
                                parameterWithName("searchLogId").attributes(key("type").value("Number")).description("삭제하려는 검색 기록의 ID(PK)")
                        )
                ));
    }

    @Test
    @DisplayName("검색 기록 편집을 통해 삭제")
    @WithMockCustomUser
    void editSearchLog() throws Exception {
        // given
        List<SearchLogDto> deleteList = new ArrayList<>();
        deleteList.add(SearchLogDto.builder()
                .id(3L)
                .log("CH4")
                .build());
        deleteList.add(SearchLogDto.builder()
                .id(4L)
                .log("C3H6O4")
                .build());

        SearchLogListDto request = SearchLogListDto.builder().searchLogDtoList(deleteList).build();

        // when
        String bearerToken = "Bearer Json Web Token";

        ResultActions result = mockMvc.perform(
                delete("/api/edit/log/search")
                        .header("Authorization", bearerToken)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        doNothing().when(searchLogService).editSearchLog(request.getSearchLogDtoList());

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("검색기록 삭제 완료"))
                .andDo(document("edit-log",
                        // JSON 값 예쁘게 출력
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 요청 헤더 설명
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).attributes(key("type").value("String"))
                                        .attributes(tokenFormat()).description("JWT (Your Token)")
                        ),
                        // 요청 값 설명
                        requestFields(
                                fieldWithPath("searchLogList[]").description("검색 결과 목록"),
                                fieldWithPath("searchLogList[].id").description("DB에 저장된 검색 기록 ID(PK)"),
                                fieldWithPath("searchLogList[].log").description("검색어")
                        )
                ));
    }

    @Test
    @DisplayName("검색 기록 전체 삭제 ")
    @WithMockCustomUser
    void deleteAllSearchLog() throws Exception {
        // when
        String bearerToken = "Bearer Json Web Token";

        ResultActions result = mockMvc.perform(
                delete("/api/delete/all/log/search")
                        .header("Authorization", bearerToken)
        );

        doNothing().when(searchLogService).deleteAllSearchLog();

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("검색기록 삭제 완료"))
                .andDo(document("remove-all-log",
                        // JSON 값 예쁘게 출력
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 요청 헤더 설명
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).attributes(key("type").value("String"))
                                        .attributes(tokenFormat()).description("JWT (Your Token)")
                        )
                ));
    }
}