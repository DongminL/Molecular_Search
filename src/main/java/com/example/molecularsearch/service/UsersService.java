package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.GoogleUserDto;
import com.example.molecularsearch.dto.NaverUserDto;
import com.example.molecularsearch.entity.Users;
import com.example.molecularsearch.exception.CustomException;
import com.example.molecularsearch.exception.ErrorCode;
import com.example.molecularsearch.repository.InfoBookmarkRepository;
import com.example.molecularsearch.repository.SearchLogRepository;
import com.example.molecularsearch.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final CustomUserDetailsService customUserService;
    private final SearchLogRepository searchLogRepository;
    private final InfoBookmarkRepository infoBookmarkRepository;

    /* 네이버 로그인으로 회원가입 */
    @Transactional
    public Users signUp(NaverUserDto info) {
        if (usersRepository.existsByUserId(info.getUserId())) {
            log.error("이미 가입된 유저, user_id: {}, timestemp: {}", info.getUserId(), LocalDateTime.now());
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
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
        
        log.info("네이버 회원가입, user_id: {}, timestemp: {}", info.getUserId(), LocalDateTime.now());

        return usersRepository.save(users); // 유저 정보 저장
    }

    /* 구글 로그인으로 회원가입 */
    @Transactional
    public Users signUp(GoogleUserDto info) {
        if(usersRepository.existsByUserId(info.getUserId())) {
            log.error("이미 가입된 유저, user_id: {}, timestemp: {}", info.getUserId(), LocalDateTime.now());
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
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

        log.info("구글 회원가입, user_id: {}, timestemp: {}", info.getUserId(), LocalDateTime.now());

        return usersRepository.save(users); // 유저 정보 저장
    }

    /* User ID로 DB에서 유저 정보 찾기 */
    @Transactional(readOnly = true)
    public Users searchByUserId(String userId) throws UsernameNotFoundException {
        return usersRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    /* Users Table의 PK로 DB에서 유저 정보 찾기 */
    @Transactional(readOnly = true)
    public Users searchByid(Long userPk) throws UsernameNotFoundException {
        return usersRepository.findById(userPk).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    /* Security Context에 저장된 Users PK에 대한 정보만 가져오는 메소드 */
    @Transactional(readOnly = true)
    public Optional<Users> getUserInSecurityContext() {
        return customUserService.getCurrentUserPk()
                .flatMap(usersRepository::findById);
    }

    /* 유저 정보 삭제 */
    @Transactional
    public void deleteUser() {
        getUserInSecurityContext().ifPresent(user -> {
            searchLogRepository.deleteAllByUser(user);    // 해당 유저의 모든 검색 기록 삭제
            infoBookmarkRepository.deleteAllByUser(user);  // 해당 유저의 모든 즐겨찾기 기록 삭제
            usersRepository.deleteById(user.getId());   // 해당 유저 정보 삭제

            log.info("회원탈퇴, user_PK: {}, timestemp: {}", user.getId(), LocalDateTime.now());
        });
    }
}
