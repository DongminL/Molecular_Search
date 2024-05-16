package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.ChemInfoDto;
import com.example.molecularsearch.dto.SearchResultDto;
import com.example.molecularsearch.entity.ChemInfo;
import com.example.molecularsearch.entity.Synonyms;
import com.example.molecularsearch.repository.SynonymsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SynonymsService {

    private final SynonymsRepository synonymsRepository;
    private final MongoTemplate mongoTemplate;

    /* Synonyms 저장 */
    public void saveSynonyms(ChemInfo chemInfo, List<String> synonymsList) {
        Synonyms synonyms = Synonyms.builder()
                .chemInfo(chemInfo)
                .synonyms(synonymsList)
                .build();

        synonymsRepository.save(synonyms);  // Synonyms 값 저장
    }

    /* keyword로 검색한 결과 리스트 가져오기 */
    public SearchResultDto searchChemInfo(String keyword, int page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "meta.score");    // 검색 정확도 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page, 10, sort);
        List<ChemInfoDto> result = new ArrayList<>(); // 검색된 분자정보 리스트 (DTO)

        // Full Text Search Query
        Query query = TextQuery.queryText(TextCriteria.forDefaultLanguage().matchingAny(keyword));

        // 검색된 분자정보 리스트를 Dto로 변환
        mongoTemplate.find(query.skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize()), Synonyms.class).forEach(e -> {
                    if (e.getChemInfo() != null) {
                        result.add(new ChemInfoDto(e.getChemInfo(), e.getChemInfo().getSynonyms()));
                    }
        });

        // 불러온 페이지 정보
        Page<ChemInfoDto> chemInfoPage = PageableExecutionUtils.getPage(
                result,
                pageable,
                () -> mongoTemplate.count(query.skip(-1).limit(-1), Synonyms.class) // 현재 쿼리가 페이징 하려고 하는 offset 까지만 보기에 이를 맨 처음부터 끝까지로 set 해줘 정확한 도큐먼트 개수를 구함
        );

        // 검색 결과 DTO로 변환
        return SearchResultDto.builder()
                .totalElements(chemInfoPage.getTotalElements())
                .totalPages(chemInfoPage.getTotalPages())
                .pageNumber(chemInfoPage.getNumber())
                .pageSize(chemInfoPage.getSize())
                .searchResults(result)
                .build();
    }
}
