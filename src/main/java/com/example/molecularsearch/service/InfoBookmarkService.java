package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.InfoBookmarkDto;
import com.example.molecularsearch.entity.ChemInfo;
import com.example.molecularsearch.entity.InfoBookmark;
import com.example.molecularsearch.entity.Users;
import com.example.molecularsearch.exception.CustomException;
import com.example.molecularsearch.exception.ErrorCode;
import com.example.molecularsearch.repository.ChemInfoRepository;
import com.example.molecularsearch.repository.InfoBookmarkRepository;
import com.example.molecularsearch.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void saveInfoFav(String chemId) {
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
    }

    /* 해당 유저의 즐겨찾기 리스트 가져오기 */
    @Transactional(readOnly = true)
    public List<InfoBookmarkDto> getBookmarkList() {
        Long userPk = customUserDetailsService.getCurrentUserPk().get();    // Security Context에 저장된 유저 PK 값 가져오기

        List<InfoBookmark> entityList = infoBookmarkRepository.findAllByUser_IdOrderByModifiedDate(userPk);   // 해당 유저의 즐겨찾기 리스트 가져오기

        // Entity List -> DTO List
        List<InfoBookmarkDto> dtoList = new ArrayList<>();
        InfoBookmarkDto dto = new InfoBookmarkDto();
        entityList.forEach(e -> {
            dtoList.add(dto.toDto(e));
        });

        return dtoList;
    }

    /* 단일 즐겨찾기 삭제 */
    @Transactional
    public void deleteInfoBookmark(Long id) {
        infoBookmarkRepository.deleteById(id); // 해당 검색어만 삭제
    }

    /* 여러 즐겨찾기 항목 삭제 */
    @Transactional
    public void editInfoBookmark(List<InfoBookmarkDto> infoBookmarkDtos) {
        infoBookmarkDtos.forEach(e -> {
            deleteInfoBookmark(e.getId());
        });
    }

    /* 즐겨찾기 유무 확인 */
    @Transactional(readOnly = true)
    public Map<String, Boolean> checkInfoBookmark(String chemId) {
        Long userPk = customUserDetailsService.getCurrentUserPk().get();    // Security Context에 저장된 유저 PK 값 가져오기
        Map<String, Boolean> state = new HashMap<>();   // 즐겨찾기 상태값

        Boolean exist = infoBookmarkRepository.existsByUser_IdAndChemInfoId(userPk, chemId);    // 즐겨찾기 유무
        state.put("state", exist);

        return state;
    }
}
