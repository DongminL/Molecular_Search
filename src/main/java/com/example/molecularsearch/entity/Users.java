package com.example.molecularsearch.entity;

import com.example.molecularsearch.constant.RoleTypeConverter;
import com.example.molecularsearch.constant.SignUpTypeConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
@Table(indexes = @Index(name = "index_user_id", columnList = "userId", unique = true))  // userId에 대한 Index 생성
public class Users extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // Primary Key

    @Column(nullable = false)
    private String userId;  // 식별 ID

    @Column
    private String email;   // 이메일

    @Column
    private String name;    // 회원 이름

    @Column
    private String nickname;    // 닉네임

    @Column
    private String mobile;  // 휴대폰 번호

    @Column
    private String gender;  // 성별

    @Column
    private String profileImage;  // 프로필 사진 url

    @Convert(converter = SignUpTypeConverter.class) // Convert로 데이터 형식 변환
    @Column(nullable = false)
    private String signUpType;    // 회원가입한 경로 (NAVER or GOOGLE)

    @Convert(converter = RoleTypeConverter.class)   // Convert로 데이터 형식 변환
    @Column(nullable = false)
    private String roleType;    // 유저 등급 (ADMIN or USER)

    @Builder
    public Users(Long id, String userId, String email, String name, String nickname, String mobile, String gender, String profileImage, String signUpType, String roleType) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.mobile = mobile;
        this.gender = gender;
        this.profileImage = profileImage;
        this.signUpType = signUpType;
        this.roleType = roleType;
    }
}
