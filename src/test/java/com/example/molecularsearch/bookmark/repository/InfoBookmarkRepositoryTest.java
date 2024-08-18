package com.example.molecularsearch.bookmark.repository;

import com.example.molecularsearch.bookmark.entity.InfoBookmark;
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
@DisplayName("Select 선행 문제 분석")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 클래스 당 하나의 인스턴스 사용
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)    // 인메모리 DB 사용하지 않음
class InfoBookmarkRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private InfoBookmarkRepository infoBookmarkRepository;

    private static Users user;

    /* 유저 더미 데이터 생성 */
    @BeforeAll()
    void setUp() {
         user = Users.builder()
                .name("a").email("a").userId("a").gender("a").mobile("a").nickname("a").roleType("RULE_USER").signUpType("NAVER")
                .build();

        user = usersRepository.save(user);
    }

    /* 테스트의 마지막에 유저 데이터 삭제 */
    @AfterAll
    void tearDown() {
        usersRepository.delete(user);
    }

    @Test
    @Transactional
    @DisplayName("Insert 시 Select 선행 X")
    void onlyInsert() {
        // given
        List<InfoBookmark> bookmarkList = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            bookmarkList.add(InfoBookmark.builder()
                    .chemInfoId(String.valueOf(i))
                    .user(user)
                    .molecularFormula("화학식 - " + i).build());
        }

        // when
        infoBookmarkRepository.saveAll(bookmarkList);

        // then
        assertEquals(5, infoBookmarkRepository.count());
    }

    @Test
    @Transactional
    @DisplayName("Insert 시 Select 선행 O")
    void precedeSelect() {
        // given
        List<InfoBookmark> bookmarkList = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            bookmarkList.add(InfoBookmark.builder()
                    .id(i)
                    .chemInfoId(String.valueOf(i))
                    .user(user)
                    .molecularFormula("화학식 - " + i).build());
        }

        // when
        infoBookmarkRepository.saveAll(bookmarkList);

        // then
        assertEquals(5, infoBookmarkRepository.count());
    }

    @Test
    @Transactional  // 단위 테스트할 땐 주석처리 하기
    @DisplayName("Update 시 Select 선행 O")
    void update() {
        // given
        user.changeNicname("바뀐 닉네임");

        // when
        usersRepository.save(user);

        // then
        usersRepository.findById(1L).ifPresent(updatedUser -> assertEquals("바뀐 닉네임", updatedUser.getNickname()));
    }

    /*
    * 이 테스트의 경우, UPDATE 쿼리만 발생하지만 Detached Entity로 인해 변경이 제대로 반영되지 않음
    * modifiedDate만 초기화 되는 것이 아닌 createdDate도 같이 초기화 되는 문제가 발생함
    */
    @Test
    @Transactional
    @DisplayName("Update 시 Select 선행 X")
    void onlyUpdate() {
        // given
        user.changeNicname("바뀐 닉네임2");

        // when
        usersRepository.updateNickname(user);

        // then
        usersRepository.findById(1L).ifPresent(updatedUser -> {
            assertEquals(updatedUser.getCreatedDate(), updatedUser.getModifiedDate());
            assertEquals("바뀐 닉네임2", updatedUser.getNickname());

            System.out.println("createdDate: " + updatedUser.getCreatedDate());
            System.out.println("modifiedDate: " + updatedUser.getModifiedDate());
        });
    }

    @Test
    @DisplayName("Delete 시 Select 선행 O")
    void delete() {
        // given
        InfoBookmark bookmark = InfoBookmark.builder()
                .chemInfoId("1")
                .user(user)
                .molecularFormula("화학식 - " + 1).build();

        // when
        bookmark = infoBookmarkRepository.save(bookmark);
        infoBookmarkRepository.delete(bookmark);

        // then
        assertEquals(0, infoBookmarkRepository.count());
    }

    @Test
    @Transactional
    @DisplayName("Delete 시 Select 선행 X")
    void onlyDelete() {
        // given
        InfoBookmark bookmark = InfoBookmark.builder()
                .chemInfoId("1")
                .user(user)
                .molecularFormula("화학식 - " + 1).build();

        // when
        bookmark = infoBookmarkRepository.save(bookmark);
        infoBookmarkRepository.onlyDelete(bookmark);

        // then
        assertEquals(0, infoBookmarkRepository.count());
    }
}