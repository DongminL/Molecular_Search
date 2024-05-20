package com.example.molecularsearch.oauth.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverUserDto {

    @JsonProperty("user_id")
    private String userId;  // 유저 ID

    @JsonProperty("email")
    private String email;   // Email

    @JsonProperty("name")
    private String name;    // 유저 이름

    @JsonProperty("nickname")
    private String nickname;    // 별명

    @JsonProperty("mobile")
    private String mobile;  // 휴대폰 번호

    @JsonProperty("gender")
    private String gender;  // 성별

    @JsonProperty("profile_image")
    private String profileImage;    // 프로필 사진 url
}