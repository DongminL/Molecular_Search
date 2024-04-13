package com.example.molecularsearch.service;

import com.example.molecularsearch.entity.ChemInfo;
import com.example.molecularsearch.entity.Synonyms;
import com.example.molecularsearch.repository.SynonymsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SynonymsService {

    private final SynonymsRepository synonymsRepository;

    /* Synonyms 저장 */
    @Transactional
    public void saveSynonyms(ChemInfo chemInfo, List<String> synonymsList) {
        String synonymsStr = String.join("|", synonymsList);  // List -> String ("|"로 구분)
        Synonyms synonyms = Synonyms.builder()
                .chemInfo(chemInfo)
                .synonyms(synonymsStr)
                .build();

        synonymsRepository.save(synonyms);  // Synonyms 값 저장
    }
}
