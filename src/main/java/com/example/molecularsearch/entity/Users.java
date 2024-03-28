package com.example.molecularsearch.entity;

import com.example.molecularsearch.constant.RoleType;
import com.example.molecularsearch.constant.SignUpType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class Users extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // Primary Key

    @Column(nullable = false, unique = true)
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

    @Column(nullable = false)
    private SignUpType signUpType;    // 회원가입한 경로 (NAVER or GOOGLE)

    @Column(nullable = false)
    private RoleType roleType;    // 유저 등급 (ADMIN or USER)

    @Builder
    public Users(Long id, String userId, String email, String name, String nickname, String mobile, String gender, String profileImage, SignUpType signUpType, RoleType roleType) {
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
