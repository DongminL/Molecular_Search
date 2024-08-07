package com.example.molecularsearch.jwt.web.filter;

import com.example.molecularsearch.exception.error.CustomException;
import com.example.molecularsearch.exception.error.ErrorCode;
import com.example.molecularsearch.jwt.repository.TokensRepository;
import com.example.molecularsearch.jwt.web.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;
    private final TokensRepository tokensRepository;

    /*
     * 실제 필터링 로직
     * 토큰의 인증정보를 Security Context에 저장
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String token = getToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        // 유효한 토큰인지 확인
        if (StringUtils.hasText(token) && jwtProvider.checkToken(token)) {
            if (tokensRepository.existsByAccessToken(token)) {  // Redis에 토큰이 있는지 확인
                Authentication authentication = jwtProvider.getAuthentication(token);   // 토큰으로부터 유저 정보 받아옴

                SecurityContextHolder.getContext().setAuthentication(authentication);   // Security Context에 인증 저장
                log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
            } else {    // 재로그인 요청
                throw new CustomException(ErrorCode.REQUIRE_RELOGIN);
            }
        } else {
            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    /* Request Header 에서 토큰 정보를 꺼내오기 */
    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);    // "Bearer " 부분을 자르고 JWT만 가져옴
        }

        return null;
    }
}
