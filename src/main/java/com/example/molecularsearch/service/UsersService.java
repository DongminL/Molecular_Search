package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.GoogleUserDto;
import com.example.molecularsearch.dto.NaverUserDto;
import com.example.molecularsearch.entity.Users;
import com.example.molecularsearch.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final CustomUserDetailsService customUserService;
    private final JwtService jwtService;

    /* 네이버 로그인으로 회원가입 */
    @Transactional
    public Users signUp(NaverUserDto info) {
        if (usersRepository.existsByUserId(info.getUserId())) {
            log.error("이미 가입된 유저입니다!");
            throw new RuntimeException("Already existed user");
        }
        // Dto -> Entity
        Users users = Users.builder()
                .userId(info.getUserId())
                .name(info.getName())
                .email(info.getEmail())
                .nickname(info.getNickname())
                .mobile(info.getMobile())
                .gender(info.getGender())
                .profileImage(info.getProfileImage())
                .signUpType("NAVER")
                .roleType("RULE_USER")
                .build();

        return usersRepository.save(users); // 유저 정보 저장
    }

    /* 구글 로그인으로 회원가입 */
    @Transactional
    public Users signUp(GoogleUserDto info) {
        if(usersRepository.existsByUserId(info.getUserId())) {
            log.error("이미 가입된 유저입니다!");
            throw new RuntimeException("Already existed user");
        }
        // Dto -> Entity
        Users users = Users.builder()
                .userId(info.getUserId())
                .name(info.getName())
                .email(info.getEmail())
                .profileImage(info.getProfileImage())
                .signUpType("GOOGLE")
                .roleType("RULE_USER")
                .build();

        return usersRepository.save(users); // 유저 정보 저장
    }

    /* User ID로 DB에서 유저 정보 찾기 */
    @Transactional(readOnly = true)
    public Users searchByUserId(String userId) throws UsernameNotFoundException{
        return usersRepository.findByUserId(userId).orElseThrow(() ->
                new UsernameNotFoundException(userId + " 아이디의 사용자는 존재하지 않습니다!"));
    }

    /* Users Table의 PK로 DB에서 유저 정보 찾기 */
    @Transactional(readOnly = true)
    public Users searchByid(Long userPk) throws UsernameNotFoundException{
        return usersRepository.findById(userPk).orElseThrow(() ->
                new UsernameNotFoundException(userPk + "번 사용자는 존재하지 않습니다!"));
    }

    /* securityContext에 저장된 userId에 대한 정보만 가져오는 메소드 */
    @Transactional(readOnly = true)
    public Optional<Users> getUserInSecurityContext() {
        return customUserService.getCurrentUserId()
                .flatMap(usersRepository::findByUserId);
    }

    /* 유저 정보 삭제 */
    @Transactional
    public void deleteUser(Long id) {
        usersRepository.deleteById(id);
    }
}
