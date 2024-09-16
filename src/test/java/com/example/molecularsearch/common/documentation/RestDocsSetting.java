package com.example.molecularsearch.common.documentation;

import com.example.molecularsearch.jwt.web.filter.JwtExceptionFilter;
import com.example.molecularsearch.jwt.web.filter.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@ExtendWith(MockitoExtension.class) // Mockito 사용
@AutoConfigureRestDocs  // REST Docs 사용
public class RestDocsSetting {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtExceptionFilter jwtExceptionFilter;

    /* MockMvc Rest Docs 초기 설정 */
    @BeforeEach
    void setUp(@Autowired RestDocumentationContextProvider restDocumentation,
               @Autowired WebApplicationContext webApplicationContext) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))   // REST Docs의 기본 설정 적용
                .build();
    }
}
