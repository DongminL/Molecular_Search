package com.example.molecularsearch.bookmark.service;

import com.example.molecularsearch.bookmark.web.dto.InfoBookmarkDto;
import com.example.molecularsearch.chem_info.domain.ChemInfo;
import com.example.molecularsearch.bookmark.entity.InfoBookmark;
import com.example.molecularsearch.users.entity.Users;
import com.example.molecularsearch.exception.error.CustomException;
import com.example.molecularsearch.exception.error.ErrorCode;
import com.example.molecularsearch.jwt.service.CustomUserDetailsService;
import com.example.molecularsearch.chem_info.repository.ChemInfoRepository;
import com.example.molecularsearch.bookmark.repository.InfoBookmarkRepository;
import com.example.molecularsearch.users.repository.UsersRepository;
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
public class InfoBookmarkService {

    private final InfoBookmarkRepository infoBookmarkRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final UsersRepository usersRepository;
    private final ChemInfoRepository chemInfoRepository;

    /* 즐겨찾기 정보 저장 */
    public void saveInfoBookmark(String chemId) {
        Long userPk = customUserDetailsService.getCurrentUserPk().get();    // Security Context에 저장된 유저 PK 값 가져오기

        try {
            ChemInfo chemInfo = chemInfoRepository.findById(chemId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHEM_INFO));   // 분자 정보 가져오기
            Users user = usersRepository.findById(userPk).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER)); // 유저 정보 가져오기

            // InfoFav Entity 생성
            InfoBookmark entity = InfoBookmark.builder()
                    .user(user)
                    .chemInfoId(chemId)
                    .molecularFormula(chemInfo.getMolecularFormula())
                    .build();

            infoBookmarkRepository.save(entity); // 즐겨찾기 추가
        } catch (NullPointerException e) {
            log.error(e.toString());
        }

        log.info("즐겨찾기 추가, user_PK : {}, chem_id: {}, timestemp: {}", userPk, chemId, LocalDateTime.now());
    }

    /* 해당 유저의 즐겨찾기 리스트 가져오기 */
    @Transactional(readOnly = true)
    public Map<String, List<InfoBookmarkDto>> getBookmarkList() {
        Long userPk = customUserDetailsService.getCurrentUserPk().get();    // Security Context에 저장된 유저 PK 값 가져오기

        List<InfoBookmark> entityList = infoBookmarkRepository.findAllByUser_IdOrderByModifiedDateDesc(userPk);   // 해당 유저의 즐겨찾기 리스트 가져오기

        // Entity List -> DTO List
        List<InfoBookmarkDto> dtoList = new ArrayList<>();
        InfoBookmarkDto dto = new InfoBookmarkDto();
        entityList.forEach(e -> {
            dtoList.add(dto.toDto(e));
        });

        // 쉽게 Parsing 할 수 있게 Mapping
        Map<String, List<InfoBookmarkDto>> result = new HashMap<>();
        result.put("bookmarkList", dtoList);

        log.debug("즐겨찾기 가져오기, user_PK : {}, timestemp: {}", userPk, LocalDateTime.now());

        return result;
    }

    /* 단일 즐겨찾기 헤제 */
    @Transactional
    public void deleteInfoBookmark(String chemInfoId) {
        Long userPk = customUserDetailsService.getCurrentUserPk().get();    // Security Context에 저장된 유저 PK 값 가져오기

        infoBookmarkRepository.deleteByUser_IdAndChemInfoId(userPk, chemInfoId); // 해당 즐겨찾기만 삭제
        log.info("즐겨찾기 해제, infoBookmark_PK : {}, timestemp: {}", chemInfoId, LocalDateTime.now());
    }

    /* 단일 즐겨찾기 삭제 */
    @Transactional
    public void deleteInfoBookmark(Long id) {
        infoBookmarkRepository.deleteById(id); // 해당 즐겨찾기만 삭제
        log.info("즐겨찾기 삭제, infoBookmark_PK : {}, timestemp: {}", id, LocalDateTime.now());
    }

    /* 여러 즐겨찾기 항목 삭제 */
    @Transactional
    public void editInfoBookmark(List<InfoBookmarkDto> infoBookmarkDtos) {
        List<Long> idList = new ArrayList<>();  // 삭제하려는 즐겨찾기 id 리스트

        infoBookmarkDtos.forEach(e -> {
            idList.add(e.getId());
        });

        infoBookmarkRepository.deleteAllByList(idList); // 선택된 즐겨찾기 목록 삭제
    }

    /* 전체 즐겨찾기 삭제 */
    @Transactional
    public void deleteAllInfoBookmark() {
        Long userPk = customUserDetailsService.getCurrentUserPk().get();    // Security Context에 저장된 유저 PK 값 가져오기

        infoBookmarkRepository.deleteAllByUser_Id(userPk); // 전체 즐겨찾기 삭제
        log.info("전체 즐겨찾기 삭제, user_PK : {}, timestemp: {}", userPk, LocalDateTime.now());
    }

    /* 즐겨찾기 유무 확인 */
    @Transactional(readOnly = true)
    public Map<String, Boolean> checkInfoBookmark(String chemId) {
        Long userPk = customUserDetailsService.getCurrentUserPk().get();    // Security Context에 저장된 유저 PK 값 가져오기
        Map<String, Boolean> state = new HashMap<>();   // 즐겨찾기 상태값

        Boolean exist = infoBookmarkRepository.existsByUser_IdAndChemInfoId(userPk, chemId);    // 즐겨찾기 유무
        state.put("state", exist);

        log.debug("즐겨찾기 유무, user_PK : {}, isBookmark: {}, timestemp: {}", userPk, exist, LocalDateTime.now());

        return state;
    }
}
