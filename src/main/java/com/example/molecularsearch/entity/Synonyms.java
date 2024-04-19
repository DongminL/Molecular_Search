package com.example.molecularsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Builder
@Document("Synonyms")
@NoArgsConstructor
@AllArgsConstructor
public class Synonyms {

    @Id
    private String id;    // Synonyms PK
    
    @DBRef  // Reference 설정
    private ChemInfo chemInfo;  // ChemInfo 객체 참조

    @TextIndexed    // TextIndex 생성 (Full Text Search 이용)
    private List<String> synonyms;    // 비슷한 화합물들
}
