package com.example.molecularsearch.chem_info.web;

import com.example.molecularsearch.chem_info.domain.ChemInfo;
import com.example.molecularsearch.chem_info.service.ChemInfoService;
import com.example.molecularsearch.chem_info.service.SynonymsService;
import com.example.molecularsearch.chem_info.web.dto.ChemInfoDto;
import com.example.molecularsearch.common.anotation.WithMockCustomUser;
import com.example.molecularsearch.common.documentation.RestDocsSetting;
import com.example.molecularsearch.search_log.service.SearchLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.molecularsearch.common.documentation.RestDocsFromatGenerator.smilesFormat;
import static com.example.molecularsearch.common.documentation.RestDocsFromatGenerator.tokenFormat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChemInfoController.class)
class ChemInfoDocumentationTest extends RestDocsSetting {

    @MockBean
    private ChemInfoService chemInfoService;

    @MockBean
    private SearchLogService searchLogService;

    @MockBean
    private SynonymsService synonymsService;

    @Test
    @DisplayName("키워드로 관련 분자 정보 검색")
    @WithMockCustomUser
    void seearchChem() throws Exception {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("SMILES로 해당 분자 정보 검색")
    @WithMockCustomUser
    void smilesChem() throws Exception {
        // given
        ChemInfoDto response = ChemInfoDto.builder()
                .id("6646d897d7e4fb17f5ee2362")
                .cid(752L)
                .inpacName("2,3-dihydroxypropanoic acid")
                .molecularFormula("C3H6O4")
                .molecularWeight(106.08)
                .inchi("InChI=1S/C3H6O4/c4-1-2(5)3(6)7/h2,4-5H,1H2,(H,6,7)")
                .inchiKey("RBNPOMFGQQGHHO-UHFFFAOYSA-N")
                .canonicalSmiles("C(C(C(=O)O)O)O")
                .isomericSmiles("C(C(C(=O)O)O)O")
                .description("Glyceric acid is a trionic acid that consists of propionic acid substituted at positions 2 and 3 by hydroxy groups. " +
                        "It has a role as a fundamental metabolite. " +
                        "It is functionally related to a propionic acid. " +
                        "It is a conjugate acid of a glycerate.")
                .synonyms(List.of("GLYCERIC ACID", "DL-Glyceric acid", "473-81-4", "2,3-Dihydroxypropanoic acid", "600-19-1"))
                .image2DUrl("https://chem-image.com")
                .build();

        given(chemInfoService.searchSmiles(any())).willReturn(response);

        // when
        String paramSmiles = "C(C(C(=O)O)O)O";
        String bearerToken = "Bearer Json Web Token";

        ResultActions result = mockMvc.perform(
                get("/api/search/chem")
                        .header("Authorization", bearerToken)
                        .param("smiles", paramSmiles)

        );

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isomeric_smiles").value(response.getIsomericSmiles()))
                .andDo(document("search-smiles",
                        // JSON 값 예쁘게 출력
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 요청 헤더 설명
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).attributes(key("type").value("String"))
                                        .attributes(tokenFormat()).description("JWT (Your Token)")
                        ),
                        // 요청 파라미터 설명
                        queryParameters(
                                parameterWithName("smiles").attributes(key("type").value("String"))
                                        .attributes(smilesFormat()).description("검색하려는 분자의 SMILES 식")
                        ),
                        // 응답 값 설명
                        responseFields(
                                fieldWithPath("id").description("DB에 저장된 ID(PK)"),
                                fieldWithPath("cid").description("분자 고유 번호"),
                                fieldWithPath("inpac_name").description("유기 화합물 이름"),
                                fieldWithPath("molecular_formula").description("화학식"),
                                fieldWithPath("molecular_weight").description("분자량 (g/mol)"),
                                fieldWithPath("inchi").description("국제 화학 식별자"),
                                fieldWithPath("inchi_key").description("InChI Key 값"),
                                fieldWithPath("canonical_smiles").description("표준 SMILES"),
                                fieldWithPath("isomeric_smiles").description("이성질체 SMILES"),
                                fieldWithPath("description").description("화합물에 대한 설명"),
                                fieldWithPath("synonyms").description("관련 단어 목록"),
                                fieldWithPath("image_2D_url").description("2D 이미지 경로")
                        )
                ));
    }

    @Test
    @DisplayName("CID 값으로 해당 분자 데이터 저장")
    @WithMockCustomUser
    void saveInfo() throws Exception {
        // given
        ChemInfo response = ChemInfo.builder()
                .id("6646d897d7e4fb17f5ee2362")
                .cid(752L)
                .inpacName("2,3-dihydroxypropanoic acid")
                .molecularFormula("C3H6O4")
                .molecularWeight(106.08)
                .inchi("InChI=1S/C3H6O4/c4-1-2(5)3(6)7/h2,4-5H,1H2,(H,6,7)")
                .inchiKey("RBNPOMFGQQGHHO-UHFFFAOYSA-N")
                .canonicalSmiles("C(C(C(=O)O)O)O")
                .isomericSmiles("C(C(C(=O)O)O)O")
                .description("Glyceric acid is a trionic acid that consists of propionic acid substituted at positions 2 and 3 by hydroxy groups. " +
                        "It has a role as a fundamental metabolite. " +
                        "It is functionally related to a propionic acid. " +
                        "It is a conjugate acid of a glycerate.")
                .synonyms(List.of("GLYCERIC ACID", "DL-Glyceric acid", "473-81-4", "2,3-Dihydroxypropanoic acid", "600-19-1"))
                .image2DUrl("https://chem-image.com")
                .build();

        given(chemInfoService.saveInfoByCid(any())).willReturn(response);

        // when
        String bearerToken = "Bearer Json Web Token";
        Map<String, Long> request = new HashMap<>();
        request.put("cid", 752L);

        ResultActions result = mockMvc.perform(
                post("/api/save/chem")
                        .header("Authorization", bearerToken)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isomericSmiles").value(response.getIsomericSmiles()))
                .andDo(document("save-chem",
                        // JSON 값 예쁘게 출력
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 요청 헤더 설명
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).attributes(key("type").value("String"))
                                        .attributes(tokenFormat()).description("JWT (Your Token)")
                        ),
                        // 요청 파라미터 설명
                        requestFields(
                                fieldWithPath("cid").description("저장하고 싶은 분자의 고유 번호")
                        ),
                        // 응답 값 설명
                        responseFields(
                                fieldWithPath("id").description("DB에 저장된 ID(PK)"),
                                fieldWithPath("cid").description("분자 고유 번호"),
                                fieldWithPath("inpacName").description("유기 화합물 이름"),
                                fieldWithPath("molecularFormula").description("화학식"),
                                fieldWithPath("molecularWeight").description("분자량 (g/mol)"),
                                fieldWithPath("inchi").description("국제 화학 식별자"),
                                fieldWithPath("inchiKey").description("InChI Key 값"),
                                fieldWithPath("canonicalSmiles").description("표준 SMILES"),
                                fieldWithPath("isomericSmiles").description("이성질체 SMILES"),
                                fieldWithPath("description").description("화합물에 대한 설명"),
                                fieldWithPath("synonyms").description("관련 단어 목록"),
                                fieldWithPath("image2DUrl").description("2D 이미지 경로")
                        )
                ));
    }

    @Test
    @DisplayName("분자 정보의 ID 값으로 검색")
    @WithMockCustomUser
    void idChem() throws Exception {
        // given

        // when

        // then

    }
}