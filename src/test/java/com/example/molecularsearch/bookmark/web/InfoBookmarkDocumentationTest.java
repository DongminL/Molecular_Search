package com.example.molecularsearch.bookmark.web;

import com.example.molecularsearch.bookmark.service.InfoBookmarkService;
import com.example.molecularsearch.bookmark.web.dto.BookmarkListDto;
import com.example.molecularsearch.bookmark.web.dto.InfoBookmarkDto;
import com.example.molecularsearch.common.anotation.WithMockCustomUser;
import com.example.molecularsearch.common.documentation.RestDocsSetting;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.molecularsearch.common.documentation.RestDocsFromatGenerator.tokenFormat;
import static org.mockito.ArgumentMatchers.anyString;
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

@WebMvcTest(InfoBookmarkController.class)
class InfoBookmarkDocumentationTest extends RestDocsSetting {

    @MockBean
    private InfoBookmarkService infoBookmarkService;

    @Test
    @DisplayName("즐겨찾기 추가")
    @WithMockCustomUser
    void addInfoBookmark() throws Exception {
        // when
        String chemId = "6646d897d7e4fb17f5ee2362";
        String bearerToken = "Bearer Json Web Token";

        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/save/bookmark/{chemId}", chemId)
                        .header("Authorization", bearerToken)
        );

        doNothing().when(infoBookmarkService).saveInfoBookmark(chemId);

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("즐겨찾기 추가 성공"))
                .andDo(document("add-bookmark",
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
                                parameterWithName("chemId").attributes(key("type").value("String")).description("즐겨찾기 하려는 분자의 ID(PK)")
                        )
                ));
    }

    @Test
    @DisplayName("즐겨찾기 해제")
    @WithMockCustomUser
    void offInfoBookmark() throws Exception {
        // when
        String chemId = "6646d897d7e4fb17f5ee2362";
        String bearerToken = "Bearer Json Web Token";

        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/off/bookmark/{chemId}", chemId)
                        .header("Authorization", bearerToken)
        );

        doNothing().when(infoBookmarkService).deleteInfoBookmark(chemId);

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("즐겨찾기 해제 완료"))
                .andDo(document("off-bookmark",
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
                                parameterWithName("chemId").attributes(key("type").value("String")).description("즐겨찾기 해제하려는 분자의 ID(PK)")
                        )
                ));
    }

    @Test
    @DisplayName("즐겨찾기 리스트 가져오기")
    @WithMockCustomUser
    void showInfoBookmark() throws Exception {
        // given
        BookmarkListDto response = objectMapper.readValue(new ClassPathResource("json/searchBookmarkList.json").getFile(),
                BookmarkListDto.class);

        given(infoBookmarkService.getBookmarkList()).willReturn(response);

        // when
        String bearerToken = "Bearer Json Web Token";

        ResultActions result = mockMvc.perform(
                get("/api/search/bookmark")
                        .header("Authorization", bearerToken)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookmarkList[1].chem_id").value(response.getBookmarkDtoList().get(1).getChemInfo()))
                .andDo(document("search-bookmark",
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
                                fieldWithPath("bookmarkList[]").description("즐겨찾기 검색 결과 목록"),
                                fieldWithPath("bookmarkList[].id").description("DB에 저장된 즐겨찾기 ID(PK)"),
                                fieldWithPath("bookmarkList[].chem_id").description("DB에 저장된 분자 정보 ID(PK)"),
                                fieldWithPath("bookmarkList[].molecular_formula").description("화학식")
                        )
                ));
    }

    @Test
    @DisplayName("즐겨찾기 단일 삭제")
    @WithMockCustomUser
    void deleteInfoBookmark() throws Exception {
        // when
        Long infoBookmarkId = 5L;
        String bearerToken = "Bearer Json Web Token";

        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/delete/bookmark/{infoBookmarkId}", infoBookmarkId)
                        .header("Authorization", bearerToken)
        );

        doNothing().when(infoBookmarkService).deleteInfoBookmark(infoBookmarkId);

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("즐겨찾기 삭제 완료"))
                .andDo(document("remove-one-bookmark",
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
                                parameterWithName("infoBookmarkId").attributes(key("type").value("Number")).description("삭제하려는 즐겨찾기의 ID(PK)")
                        )
                ));
    }

    @Test
    @DisplayName("즐겨찾기 편집을 통해 삭제")
    @WithMockCustomUser
    void editInfoBookmark() throws Exception {
        // given
        List<InfoBookmarkDto> deleteList = new ArrayList<>();
        deleteList.add(InfoBookmarkDto.builder()
                        .id(3L)
                        .chemInfo("6646d68fd7e4fb17f5ee2039")
                        .molecularFormula("CH4")
                        .build());
        deleteList.add(InfoBookmarkDto.builder()
                        .id(4L)
                        .chemInfo("6646d897d7e4fb17f5ee2362")
                        .molecularFormula("C3H6O4")
                        .build());

        BookmarkListDto request = BookmarkListDto.builder().bookmarkDtoList(deleteList).build();

        // when
        String bearerToken = "Bearer Json Web Token";

        ResultActions result = mockMvc.perform(
                delete("/api/edit/bookmark")
                        .header("Authorization", bearerToken)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        doNothing().when(infoBookmarkService).editInfoBookmark(request.getBookmarkDtoList());

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("즐겨찾기 삭제 완료"))
                .andDo(document("edit-bookmark",
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
                                fieldWithPath("bookmarkList[]").description("즐겨찾기 검색 결과 목록"),
                                fieldWithPath("bookmarkList[].id").description("DB에 저장된 즐겨찾기 ID(PK)"),
                                fieldWithPath("bookmarkList[].chem_id").description("DB에 저장된 분자 정보 ID(PK)"),
                                fieldWithPath("bookmarkList[].molecular_formula").description("화학식")
                        )
                ));
    }

    @Test
    @DisplayName("즐겨찾기 전체 삭제")
    @WithMockCustomUser
    void deleteAllInfoBookmark() throws Exception {
        // when
        String bearerToken = "Bearer Json Web Token";

        ResultActions result = mockMvc.perform(
                delete("/api/delete/all/bookmark")
                        .header("Authorization", bearerToken)
        );

        doNothing().when(infoBookmarkService).deleteAllInfoBookmark();

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("즐겨찾기 삭제 완료"))
                .andDo(document("remove-all-bookmark",
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

    @Test
    @DisplayName("즐겨찾기 여부 확인")
    @WithMockCustomUser
    void checkInfoBookmark() throws Exception {
        // given
        Map<String, Boolean> response = new HashMap<>();
        response.put("state", Boolean.TRUE);

        given(infoBookmarkService.checkInfoBookmark(anyString())).willReturn(response);

        // when
        String chemId = "6646d98fd7e4fb17f5ee24d";
        String bearerToken = "Bearer Json Web Token";

        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/check/bookmark/{chemId}", chemId)
                        .header("Authorization", bearerToken)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.state").value(Boolean.TRUE))
                .andDo(document("check-bookmark",
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
                                parameterWithName("chemId").attributes(key("type").value("String")).description("즐겨찾기 여부를 확인하려는 분자 정보의 ID(PK)")
                        ),
                        // 응답 값 설명
                        responseFields(
                                fieldWithPath("state").description("유저의 해당 분자 정보 즐겨찾기 여부")
                        )
                ));
    }
}