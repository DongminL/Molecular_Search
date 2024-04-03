package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.ChemInfoDto;
import com.example.molecularsearch.entity.ChemInfo;
import com.example.molecularsearch.repository.ChemInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChemInfoService {

    @Value("${fastapi.search.name.info}")
    private String NAME_INFO_URL; // 화합물 이름으로 분자 정보 검색 API URL

    @Value("${fastapi.search.name.2d-image}")
    private String NAME_2D_URL;   // 화합물 이름으로 2D 이미지 검색 API URL

    @Value("${fastapi.search.name.3d-image}")
    private String NAME_3D_URL;   // 화합물 이름으로 3D 이미지 검색 API URL

    @Value("${fastapi.search.smiles.info}")
    private String SMILES_INFO_URL;   // SMILES 식으로 분자 정보 검색 API URL

    @Value("${fastapi.search.smiles.2d-image}")
    private String SMILES_2D_URL; // SMILES 식으로 2D 이미지 검색 API URL

    @Value("${fastapi.search.smiles.3d-image}")
    private String SMILES_3D_URL; // SMILES 식으로 3D 이미지 검색 API URL
    private final ChemInfoRepository chemInfoRepository;

    /* URL에 따른 WebClient 객체 생성 */
    public WebClient createWebclient(String url) {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(url);  // URI 인코딩
        // HTTP 클라이언트
        return WebClient.builder()
                .baseUrl(url)  // 기본 URL
                .uriBuilderFactory(factory) // URI 인코딩
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)  // Header의 Content_Type 부분 JSON 형식으로 명시
                .build();    // 인스턴스 생성
    }

    /* FAST API에 화합물 이름으로 분자정보 검색 */
    public ChemInfoDto getChemInfo(String name) {
        WebClient webClient = createWebclient(NAME_INFO_URL);

        ChemInfoDto request = webClient.get()    // GET 요청
                .uri(uriBuilder -> uriBuilder
                        .queryParam("mol_Name", name)   // Params 설정
                        .build())
                .retrieve() // 응답값을 가져옴
                .bodyToMono(ChemInfoDto.class)  // 응답값을 ChemInfoDto로 직렬화
                .block();   //  동기 방식

        assert request != null;
        log.info(request.toString());
        return request;
    }

    /* 응답값으로 받은 분자정보 저장 */
    @Transactional
    public ChemInfo saveChemInfo(ChemInfoDto request) {
        ChemInfo chemInfo = ChemInfo.builder()
                .cid(request.getCid())
                .inpacName(request.getInpacName())
                .molecularFormula(request.getMolecularFormula())
                .molecularWeight(request.getMolecularWeight())
                .inchi(request.getInchi())
                .inchiKey(request.getInchiKey())
                .canonicalSmiles(request.getCanonicalSmiles())
                .isomericSmiles(request.getIsomericSmiles())
                .build();

        return chemInfoRepository.save(chemInfo);
    }

    public ChemInfoDto searchChemInfo(String name) {
        return null;
    }
}
