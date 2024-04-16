package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.InfoFavDto;
import com.example.molecularsearch.entity.ChemInfo;
import com.example.molecularsearch.entity.InfoFav;
import com.example.molecularsearch.entity.Users;
import com.example.molecularsearch.repository.ChemInfoRepository;
import com.example.molecularsearch.repository.InfoFavRepository;
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
public class InfoFavService {

    private final InfoFavRepository infoFavRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final UsersRepository usersRepository;
    private final ChemInfoRepository chemInfoRepository;

    /* 즐겨찾기 정보 저장 */
    @Transactional
    public void saveInfoFav(Long chemId) {
        Long userPk = customUserDetailsService.getCurrentUserPk().get();    // Security Context에 저장된 유저 PK 값 가져오기

        try {
            ChemInfo chemInfo = chemInfoRepository.findById(chemId).orElse(null);   // 분자 정보 가져오기
            Users user = usersRepository.findById(userPk).orElse(null); // 유저 정보 가져오기

            // InfoFav Entity 생성
            InfoFav entity = InfoFav.builder()
                    .user(user)
                    .chemInfo(chemInfo)
                    .molecularFormula(chemInfo.getMolecularFormula())
                    .build();

            infoFavRepository.save(entity); // 즐겨찾기 추가
        } catch (NullPointerException e) {
            log.error(e.toString());
        }
    }

    /* 해당 유저의 즐겨찾기 리스트 가져오기 */
    @Transactional(readOnly = true)
    public List<InfoFavDto> getFavList() {
        Long userPk = customUserDetailsService.getCurrentUserPk().get();    // Security Context에 저장된 유저 PK 값 가져오기

        List<InfoFav> entityList = infoFavRepository.findAllByUser_IdOrderByModifiedDate(userPk);   // 해당 유저의 즐겨찾기 리스트 가져오기

        // Entity List -> DTO List
        List<InfoFavDto> dtoList = new ArrayList<>();
        InfoFavDto dto = new InfoFavDto();
        entityList.forEach(e -> {
            dtoList.add(dto.toDto(e));
        });

        return dtoList;
    }

    /* 단일 검색 기록 삭제 */
    @Transactional
    public void deleteInfoFav(Long id) {
        infoFavRepository.deleteById(id); // 해당 검색어만 삭제
    }

    /* 여러 검색 기록 삭제 */
    @Transactional
    public void editInfoFav(List<InfoFavDto> infoFavDtos) {
        infoFavDtos.forEach(e -> {
            deleteInfoFav(e.getId());
        });
    }

    /* 즐겨찾기 유무 확인 */
    @Transactional(readOnly = true)
    public Map<String, Boolean> checkInfoFav(Long chemId) {
        Long userPk = customUserDetailsService.getCurrentUserPk().get();    // Security Context에 저장된 유저 PK 값 가져오기
        Map<String, Boolean> state = new HashMap<>();   // 즐겨찾기 상태값

        Boolean exist = infoFavRepository.existsByUser_IdAndChemInfo_Id(userPk, chemId);    // 즐겨찾기 유무
        state.put("state", exist);

        return state;
    }
}
