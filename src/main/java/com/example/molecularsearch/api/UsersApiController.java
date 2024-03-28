package com.example.molecularsearch.api;

import com.example.molecularsearch.entity.Users;
import com.example.molecularsearch.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsersApiController {
    private final UsersService usersService;

    @PostMapping("/api")
    public void userSave(@RequestBody Users user) {
        usersService.save(user);
    }
}
