package com.example.molecularsearch.bookmark.entity;

import com.example.molecularsearch.common.entity.BaseTime;
import com.example.molecularsearch.users.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoBookmark extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // InfoFav Table PK

    @ManyToOne(fetch = FetchType.LAZY)  // 지연 로딩으로 설정 (Select 시 Users와 무분별한 JOIN 방지)
    @JoinColumn(name = "user_id")
    private Users user; // Users 객체 참조

    @Column
    private String chemInfoId;  // MongoDB의 ChemInfo Documnet _id 참조
    
    @Column
    private String molecularFormula;    // 분자식
}