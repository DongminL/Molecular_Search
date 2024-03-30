package com.example.molecularsearch.service;

import com.example.molecularsearch.entity.Users;
import com.example.molecularsearch.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    @Transactional
    public void save(Users user) {
        log.info(user.toString());
        usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<Users> searchUserId(String userId) {
        Optional<Users> users = usersRepository.findByUserId(userId);

        if (users.isEmpty()) {
            log.error("Not found error");
            throw new RuntimeException("Not found error");
        }

        return users;
    }
}
