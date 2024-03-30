package com.example.molecularsearch.service;

import com.example.molecularsearch.entity.Users;
import com.example.molecularsearch.jwt.CustomUserDetails;
import com.example.molecularsearch.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {

    private final UsersRepository usersRepository;
    
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<Users> users = usersRepository.findById(Long.parseLong(userId));   // DB PK 값으로 찾기

        if (users.isEmpty()) {
            users = usersRepository.findByUserId(userId);   // User Id 값으로 찾기
        }

        if (users.isEmpty()) {
            log.error("Not found error");
            throw new RuntimeException("Not found error");
        }

        return new CustomUserDetails(users.get());
    }
}
