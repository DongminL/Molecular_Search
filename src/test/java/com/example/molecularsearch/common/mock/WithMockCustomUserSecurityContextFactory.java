package com.example.molecularsearch.common.mock;

import com.example.molecularsearch.common.anotation.WithMockCustomUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collection;
import java.util.List;

/* JWT Filter와 비슷한 로직의 테스트용 SecurityContext */
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        // 커스텀한 Mock User를 SecurityContext에 추가
        final Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(annotation.roleType()));
        final Authentication authentication = new UsernamePasswordAuthenticationToken(annotation.userName(), "", authorities);
        securityContext.setAuthentication(authentication);

        return securityContext;
    }
}
