package com.example.molecularsearch.entity;

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
public class Synonyms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // Synonyms PK
    
    @OneToOne(fetch = FetchType.LAZY)   // 지연 로딩으로 설정 (Select 시 ChemInfo와 JOIN 방지)
    @JoinColumn(name = "chemInfo_id")
    private ChemInfo chemInfo;  // ChemInfo 객체 참조

    @Column(columnDefinition = "text")
    private String synonyms;    // 비슷한 화합물들
}
