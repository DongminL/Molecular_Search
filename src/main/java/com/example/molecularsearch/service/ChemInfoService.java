package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.ChemInfoDto;
import com.example.molecularsearch.entity.ChemInfo;
import com.example.molecularsearch.repository.ChemInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChemInfoService {

    private final ChemInfoWebClient chemInfoWebClient;
    private final ChemInfoRepository chemInfoRepository;
    private final SynonymsService synonymsService;

    /* SMILES 식으로 분자정보 검색 */
    @Transactional
    public ChemInfoDto searchSmiles(String smiles) {
        ChemInfoDto chemInfoDto;
        ChemInfo entity = findChemInfoBySmiles(smiles); // SMILES 식으로 찾아 가져오기

        // DB에 해당 정보가 있으면
        if (entity != null) {
            log.info(entity.toString());
            chemInfoDto = new ChemInfoDto(entity, entity.getSynonyms());    // Synonyms List 크기를 최대 5로 줄임
            return chemInfoDto;
        }

        chemInfoDto = chemInfoWebClient.requestInfoBySmiles(smiles);  // Fast API로 요청하여 가져옴
        log.info(chemInfoDto.toString());

        entity = saveChemInfo(chemInfoDto);  // 가져옴 값 저장
        chemInfoDto = new ChemInfoDto(entity, entity.getSynonyms());    // Synonyms List 크기를 최대 5로 줄임

        return chemInfoDto;
    }

    /* CID 값으로 요청한 분자정보 저장*/
    public ChemInfo saveInfoByCid(Long cid) {
        ChemInfoDto chemInfoDto = chemInfoWebClient.requestInfoByCid(cid);

        return saveChemInfo(chemInfoDto);
    }

    /* 응답값으로 받은 분자정보 저장 */
    @Transactional
    public ChemInfo saveChemInfo(ChemInfoDto request) {
        ChemInfo chemInfo;
        List<String> synonyms = request.getSynonyms();

        // Synonyms 값 처리
        if (synonyms == null) {
            chemInfo = request.toEntity();
            chemInfoRepository.save(chemInfo);  // 분자 정보 저장
        } else {
            List<String> entitySynonyms;

            // 크기가 5인 Synonyms List로 변환
            if (synonyms.size() > 5) {
                entitySynonyms = synonyms.subList(0, 5);    // 0 ~ 4 번째 값만 리스트로 가져옴
            } else {
                entitySynonyms = synonyms;
            }

            chemInfo = request.toEntity(entitySynonyms);    // Synonyms를 크기가 5인 List로 변경
            ChemInfo entity = chemInfoRepository.save(chemInfo);  // 분자 정보 저장
            synonymsService.saveSynonyms(entity, request.getSynonyms());  // 해당 분자 정보에 대한 Synonyms 저장
        }

        return chemInfo;
    }
    
    /* SMILES 식으로 분자 정보 가져오기 */
    @Transactional(readOnly = true)
    public ChemInfo findChemInfoBySmiles(String smiles) {
        return chemInfoRepository.findByIsomericSmiles(smiles).orElse(null);    // 없으면 null 반환
    }
}
