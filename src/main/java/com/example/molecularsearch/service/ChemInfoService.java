package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.ChemInfoDto;
import com.example.molecularsearch.entity.ChemInfo;
import com.example.molecularsearch.repository.ChemInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChemInfoService {

    private final ChemInfoWebClient chemInfoWebClient;
    private final ChemInfoRepository chemInfoRepository;

    /* SMILES 식으로 분자정보 검색 */
    @Transactional
    public ChemInfoDto searchSmiles(String smiles) {
        ChemInfoDto chemInfoDto;
        ChemInfo entity = findChemInfoBySmiles(smiles); // SMILES 식으로 찾아 가져오기

        // DB에 해당 정보가 있으면
        if (entity != null) {
            chemInfoDto = new ChemInfoDto(entity, null);
            return chemInfoDto;
        }

        chemInfoDto = chemInfoWebClient.getChemInfo(smiles);  // Fast API로 요청하여 가져옴
        log.info(chemInfoDto.toString());

        saveChemInfo(chemInfoDto);  // 가져옴 값 저장

        return chemInfoDto;
    }

    /* 응답값으로 받은 분자정보 저장 */
    @Transactional
    public ChemInfo saveChemInfo(ChemInfoDto request) {
        ChemInfo chemInfo = request.toEntity();

        return chemInfoRepository.save(chemInfo);
    }
    
    /* SMILES 식으로 분자 정보 가져오기 */
    @Transactional(readOnly = true)
    public ChemInfo findChemInfoBySmiles(String smiles) {
        return chemInfoRepository.findByIsomericSmiles(smiles).orElse(null);    // 없으면 null 반환
    }
}
