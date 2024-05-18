package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.SearchLogDto;
import com.example.molecularsearch.entity.SearchLog;
import com.example.molecularsearch.repository.SearchLogRepository;
import com.example.molecularsearch.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchLogService {

    private final CustomUserDetailsService customUserDetailsService;
    private final UsersRepository usersRepository;
    private final SearchLogRepository searchLogRepository;

    /* 검색어 저장 */
    @Transactional
    public void saveSearchLog(String searchLog) {
        Long userPk = customUserDetailsService.getCurrentUserPk().get();  // Security Context에서 Users PK 값 가져오기

        // Users 테이블에 해당 객체가 있으면 검색 기록 저장
        usersRepository.findById(userPk).ifPresent(user -> {
            searchLogRepository.save(SearchLog.builder()
                .user(user)
                .log(searchLog)
                .build());
        });

        log.info("검색기록 추가, user_PK : {}, search_log: {}, timestemp: {}", userPk, searchLog, LocalDateTime.now());
    }

    /* 단일 검색 기록 삭제 */
    @Transactional
    public void deleteSearchLog(Long id) {
        searchLogRepository.deleteById(id); // 해당 검색어만 삭제

        log.info("검색기록 삭제, search_log_PK: {}, timestemp: {}", id, LocalDateTime.now());
    }

    /* 여러 검색 기록 삭제 */
    @Transactional
    public void editSearchLog(List<SearchLogDto> searchLogDtos) {
        searchLogDtos.forEach(e -> {
            deleteSearchLog(e.getId());
        });
    }

    /* 유저에 대한 모든 검색 기록 가져오기 */
    @Transactional(readOnly = true)
    public Map<String, List<SearchLogDto>> findSearchLog() {
        Long userPk = customUserDetailsService.getCurrentUserPk().get();  // Security Context에서 Users PK 값 가져오기

        List<SearchLog> searchLogs = searchLogRepository.findAllByUser_IdOrderByCreatedDate(userPk);    // 해당 유저에 대한 전체 검색 기록

        // Entity List -> Dto List
        List<SearchLogDto> searchLogDtos = new ArrayList<>();
        SearchLogDto dto = new SearchLogDto();
        searchLogs.forEach(e -> {
            searchLogDtos.add(dto.toDto(e));
        });

        // 쉽게 Parsing 할 수 있게 Mapping
        Map<String, List<SearchLogDto>> result = new HashMap<>();
        result.put("searchLogList", searchLogDtos);

        log.debug("검색기록 가져오기, user_PK : {}, timestemp: {}", userPk, LocalDateTime.now());

        return result;
    }


}
