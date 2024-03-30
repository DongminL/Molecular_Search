package com.example.molecularsearch.jwt;

import com.example.molecularsearch.entity.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Entity
@ToString
@NoArgsConstructor
public class RefreshToken extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // Primary Key

    @Indexed
    private String refreshToken;  // Refresh Token 값

    private String userPk;   // Users Table Pk 값

    @Builder
    public RefreshToken(Long id, String refreshToken, String userPk) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.userPk = userPk;
    }

}
