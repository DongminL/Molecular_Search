package com.example.molecularsearch.oauth.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserDto {

    @JsonProperty("user_id")
    private String userId;  // 유저 ID

    @JsonProperty("email")
    private String email;   // Email

    @JsonProperty("name")
    private String name;    // 유저 이름

    @JsonProperty("photo_url")
    private String profileImage;    // 프로필 사진 url
}
