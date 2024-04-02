package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.ChemInfoDto;
import com.example.molecularsearch.entity.ChemInfo;
import com.example.molecularsearch.repository.ChemInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChemInfoService {

    private final ChemInfoRepository chemInfoRepository;
    private final String BASE_URL = "http://localhost:8008/getMolInfo_Name";

    DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);  // URI 인코딩
    // HTTP 클라이언트
    private WebClient webClient = WebClient.builder()
            .baseUrl(BASE_URL)  // 기본 URL
            .uriBuilderFactory(factory) // URI 인코딩
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)  // Header의 Content_Type 부분 JSON 형식으로 명시
            .build();;    // 인스턴스 생성

    public ChemInfo getChemInfo(String name) {
        ChemInfoDto request = webClient.get()    // GET 요청
                .uri(uriBuilder -> uriBuilder
                        .queryParam("mol_Name", name)   // Params 설정
                        .build())
                .retrieve() // 응답값을 가져옴
                .bodyToMono(ChemInfoDto.class)  // 응답값을 ChemInfoDto로 직렬화
                .block();   //  동기 방식

        log.info(request.toString());

        return ChemInfo.builder()
                .cid(request.getCid())
                .inpacName(request.getInpacName())
                .molecularFormula(request.getMolecularFormula())
                .molecularWeight(request.getMolecularWeight())
                .inchi(request.getInchi())
                .inchiKey(request.getInchiKey())
                .canonicalSmiles(request.getCanonicalSmiles())
                .isomericSmiles(request.getIsomericSmiles())
                .build();
    }

}
