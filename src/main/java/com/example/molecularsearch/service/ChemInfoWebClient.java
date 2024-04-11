package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.ChemInfoDto;
import com.example.molecularsearch.dto.DescriptionResponse;
import com.example.molecularsearch.dto.SynonymsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

@Slf4j
@Component
public class ChemInfoWebClient {

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

    @Value("${fastapi.search.cid.info}")
    private String CID_INFO_URL;    // cid로 분자 정보 검색 API URL

    @Value("${pubchem.search.cid}")
    private String PUBCHEM_CID_URL;

    @Value("${pubchem.search.name}")
    private String PUBCHEM_NAME_URL;

    @Value("${pubchem.search.smiles}")
    private String PUBCHEM_SMILES_URL;

    @Value("${pubchem.sysnonyms}")
    private String REQUEST_SYNONYMS;

    @Value("${pubchem.description}")
    private String REQUEST_DESCRIPTION;

    /* 여러 API에서 비동기 처리로 분자 정보 가져오기 */
    public ChemInfoDto requestInfoBySmiles(String smiles) {
        Mono<ChemInfoDto> chemInfoMono = getChemInfoBySmiles(smiles); // 분자 정보
        Mono<SynonymsResponse> synonymsMono = getSynonymsBySmiles(smiles);   // Synonyms
        Mono<DescriptionResponse> descriptionMono = getDescriptionBySmiles(smiles); // Description
        ChemInfoDto chemInfoDto;

        try {
            Tuple3<ChemInfoDto, SynonymsResponse, DescriptionResponse> tuple3 = Mono.zip(chemInfoMono, synonymsMono, descriptionMono).block();    // 세 Mono의 결과값을 묶어서 동기적으로 처리

            chemInfoDto = tuple3.getT1();
            chemInfoDto.updateSynonyms(tuple3.getT2().getSynonyms());
            chemInfoDto.updateDescription(tuple3.getT3().getDescription());
        } catch (NullPointerException e) {
            throw new NullPointerException("요청 실패");
        } catch (WebClientResponseException.NotFound e) {
            log.error(e.toString());

            Tuple2<ChemInfoDto, DescriptionResponse> tuple2 = Mono.zip(chemInfoMono, descriptionMono).block();    // 세 Mono의 결과값을 묶어서 동기적으로 처리

            chemInfoDto = tuple2.getT1();
            chemInfoDto.updateDescription(tuple2.getT2().getDescription());
        }

        return chemInfoDto;
    }

    /* 여러 API에서 비동기 처리로 분자 정보 가져오기 */
    public ChemInfoDto requestInfoByCid(Long cid) {
        Mono<ChemInfoDto> chemInfoMono = getChemInfoByCid(cid); // 분자 정보
        Mono<SynonymsResponse> synonymsMono = getSynonymsByCid(cid);   // Synonyms
        Mono<DescriptionResponse> descriptionMono = getDescriptionByCid(cid);   // Description
        ChemInfoDto chemInfoDto;

        try {
            Tuple3<ChemInfoDto, SynonymsResponse, DescriptionResponse> tuple3 = Mono.zip(chemInfoMono, synonymsMono, descriptionMono).block();    // 세 Mono의 결과값을 묶어서 동기적으로 처리

            chemInfoDto = tuple3.getT1();
            chemInfoDto.updateSynonyms(tuple3.getT2().getSynonyms());
            log.info(tuple3.getT2().getSynonyms().toString());
            chemInfoDto.updateDescription(tuple3.getT3().getDescription());
        } catch (NullPointerException e) {
            throw new NullPointerException("요청 실패");
        } catch (WebClientResponseException.NotFound e) {
            log.error(e.toString());

            Tuple2<ChemInfoDto, DescriptionResponse> tuple2 = Mono.zip(chemInfoMono, descriptionMono).block();    // 세 Mono의 결과값을 묶어서 동기적으로 처리

            chemInfoDto = tuple2.getT1();
            chemInfoDto.updateDescription(tuple2.getT2().getDescription());
        }

        return chemInfoDto;
    }


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

    /* FAST API에 화합물 이름으로 분자정보 가져오기 */
    public ChemInfoDto getChemInfoByName(String name) {
        WebClient webClient = createWebclient(NAME_INFO_URL);

        ChemInfoDto request = webClient.get()    // GET 요청
                .uri(uriBuilder -> uriBuilder
                        .queryParam("mol_Name", name)   // Params 설정
                        .build())
                .retrieve() // 응답값을 가져옴
                .bodyToMono(ChemInfoDto.class)  // 응답값을 ChemInfoDto로 직렬화
                .block();   //  동기 방식

        assert request != null : "찾지 못했습니다!";
        log.info(request.toString());
        return request;
    }

    /* FAST API에 SMILES 식으로 분자정보 가져오기 */
    public Mono<ChemInfoDto> getChemInfoBySmiles(String smiles) {
        WebClient webClient = createWebclient(SMILES_INFO_URL);

        try {
            return webClient.get()    // GET 요청
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("mol_Smlies", smiles)   // Params 설정
                            .build())
                    .retrieve() // 응답값을 가져옴
                    .bodyToMono(ChemInfoDto.class);  // 응답값을 ChemInfoDto로 직렬화
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }

    /* PubChem에서 SMILES 식에 대한 Synonyms 값 가져오기 */
    public Mono<SynonymsResponse> getSynonymsBySmiles(String smiles) {
        WebClient webClient = createWebclient(PUBCHEM_SMILES_URL + smiles + REQUEST_SYNONYMS);

        try {
            return webClient.get()    // GET 요청
                    .retrieve() // 응답값을 가져옴
                    .bodyToMono(SynonymsResponse.class);  // 응답값을 ChemInfoDto로 직렬화
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }

    /* PubChem에서 SMILES에 대한 Description 값 가져오기 */
    public Mono<DescriptionResponse> getDescriptionBySmiles(String smiles) {
        WebClient webClient = createWebclient(PUBCHEM_SMILES_URL + smiles + REQUEST_DESCRIPTION);

        try {
            return webClient.get()    // GET 요청
                    .retrieve() // 응답값을 가져옴
                    .bodyToMono(DescriptionResponse.class);  // 응답값을 ChemInfoDto로 직렬화
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }

    /* FAST API에 CID로 분자정보 가져오기 */
    public Mono<ChemInfoDto> getChemInfoByCid(Long cid) {
        WebClient webClient = createWebclient(CID_INFO_URL);

        try {
            return webClient.get()    // GET 요청
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("mol_Cid", cid)   // Params 설정
                            .build())
                    .retrieve() // 응답값을 가져옴
                    .bodyToMono(ChemInfoDto.class);  // 응답값을 ChemInfoDto로 직렬화
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }

    /* PubChem에서 CID에 대한 Synonyms 값 가져오기 */
    public Mono<SynonymsResponse> getSynonymsByCid(Long cid) {
        WebClient webClient = createWebclient(PUBCHEM_CID_URL + cid + REQUEST_SYNONYMS);

        try {
            return webClient.get()    // GET 요청
                    .retrieve() // 응답값을 가져옴
                    .bodyToMono(SynonymsResponse.class);  // 응답값을 ChemInfoDto로 직렬화
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }

    /* PubChem에서 CID에 대한 Description 값 가져오기 */
    public Mono<DescriptionResponse> getDescriptionByCid(Long cid) {
        WebClient webClient = createWebclient(PUBCHEM_CID_URL + cid + REQUEST_DESCRIPTION);

        try {
            return webClient.get()    // GET 요청
                    .retrieve() // 응답값을 가져옴
                    .bodyToMono(DescriptionResponse.class);  // 응답값을 ChemInfoDto로 직렬화
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }
}
