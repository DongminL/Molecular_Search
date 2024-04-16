package com.example.molecularsearch.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class InfoFav extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // InfoFav Table PK

    @ManyToOne(fetch = FetchType.LAZY)  // 지연 로딩으로 설정 (Select 시 Users와 무분별한 JOIN 방지)
    @JoinColumn(name = "user_id")
    private Users user; // Users 객체 참조

    @ManyToOne(fetch = FetchType.LAZY)  // 지연 로딩으로 설정 (Select 시 ChemInfo와 무분별한 JOIN 방지)
    @JoinColumn(name = "chemInfo_id")
    private ChemInfo chemInfo;  // ChemInfo 객체 참조
    
    @Column
    private String molecularFormula;    // 분자식

    @Builder
    public InfoFav(Long id, Users user, ChemInfo chemInfo, String molecularFormula) {
        this.id = id;
        this.user = user;
        this.chemInfo = chemInfo;
        this.molecularFormula = molecularFormula;
    }
}
