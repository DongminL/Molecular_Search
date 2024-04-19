package com.example.molecularsearch.service;

import com.example.molecularsearch.entity.ChemInfo;
import com.example.molecularsearch.entity.Synonyms;
import com.example.molecularsearch.repository.SynonymsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SynonymsService {

    private final SynonymsRepository synonymsRepository;

    /* Synonyms 저장 */
    public void saveSynonyms(ChemInfo chemInfo, List<String> synonymsList) {
        Synonyms synonyms = Synonyms.builder()
                .chemInfo(chemInfo)
                .synonyms(synonymsList)
                .build();

        synonymsRepository.save(synonyms);  // Synonyms 값 저장
    }

    /* keyword로 검색한 결과 리스트 가져오기 */
    public List<Synonyms> searchChemInfo(String keyword) {
        Sort sort = Sort.by(Sort.Direction.DESC, "meta.score");    // 검색 정확도 기준으로 내림차순 정렬

        return synonymsRepository.findByTextSearch(keyword, sort);
    }
}
