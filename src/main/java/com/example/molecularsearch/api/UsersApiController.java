package com.example.molecularsearch.api;

import com.example.molecularsearch.dto.NaverUserDto;
import com.example.molecularsearch.entity.Users;
import com.example.molecularsearch.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsersApiController {

    private final UsersService usersService;

    @PostMapping("/api/naver/login")
    public ResponseEntity<Users> userSave(@RequestBody NaverUserDto naverUserDto) {
        return
    }
}
