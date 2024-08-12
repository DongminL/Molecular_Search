package com.example.molecularsearch.search_log.repository;

import com.example.molecularsearch.search_log.entity.SearchLog;
import com.example.molecularsearch.users.entity.Users;
import com.example.molecularsearch.users.repository.UsersRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 클래스 당 하나의 인스턴스 사용
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)    // 인메모리 DB 사용하지 않음
class SearchLogRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SearchLogRepository searchLogRepository;

    /* 더미 데이터 생성 */
    @BeforeAll()
    void setUp() {
        List<SearchLog> logList = new ArrayList<>();
        Users user = Users.builder()
                .name("a").email("a").userId("a").gender("a").mobile("a").nickname("a").roleType("RULE_USER").signUpType("NAVER")
                .build();

        for (long i = 1; i <= 5; i++) {
            logList.add(SearchLog.builder()
                    .id(i)
                    .user(user)
                    .log("검색기록-" + i).build());
        }

        usersRepository.save(user);
        searchLogRepository.saveAll(logList);
    }

    /* 각 테스트의 마지막에 유저 데이터 삭제 */
    @AfterAll
    void tearDown() {
        searchLogRepository.deleteAllInBatch();
        usersRepository.deleteById(1L);
    }

    @Test
    @Transactional  // 테스트 종료 후 Rollback 됨
    @DisplayName("JPA: deleteAllById로 리스트 삭제")
    void deleteAllById() {
        // given
        List<Long> idList = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            idList.add(i);
        }

        // when
        searchLogRepository.deleteAllById(idList);

        // then
        assertEquals(0, searchLogRepository.count());
    }

    @Test
    @Transactional  // 테스트 종료 후 Rollback 됨
    @DisplayName("JPA: deleteAllByIdInBatch로 리스트 삭제")
    void deleteAllByIdInBatch() {
        // given
        List<Long> idList = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            idList.add(i);
        }

        // when
        searchLogRepository.deleteAllByIdInBatch(idList);

        // then
        assertEquals(0, searchLogRepository.count());
    }

    @Test
    @Transactional  // 테스트 종료 후 Rollback 됨
    @DisplayName("JPQL: @Query로 리스트 삭제")
    void deleteAllByList() {
        // given
        List<Long> idList = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            idList.add(i);
        }

        // when
        searchLogRepository.deleteAllByList(idList);

        // then
        assertEquals(0, searchLogRepository.count());
    }
}