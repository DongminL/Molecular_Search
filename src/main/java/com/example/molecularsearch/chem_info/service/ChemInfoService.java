package com.example.molecularsearch.chem_info.service;

import com.example.molecularsearch.chem_info.web.dto.ChemInfoDto;
import com.example.molecularsearch.chem_info.domain.ChemInfo;
import com.example.molecularsearch.exception.error.CustomException;
import com.example.molecularsearch.exception.error.ErrorCode;
import com.example.molecularsearch.chem_info.repository.ChemInfoRepository;
import com.example.molecularsearch.chem_info.web.api.GenericWebclient;
import com.example.molecularsearch.search_log.service.SearchLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChemInfoService {

    private final GenericWebclient<String> webclientBySmiles;
    private final GenericWebclient<Long> webclientByCid;
    private final ChemInfoRepository chemInfoRepository;
    private final SearchLogService searchLogService;
    private final SynonymsService synonymsService;

    /* SMILES 식으로 분자정보 검색 */
    public ChemInfoDto searchSmiles(String smiles) {
        ChemInfoDto chemInfoDto;
        ChemInfo entity = findChemInfoBySmiles(smiles); // SMILES 식으로 찾아 가져오기

        // DB에 해당 정보가 있으면
        if (entity != null) {
            chemInfoDto = new ChemInfoDto(entity, entity.getSynonyms());

            searchLogService.saveSearchLog(chemInfoDto.getMolecularFormula());   // 검색기록을 분자식으로 기록
            return chemInfoDto;
        }

        chemInfoDto = webclientBySmiles.requestInfo(smiles);  // Fast API로 요청하여 가져옴

        entity = saveChemInfo(chemInfoDto);  // 가져온 값 저장
        chemInfoDto = new ChemInfoDto(entity, entity.getSynonyms());

        searchLogService.saveSearchLog(chemInfoDto.getMolecularFormula());   // 검색기록을 분자식으로 기록

        return chemInfoDto;
    }

    /* CID 값으로 요청한 분자정보 저장*/
    public ChemInfo saveInfoByCid(Long cid) {
        ChemInfoDto chemInfoDto = webclientByCid.requestInfo(cid);

        return saveChemInfo(chemInfoDto);
    }

    /* 응답값으로 받은 분자정보 저장 */
    public ChemInfo saveChemInfo(ChemInfoDto response) {
        ChemInfo chemInfo;
        List<String> synonyms = response.getSynonyms();

        try {
            if (response.getId() != null) {
                throw new DuplicateKeyException("이미 저장된 데이터");
            }

            // Synonyms 값 처리
            if (synonyms == null) {
                chemInfo = response.toEntity();
                chemInfoRepository.save(chemInfo);  // 분자 정보 저장
            } else {
                List<String> entitySynonyms;

                // 크기가 5인 Synonyms List로 변환
                if (synonyms.size() > 5) {
                    entitySynonyms = synonyms.subList(0, 5);    // 0 ~ 4 번째 값만 리스트로 가져옴
                } else {
                    entitySynonyms = synonyms;
                }

                chemInfo = response.toEntity(entitySynonyms);    // Synonyms를 크기가 5인 List로 변경
                ChemInfo entity = chemInfoRepository.save(chemInfo);  // 분자 정보 저장
                synonymsService.saveSynonyms(entity, response.getSynonyms());  // 해당 분자 정보에 대한 Synonyms 저장
            }
        } catch (DuplicateKeyException e) {
            log.debug("PubChem에서 SMILES 보정하여 검색한 것");
            ChemInfo originInfo = response.toEntity();

            return originInfo;
        } catch (Exception e) {
            log.error("분자정보 저장 시 에러, CID: {}, timestemp: {}", e, LocalDateTime.now());
            throw new CustomException(ErrorCode.ALREADY_EXIST_CHEM_INFO);
        }

        log.info("분자정보 저장, CID: {}, timestemp: {}", chemInfo.getCid(), LocalDateTime.now());

        return chemInfo;
    }
    
    /* SMILES 식으로 분자 정보 가져오기 */
    public ChemInfo findChemInfoBySmiles(String smiles) {
        return chemInfoRepository.findByIsomericSmiles(smiles).orElse(null);    // 없으면 null 반환
    }

    /* _id로 분자 정보 가져오기 */
    public ChemInfo findChemInfoById(String id) {
        return chemInfoRepository.findById(id).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_CHEM_INFO));    // 없으면 404 Error
    }

    /* CID로 분자 정보 가져오기 */
    public ChemInfo findChemInfoByCid(Long cid) {
        return chemInfoRepository.findByCid(cid).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_CHEM_INFO));    // 없으면 404 Error
    }

    /* CID 값으로 3D Image 정보 변경 */
    public String update3DImage(Long cid) {
        ChemInfo chemInfo = findChemInfoByCid(cid);

        String sdf = webclientByCid.getSdf(cid).block();

        return sdf;
    }
}
