package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.ChemInfoDto;
import com.example.molecularsearch.dto.SearchResultDto;
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
    public SearchResultDto searchChemInfo(String keyword) {
        Sort sort = Sort.by(Sort.Direction.DESC, "meta.score");    // 검색 정확도 기준으로 내림차순 정렬
        SearchResultDto result = new SearchResultDto(); // 검색된 분자정보 리스트 DTO

        // Synonyms를 이용한 분자정보 리스트를 Dto로 변환
        synonymsRepository.findByTextSearch(keyword, sort).forEach(e -> {
            result.getSearchResults().add(new ChemInfoDto(e.getChemInfo(), e.getChemInfo().getSynonyms()));
        });
        
        return result;
    }
}
