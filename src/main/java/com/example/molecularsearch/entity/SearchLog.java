package com.example.molecularsearch.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class SearchLog extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // SearchLog 테이블 PK

    @ManyToOne(fetch = FetchType.LAZY)  // 지연 로딩으로 설정 (Select 시 Users와 JOIN 방지)
    @JoinColumn(name = "users_id")
    private Users user;    // Users 객체 참조

    @Column
    private String log; // 검색어

    @Builder
    public SearchLog(Long id, Users user, String log) {
        this.id = id;
        this.user = user;
        this.log = log;
    }
}
