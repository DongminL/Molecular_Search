package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.ChemInfoDto;
import com.example.molecularsearch.dto.ConformerResponse;
import com.example.molecularsearch.dto.DescriptionResponse;
import com.example.molecularsearch.dto.SynonymsResponse;
import com.example.molecularsearch.exception.CustomException;
import com.example.molecularsearch.exception.ErrorCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple5;

import java.time.LocalDateTime;

@Slf4j
@Component
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class GenericWebclient<T> {

    @Value("${fastapi.search.smiles.info}")
    private String SMILES_INFO_URL;   // SMILES 식으로 분자 정보 검색 API URL

    @Value("${fastapi.search.cid.info}")
    private String CID_INFO_URL;    // cid로 분자 정보 검색 API URL

    @Value("${pubchem.search.cid}")
    private String PUBCHEM_CID_URL;

    @Value("${pubchem.search.smiles}")
    private String PUBCHEM_SMILES_URL;

    @Value("${pubchem.sysnonyms}")
    private String REQUEST_SYNONYMS;

    @Value("${pubchem.description}")
    private String REQUEST_DESCRIPTION;

    private final WebClient webClient = WebClient.create();
    private final AwsS3Service awsS3Service;

    /* 여러 API에서 비동기 처리로 분자 정보 가져오기 */
    public ChemInfoDto requestInfo(T keyword) {
        Mono<ChemInfoDto> chemInfoMono = getChemInfo(keyword); // 분자 정보
        Mono<SynonymsResponse> synonymsMono = getSynonyms(keyword);   // Synonyms
        Mono<DescriptionResponse> descriptionMono = getDescription(keyword); // Description
        Mono<InputStreamResource> imageMono = getImage(keyword); // 2D Image
        Mono<ConformerResponse> conformerMono = getConformers(keyword);    // 3D Image Conformer
        ChemInfoDto chemInfoDto;

        try {
            Tuple5<ChemInfoDto, SynonymsResponse, DescriptionResponse, InputStreamResource, ConformerResponse> tuple5 = Mono.zip(chemInfoMono, synonymsMono, descriptionMono, imageMono, conformerMono).block();    // 다섯 개의 Mono 결과값을 묶어서 동기적으로 처리

            chemInfoDto = tuple5.getT1();

            // AWS S3에 이미지 저장 후 해당 이미지의 url 가져오기
            byte[] bytes = tuple5.getT4().getInputStream().readAllBytes();  // Image Byte[]
            if (bytes.length > 0) {
                chemInfoDto.update2DImage(awsS3Service.saveImage(chemInfoDto.getCid(), bytes));
            }
            if (!(tuple5.getT5().getCompounds().isEmpty())) {
                chemInfoDto.update3DImage(tuple5.getT5());
                log.debug("3D Conformer : " + chemInfoDto.getImage3DConformer().toString());
            }
            if (tuple5.getT2().getInformationList() != null) {
                chemInfoDto.updateSynonyms(tuple5.getT2().getSynonyms());
            }
            chemInfoDto.updateDescription(tuple5.getT3().getDescription());

            log.debug("Image URL: {}, timestemp: {}",chemInfoDto.getImage2DUrl(), LocalDateTime.now());
        } catch (WebClientRequestException e) {
            log.error("WebClient 요청 에러 : {}, timestemp: {}", e, LocalDateTime.now());
            throw new CustomException(ErrorCode.EXTERNAL_API_REQUEST_FAILED);
        } catch (CustomException e) {
            log.error("이미지 저장 실패, timestemp: {}", LocalDateTime.now());
            throw new CustomException(ErrorCode.EXTERNAL_API_REQUEST_FAILED);
        } catch (Exception e) {
            log.error("API 요청 실패 : {}, timestemp: {}", e, LocalDateTime.now());
            throw new CustomException(ErrorCode.EXTERNAL_API_REQUEST_FAILED);
        }

        return chemInfoDto;
    }

    /* FAST API에서 분자정보 가져오기 */
    public Mono<ChemInfoDto> getChemInfo(T keyword) {
        // 타입에 따라 URL 및 Parameter 변경
        if (keyword instanceof Long) {
            return webClient.mutate()
                    .baseUrl(CID_INFO_URL).build()
                    .get()    // GET 요청
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("mol_Cid", keyword)   // Params 설정
                            .build())
                    .retrieve() // 응답값을 가져옴
                    .bodyToMono(ChemInfoDto.class)  // 응답값을 ChemInfoDto로 역직렬화
                    .onErrorMap(e -> {
                        log.error("Fast API 요청 실패 : {}, timestemp: {}", e, LocalDateTime.now());
                        throw new CustomException(ErrorCode.EXTERNAL_API_REQUEST_FAILED);
                    });
        }

        return webClient.mutate()
                .baseUrl(SMILES_INFO_URL).build()
                .get()    // GET 요청
                .uri(uriBuilder -> uriBuilder
                        .queryParam("mol_Smlies", keyword)   // Params 설정
                        .build())
                .retrieve() // 응답값을 가져옴
                .bodyToMono(ChemInfoDto.class)  // 응답값을 ChemInfoDto로 역직렬화
                .onErrorMap(e -> {
                    log.error("Fast API 요청 실패 : {}, timestemp: {}", e, LocalDateTime.now());
                    throw new CustomException(ErrorCode.EXTERNAL_API_REQUEST_FAILED);
                });
    }

    /* PubChem에서 Synonyms 값 가져오기 */
    public Mono<SynonymsResponse> getSynonyms(T keyword) {
        String baseUrl;

        // 타입에 따라 URL 변경
        if (keyword instanceof Long) {
            baseUrl = PUBCHEM_CID_URL + keyword + REQUEST_SYNONYMS;
        } else {
            baseUrl = PUBCHEM_SMILES_URL + keyword + REQUEST_SYNONYMS;
        }

        return webClient.mutate()
                .baseUrl(baseUrl).build()
                .get()    // GET 요청
                .retrieve() // 응답값을 가져옴
                // 400번대 에러 시
                .onStatus(HttpStatusCode::is4xxClientError, e -> {
                    throw new RuntimeException("Synonyms_Not_Found");
                })
                .bodyToMono(SynonymsResponse.class)  // 응답값을 SynonymsResponse로 역직렬화
                .onErrorResume(e -> {
                    // 400번대 에러 처리
                    if (e instanceof RuntimeException) {
                        return Mono.just(new SynonymsResponse());
                    }

                    // 그 외 Exception 에러 처리
                    log.error("Synonyms 요청 에러 : {}, timestemp: {}", e, LocalDateTime.now());
                    throw new CustomException(ErrorCode.EXTERNAL_API_REQUEST_FAILED);
                });
    }

    /* PubChem에서 Description 값 가져오기 */
    public Mono<DescriptionResponse> getDescription(T keyword) {
        String baseUrl;

        // 타입에 따라 URL 변경
        if (keyword instanceof Long) {
            baseUrl = PUBCHEM_CID_URL + keyword + REQUEST_DESCRIPTION;
        } else {
            baseUrl = PUBCHEM_SMILES_URL + keyword + REQUEST_DESCRIPTION;
        }

        return webClient.mutate()
                .baseUrl(baseUrl).build()
                .get()    // GET 요청
                .retrieve() // 응답값을 가져옴
                .bodyToMono(DescriptionResponse.class)  // 응답값을 DescriptionResponse로 역직렬화
                .onErrorResume(e -> {
                   log.error("Description 요청 에러: {}, timestemp: {}", e, LocalDateTime.now());
                   throw new CustomException(ErrorCode.EXTERNAL_API_REQUEST_FAILED);
                });
    }

    /* PubChem에서 png 파일 가져오기 */
    public Mono<InputStreamResource> getImage(T keyword) {
        String baseUrl;

        // 타입에 따라 URL 변경
        if (keyword instanceof Long) {
            baseUrl = PUBCHEM_CID_URL + keyword + "/png";
        } else {
            baseUrl = PUBCHEM_SMILES_URL + keyword + "/png";
        }

        return webClient.mutate()
                .baseUrl(baseUrl).build()
                .get()    // GET 요청
                .accept(MediaType.IMAGE_PNG)    // 가져오려는 Content Type 명시
                .retrieve() // 응답값을 가져옴
                .onStatus(HttpStatusCode::is4xxClientError, e -> {
                    throw new CustomException(ErrorCode.NOT_FOUND_CHEM_INFO);
                })
                .bodyToMono(new ParameterizedTypeReference<InputStreamResource>() {
                });   // Chunk 단위로 이미지를 읽어옮
    }

    /* PubChem에서 3D Conformer 가져오기 */
    public Mono<ConformerResponse> getConformers(T keyword) {
        String baseUrl;

        // 타입에 따라 URL 변경
        if (keyword instanceof Long) {
            baseUrl = PUBCHEM_CID_URL + keyword + "/json";
        } else {
            baseUrl = PUBCHEM_SMILES_URL + keyword + "/json";
        }

        return webClient.mutate()
                .baseUrl(baseUrl).build()
                .get()    // GET 요청
                .uri(uriBuilder -> uriBuilder
                        .queryParam("record_type", "3d")
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, e -> {
                    throw new RuntimeException("404 에러");
                })
                .bodyToMono(ConformerResponse.class)
                .onErrorResume(e -> {
                    if (e instanceof RuntimeException) {
                        return Mono.just(new ConformerResponse());
                    }

                    log.error("3D Conformer 에러: {}, timestemp: {}", e, LocalDateTime.now());
                    throw new CustomException(ErrorCode.EXTERNAL_API_REQUEST_FAILED);
                });
    }
}
