package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.SearchLogDto;
import com.example.molecularsearch.entity.SearchLog;
import com.example.molecularsearch.entity.Users;
import com.example.molecularsearch.jwt.JwtProvider;
import com.example.molecularsearch.repository.SearchLogRepository;
import com.example.molecularsearch.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchLogService {

    private final JwtProvider jwtProvider;
    private final JwtService jwtService;
    private final UsersRepository usersRepository;
    private final SearchLogRepository searchLogRepository;

    /* 검색어 저장 */
    @Transactional
    public void saveSearchLog(String bearerToken, String log) {
        String userPk = jwtProvider.getUserPk(jwtService.getHeaderToken(bearerToken));  // Access Token으로 Users PK 값 가져오기
        // Users 테이블에 해당 객체가 있으면 검색 기록 저장
        usersRepository.findById(Long.parseLong(userPk)).ifPresent(user -> {
            searchLogRepository.save(SearchLog.builder()
                .user(user)
                .log(log)
                .build());
        });
    }

    /* 단일 검색 기록 삭제 */
    @Transactional
    public void deleteSearchLog(Long id) {
        searchLogRepository.deleteById(id); // 해당 검색어만 삭제
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
    public List<SearchLogDto> findSearchLog(String bearerToken) {
        String userPk = jwtProvider.getUserPk(jwtService.getHeaderToken(bearerToken));  // Access Token으로 Users PK 값 가져오기
        Users user = usersRepository.findById(Long.parseLong(userPk)).orElse(null);   // 해당 Entity 가져오기

        List<SearchLog> searchLogs = searchLogRepository.findAllByUserOrderByCreatedDate(user);    // 해당 유저에 대한 전체 검색 기록

        // Entity List -> Dto List
        List<SearchLogDto> searchLogDtos = new ArrayList<>();
        for (SearchLog o : searchLogs) {
            searchLogDtos.add(SearchLogDto.builder()
                    .id(o.getId())
                    .log(o.getLog())
                    .build());
        }

        return searchLogDtos;
    }
}
