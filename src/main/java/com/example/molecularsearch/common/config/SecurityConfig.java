package com.example.molecularsearch.common.config;

import com.example.molecularsearch.jwt.web.filter.JwtExceptionFilter;
import com.example.molecularsearch.jwt.web.filter.JwtFilter;
import com.example.molecularsearch.jwt.web.handler.JwtHandler;
import com.example.molecularsearch.jwt.web.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity  // Spring Security 지원을 가능하게 함
@ComponentScan(basePackages = {"com.example.molecularsearch.jwt"})
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtHandler jwtHandler;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                // JWT로 안드로이드와 통신하기 때문에 csrf disable
                .csrf(AbstractHttpConfigurer::disable)
                // 세션 사용 X
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // JWT Handler 등록
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(jwtHandler)
                                .accessDeniedHandler(jwtHandler))
                // JwtFilter를 Security 로직에 등록
                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)    // JwtFilter를 UsernamePasswordAuthenticationFilter보다 먼저 실행
                // URL 권한 설정 */
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests.requestMatchers("/api/login/naver", "/api/login/google", "/api/login/reissue").permitAll()  // 로그인, 토큰 갱신 요청은 누구든지 허용
                                .anyRequest().authenticated()   // 그외 다른 요청들은 토큰 인증해야함
                );

        http.addFilterBefore(new JwtExceptionFilter(), JwtFilter.class);    // JwtExceptionFilter를 JwtFilter보다 먼저 실행

        return http.build();
    }
}
