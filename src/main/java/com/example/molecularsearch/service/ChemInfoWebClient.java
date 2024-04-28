package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.ChemInfoDto;
import com.example.molecularsearch.dto.DescriptionResponse;
import com.example.molecularsearch.dto.SynonymsResponse;
import com.example.molecularsearch.exception.CustomException;
import com.example.molecularsearch.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

@Slf4j
@Component
@RequiredArgsConstructor
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

    private final WebClient webClient = WebClient.create();

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
        } catch (WebClientResponseException.NotFound e) {
            log.error(e.toString());

            Tuple2<ChemInfoDto, DescriptionResponse> tuple2 = Mono.zip(chemInfoMono, descriptionMono).block();    // 세 Mono의 결과값을 묶어서 동기적으로 처리

            chemInfoDto = tuple2.getT1();
            chemInfoDto.updateDescription(tuple2.getT2().getDescription());
        } catch (Exception e) {
            log.error(e.toString());
            throw new CustomException(ErrorCode.EXTERNAL_API_REQUEST_FAILED);
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
            chemInfoDto.updateDescription(tuple3.getT3().getDescription());
        } catch (WebClientResponseException.NotFound e) {
            log.error(e.toString());

            Tuple2<ChemInfoDto, DescriptionResponse> tuple2 = Mono.zip(chemInfoMono, descriptionMono).block();    // 세 Mono의 결과값을 묶어서 동기적으로 처리

            chemInfoDto = tuple2.getT1();
            chemInfoDto.updateDescription(tuple2.getT2().getDescription());
        } catch (Exception e) {
            log.error(e.toString());
            throw new CustomException(ErrorCode.EXTERNAL_API_REQUEST_FAILED);
        }

        return chemInfoDto;
    }

    /* FAST API에 SMILES 식으로 분자정보 가져오기 */
    public Mono<ChemInfoDto> getChemInfoBySmiles(String smiles) {
        try {
            return webClient.mutate()
                    .baseUrl(SMILES_INFO_URL).build()
                    .get()    // GET 요청
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
        try {
            return webClient.mutate()
                    .baseUrl(PUBCHEM_SMILES_URL + smiles + REQUEST_SYNONYMS).build()
                    .get()    // GET 요청
                    .retrieve() // 응답값을 가져옴
                    .bodyToMono(SynonymsResponse.class);  // 응답값을 ChemInfoDto로 직렬화
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }

    /* PubChem에서 SMILES에 대한 Description 값 가져오기 */
    public Mono<DescriptionResponse> getDescriptionBySmiles(String smiles) {
        try {
            return webClient.mutate()
                    .baseUrl(PUBCHEM_SMILES_URL + smiles + REQUEST_DESCRIPTION).build()
                    .get()    // GET 요청
                    .retrieve() // 응답값을 가져옴
                    .bodyToMono(DescriptionResponse.class);  // 응답값을 ChemInfoDto로 직렬화
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }

    /* FAST API에 CID로 분자정보 가져오기 */
    public Mono<ChemInfoDto> getChemInfoByCid(Long cid) {
        try {
            return webClient.mutate()
                    .baseUrl(CID_INFO_URL).build()
                    .get()    // GET 요청
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
         try {
            return webClient.mutate()
                    .baseUrl(PUBCHEM_CID_URL + cid + REQUEST_SYNONYMS).build()
                    .get()    // GET 요청
                    .retrieve() // 응답값을 가져옴
                    .bodyToMono(SynonymsResponse.class);  // 응답값을 ChemInfoDto로 직렬화
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }

    /* PubChem에서 CID에 대한 Description 값 가져오기 */
    public Mono<DescriptionResponse> getDescriptionByCid(Long cid) {
        try {
            return webClient.mutate()
                    .baseUrl(PUBCHEM_CID_URL + cid + REQUEST_DESCRIPTION).build()
                    .get()    // GET 요청
                    .retrieve() // 응답값을 가져옴
                    .bodyToMono(DescriptionResponse.class);  // 응답값을 ChemInfoDto로 직렬화
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }
}
