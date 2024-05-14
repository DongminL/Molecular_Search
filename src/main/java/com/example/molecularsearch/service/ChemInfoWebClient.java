package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.ChemInfoDto;
import com.example.molecularsearch.dto.ConformerResponse;
import com.example.molecularsearch.dto.DescriptionResponse;
import com.example.molecularsearch.dto.SynonymsResponse;
import com.example.molecularsearch.exception.CustomException;
import com.example.molecularsearch.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple4;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChemInfoWebClient {

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

    /* SMILES 식으로 여러 API에서 비동기 처리로 분자 정보 가져오기 */
    public ChemInfoDto requestInfoBySmiles(String smiles) {
        Mono<ChemInfoDto> chemInfoMono = getChemInfoBySmiles(smiles); // 분자 정보
        Mono<SynonymsResponse> synonymsMono = getSynonymsBySmiles(smiles);   // Synonyms
        Mono<DescriptionResponse> descriptionMono = getDescriptionBySmiles(smiles); // Description
        Mono<InputStreamResource> imageMono = getImageBySmiles(smiles); // 2D Image
        ChemInfoDto chemInfoDto;

        try {
            Tuple4<ChemInfoDto, SynonymsResponse, DescriptionResponse, InputStreamResource> tuple4 = Mono.zip(chemInfoMono, synonymsMono, descriptionMono, imageMono).block();    // 네 Mono의 결과값을 묶어서 동기적으로 처리

            chemInfoDto = tuple4.getT1();

            byte[] bytes = tuple4.getT4().getInputStream().readAllBytes();  // Image Byte[]
            if (bytes.length > 0) {
                chemInfoDto.update2DImage(awsS3Service.saveImage(chemInfoDto.getCid(), bytes));
            }
            chemInfoDto.updateSynonyms(tuple4.getT2().getSynonyms());
            chemInfoDto.updateDescription(tuple4.getT3().getDescription());
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

    /* CID 값으로 여러 API에서 비동기 처리로 분자 정보 가져오기 */
    public ChemInfoDto requestInfoByCid(Long cid) {
        Mono<ChemInfoDto> chemInfoMono = getChemInfoByCid(cid); // 분자 정보
        Mono<SynonymsResponse> synonymsMono = getSynonymsByCid(cid);   // Synonyms
        Mono<DescriptionResponse> descriptionMono = getDescriptionByCid(cid);   // Description
        Mono<InputStreamResource> imageMono = getImageByCid(cid); // 2D Image
        ChemInfoDto chemInfoDto;

        try {
            Tuple4<ChemInfoDto, SynonymsResponse, DescriptionResponse, InputStreamResource> tuple4 = Mono.zip(chemInfoMono, synonymsMono, descriptionMono, imageMono).block();    // 네 Mono의 결과값을 묶어서 동기적으로 처리

            chemInfoDto = tuple4.getT1();

            byte[] bytes = tuple4.getT4().getInputStream().readAllBytes();  // Image Byte[]
            if (bytes.length > 0) {
                chemInfoDto.update2DImage(awsS3Service.saveImage(cid, bytes));
            }
            chemInfoDto.updateSynonyms(tuple4.getT2().getSynonyms());
            chemInfoDto.updateDescription(tuple4.getT3().getDescription());

            log.info(chemInfoDto.getImage2DUrl() + " : 생성");
        } catch (WebClientResponseException.NotFound e) {
            log.error(e.toString());

            Tuple2<ChemInfoDto, DescriptionResponse> tuple2 = Mono.zip(chemInfoMono, descriptionMono).block();    // 세 Mono의 결과값을 묶어서 동기적으로 처리

            chemInfoDto = tuple2.getT1();
            chemInfoDto.updateDescription(tuple2.getT2().getDescription());
        } catch (CustomException e) {
            log.error("이미지 저장 실패");
            throw new CustomException(ErrorCode.EXTERNAL_API_REQUEST_FAILED, "이미지 저장 실패");
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
                    .bodyToMono(ChemInfoDto.class);  // 응답값을 ChemInfoDto로 역직렬화
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
                    .bodyToMono(SynonymsResponse.class);  // 응답값을 SynonymsResponse로 역직렬화
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
                    .bodyToMono(DescriptionResponse.class);  // 응답값을 DescriptionResponse로 역직렬화
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }

    /* PubChem에서 SMILES에 대한 png 파일 가져오기 */
    public Mono<InputStreamResource> getImageBySmiles(String smiles) {
        try {
            return webClient.mutate()
                    .baseUrl(PUBCHEM_SMILES_URL + smiles + "/png").build()
                    .get()    // GET 요청
                    .accept(MediaType.IMAGE_PNG)    // 가져오려는 Content Type 명시
                    .retrieve() // 응답값을 가져옴
                    .bodyToMono(new ParameterizedTypeReference<InputStreamResource>() {
                    });   // Chunk 단위로 이미지를 읽어옮
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
                    .bodyToMono(ChemInfoDto.class);  // 응답값을 ChemInfoDto로 역직렬화
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
                    .bodyToMono(SynonymsResponse.class);  // 응답값을 SynonymsResponse로 역직렬화
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
                    .bodyToMono(DescriptionResponse.class);  // 응답값을 DescriptionResponse로 역직렬화
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }

    /* PubChem에서 CID에 대한 png 파일 가져오기 */
    public Mono<InputStreamResource> getImageByCid(Long cid) {
        try {
            return webClient.mutate()
                    .baseUrl(PUBCHEM_CID_URL + cid + "/png").build()
                    .get()    // GET 요청
                    .accept(MediaType.IMAGE_PNG)    // 가져오려는 Content Type 명시
                    .retrieve() // 응답값을 가져옴
                    .bodyToMono(new ParameterizedTypeReference<InputStreamResource>() {
                    });   // Chunk 단위로 이미지를 읽어옮
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }

    /* PubChem에서 CID에 대한 3D Conformers 가져오기 */
    public ConformerResponse getConformersByCid(Long cid) {
        try {
            return webClient.mutate()
                    .baseUrl(PUBCHEM_CID_URL + cid + "/json").build()
                    .get()    // GET 요청
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("record_type", "3d")
                            .build())
                    .retrieve()
                    .bodyToMono(ConformerResponse.class)
                    .block();
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }
}
