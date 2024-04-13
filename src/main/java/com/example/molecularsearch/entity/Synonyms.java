package com.example.molecularsearch.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Synonyms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // Synonyms PK
    
    @OneToOne(fetch = FetchType.LAZY)   // 지연 로딩으로 설정 (Select 시 ChemInfo와 JOIN 방지)
    @JoinColumn(name = "chemInfo_id")
    private ChemInfo chemInfo;  // ChemInfo 객체 참조

    @Column(length = 10000)
    private String synonyms;    // 비슷한 화합물들

    @Builder
    public Synonyms(Long id, ChemInfo chemInfo, String synonyms) {
        this.id = id;
        this.chemInfo = chemInfo;
        this.synonyms = synonyms;
    }
}
