package com.example.molecularsearch.service;

import com.example.molecularsearch.entity.Users;
import com.example.molecularsearch.jwt.CustomUserDetails;
import com.example.molecularsearch.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;
    
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<Users> users = usersRepository.findById(Long.parseLong(id));   // Users Table PK 값으로 찾기

        if (users.isEmpty()) {
            users = usersRepository.findByUserId(id);   // User Id 값으로 찾기
        }

        if (users.isEmpty()) {
            throw new RuntimeException("Not Found Error");
        }

        return new CustomUserDetails(users.get());
    }

    // Security Context에 저장된 User PK 가져오기
    public Optional<Long> getCurrentUserPk() {
        // Request가 들어올 때 SecurityContext에 Authentication 객체를 저장해서 사용
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("Security Context에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        String userPk = null;
        if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            userPk = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            userPk = (String) authentication.getPrincipal();
        }

        assert userPk != null : "User 정보를 찾을 수 없습니다.";
        return Optional.of(Long.parseLong(userPk));
    }
}
