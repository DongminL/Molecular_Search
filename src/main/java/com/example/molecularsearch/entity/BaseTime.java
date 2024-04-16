package com.example.molecularsearch.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass   // BaseTime을 상속하면 해당 Entity가 저장될 때 Column으로 생성일과 수정일이 추가됨
@EntityListeners(AuditingEntityListener.class)  // Auditing 기능 추가
public class BaseTime {

    @CreatedDate    // DB 저장 시 자동으로 생성일 추가
    private LocalDateTime createdDate;   // 생성일

    @LastModifiedDate   // DB 저장 시 자동으로 생성일 추가
    private LocalDateTime modifiedDate; // 수정일
}
