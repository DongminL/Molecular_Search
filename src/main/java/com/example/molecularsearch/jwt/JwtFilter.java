package com.example.molecularsearch.jwt;

import com.example.molecularsearch.exception.CustomException;
import com.example.molecularsearch.exception.ErrorCode;
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

        try {
            // 유효한 토큰인지 확인
            if (StringUtils.hasText(token) && jwtProvider.checkToken(token)) {
                Authentication authentication = jwtProvider.getAuthentication(token);   // 토큰으로부터 유저 정보 받아옴

                SecurityContextHolder.getContext().setAuthentication(authentication);   // Security Context에 인증 저장
                log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
            } else {
                log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
            }

            filterChain.doFilter(servletRequest, servletResponse);
        } finally { // 다시 Filter를 빠져 나가기 전에
            SecurityContextHolder.clearContext();   // Security Context 비우기
            log.debug("Security Context를 비웠습니다.");
        }
    }

    /* Request Header 에서 토큰 정보를 꺼내오기 */
    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);    // "Bearer " 부분을 자르고 JWT만 가져옴
        }

        throw new CustomException(ErrorCode.BAD_REQUEST_HAEDER);
    }
}
