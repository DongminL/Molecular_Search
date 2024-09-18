package com.example.molecularsearch.oauth.web;

import com.example.molecularsearch.common.anotation.WithMockCustomUser;
import com.example.molecularsearch.common.documentation.RestDocsSetting;
import com.example.molecularsearch.jwt.service.JwtService;
import com.example.molecularsearch.jwt.web.dto.TokenResponse;
import com.example.molecularsearch.oauth.service.OAuthLoginService;
import com.example.molecularsearch.oauth.web.dto.GoogleUserDto;
import com.example.molecularsearch.oauth.web.dto.NaverUserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.example.molecularsearch.common.documentation.RestDocsFromatGenerator.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OAuthLoginController.class)
class OAuthLoginDocumentationTest extends RestDocsSetting {

    @MockBean
    private OAuthLoginService oAuthLoginService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("네이버 로그인")
    @WithMockCustomUser
    void loginNaver() throws Exception {
        // given
        TokenResponse response = TokenResponse.builder()
                .accessToken("accessToken")
                .grantType("Bearer")
                .expiredAt(3600000L)
                .build();

        given(oAuthLoginService.login(any(NaverUserDto.class))).willReturn(response);

        //when
        NaverUserDto request = NaverUserDto.builder()
                .userId("aaaaa111")
                .email("aaaaa111@gmail.com")
                .name("홍길동")
                .nickname("길동이")
                .gender("M")
                .mobile("010-0000-0000")
                .profileImage("https://profile-image.com")
                .build();

        ResultActions result = mockMvc.perform(
                post("/api/login/naver")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(response.getAccessToken()))
                .andDo(document("naver-login",
                        // JSON 값 예쁘게 출력
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 요청 값 설명
                        requestFields(
                            fieldWithPath("user_id").description("네이버 ID의 고유 값"),
                            fieldWithPath("email").attributes(emailFormat()).description("사용자 이메일"),
                            fieldWithPath("name").description("사용자 이름"),
                            fieldWithPath("nickname").description("닉네임"),
                            fieldWithPath("gender").description("성별"),
                            fieldWithPath("mobile").attributes(phoneNumberFormat()).description("휴대폰 번호"),
                            fieldWithPath("profile_image").attributes(imageUrlFormat()).description("프로필 사진 URL").optional()
                        ),
                        // 응답 값 설명
                        responseFields(
                            fieldWithPath("accessToken").description("액세스 토큰"),
                            fieldWithPath("grantType").description("토큰 타입"),
                            fieldWithPath("expiredAt").description("토큰 만료 기간 (ms)")
                        )
                ));
    }

    @Test
    @DisplayName("구글 로그인")
    void loginGoogle() throws Exception {
        // given
        TokenResponse response = TokenResponse.builder()
                .accessToken("accessToken")
                .grantType("Bearer")
                .expiredAt(3600000L)
                .build();

        given(oAuthLoginService.login(any(GoogleUserDto.class))).willReturn(response);

        //when
        GoogleUserDto request = GoogleUserDto.builder()
                .userId("aaaaa111")
                .email("aaaaa111@gmail.com")
                .name("홍길동")
                .profileImage("https://profile-image.com")
                .build();

        ResultActions result = mockMvc.perform(
                post("/api/login/google")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(response.getAccessToken()))
                .andDo(document("google-login",
                        // JSON 값 예쁘게 출력
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 요청 값 설명
                        requestFields(
                                fieldWithPath("user_id").description("구글 ID의 고유 값"),
                                fieldWithPath("email").attributes(emailFormat()).description("사용자 이메일"),
                                fieldWithPath("name").description("사용자 이름"),
                                fieldWithPath("photo_url").attributes(imageUrlFormat()).description("프로필 사진 URL").optional()
                        ),
                        // 응답 값 설명
                        responseFields(
                                fieldWithPath("accessToken").description("액세스 토큰"),
                                fieldWithPath("grantType").description("토큰 타입"),
                                fieldWithPath("expiredAt").description("토큰 만료 기간 (ms)")
                        )
                ));
    }

    @Test
    @DisplayName("토큰 갱신")
    void reissueToken() throws Exception {
        // given
        TokenResponse response = TokenResponse.builder()
                .accessToken("new Access Token")
                .grantType("Bearer")
                .expiredAt(3600000L)
                .build();

        given(jwtService.reissueAccessToken(anyString())).willReturn(response);

        // when
        String bearerToken = "Bearer Json Web Token";

        when(jwtService.getHeaderToken(anyString())).thenReturn(bearerToken.substring(7));

        ResultActions result = mockMvc.perform(
                patch("/api/login/reissue")
                        .header("Authorization", bearerToken)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(response.getAccessToken()))
                .andDo(document("token-reissue",
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
                                fieldWithPath("accessToken").description("액세스 토큰"),
                                fieldWithPath("grantType").description("토큰 타입"),
                                fieldWithPath("expiredAt").description("토큰 만료 기간 (ms)")
                        )
                ));
    }

    @Test
    @DisplayName("로그아웃")
    @WithMockCustomUser
    void logout() throws Exception {
        // given
        String bearerToken = "Bearer Json Web Token";

        given(jwtService.getHeaderToken(anyString())).willReturn("");

        // when
        ResultActions result = mockMvc.perform(
                delete("/api/logout")
                        .header("Authorization", bearerToken)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("로그아웃 성공"))
                .andDo(document("logout",
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