package com.example.molecularsearch.repository;

import com.example.molecularsearch.entity.InfoFav;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfoFavRepository extends JpaRepository<InfoFav, Long> {
    
    List<InfoFav> findAllByUser_IdOrderByModifiedDate(Long users_id);    // 해당 유저에 대한 즐겨찾기 리스트 가져오기 (수정일 기준으로 정렬)
    
    Boolean existsByUser_IdAndChemInfo_Id(Long users_id, Long chem_id);    // 해당 분자정보의 즐겨찾기 여부 확인
    
    void deleteAllByUser_id(Long users_id);   // 해당 유저에 대한 정보 전부 삭제
    
    void deleteById(Long id);   // PK 값으로 정보 삭제
}
