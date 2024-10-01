package com.example.molecularsearch.bookmark.web;

import com.example.molecularsearch.bookmark.service.InfoBookmarkService;
import com.example.molecularsearch.bookmark.web.dto.BookmarkListDto;
import com.example.molecularsearch.exception.error.CustomException;
import com.example.molecularsearch.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InfoBookmarkController {

    private final InfoBookmarkService infoBookmarkService;

    /* 즐겨찾기 추가 */
    @PostMapping("/api/save/bookmark/{chemId}")
    public ResponseEntity<?> addInfoBookmark(@PathVariable String chemId) {
        infoBookmarkService.saveInfoBookmark(chemId);

        return ResponseEntity.ok("즐겨찾기 추가 성공");
    }

    /* 즐겨찾기 해제 */
    @DeleteMapping("/api/off/bookmark/{chemId}")
    public ResponseEntity<String> offInfoBookmark(@PathVariable String chemId) {
        infoBookmarkService.deleteInfoBookmark(chemId);

        return ResponseEntity.ok("즐겨찾기 해제 완료");
    }

    /* 즐겨찾기 리스트 */
    @GetMapping("/api/search/bookmark")
    public ResponseEntity<BookmarkListDto> showInfoBookmark() {
        return ResponseEntity.ok(infoBookmarkService.getBookmarkList());
    }

    /* 즐겨찾기 단일 삭제 */
    @DeleteMapping("/api/delete/bookmark/{infoBookmarkId}")
    public ResponseEntity<String> deleteInfoBookmark(@PathVariable Long infoBookmarkId) {
        infoBookmarkService.deleteInfoBookmark(infoBookmarkId);

        return ResponseEntity.ok("즐겨찾기 삭제 완료");
    }

    /* 즐겨찾기 편집을 통해 삭제 */
    @DeleteMapping("/api/edit/bookmark")
    public ResponseEntity<String> editInfoBookmark(@RequestBody BookmarkListDto bookmarkListDto) {
        try {
            infoBookmarkService.editInfoBookmark(bookmarkListDto.getBookmarkDtoList());
        } catch (NullPointerException e) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        return ResponseEntity.ok("즐겨찾기 삭제 완료");
    }

    /* 즐겨찾기 전체 삭제 */
    @DeleteMapping("/api/delete/all/bookmark")
    public ResponseEntity<String> deleteAllInfoBookmark() {
        infoBookmarkService.deleteAllInfoBookmark();

        return ResponseEntity.ok("즐겨찾기 삭제 완료");
    }

    /* 즐겨찾기 유무 */
    @GetMapping("/api/check/bookmark/{chemId}")
    public ResponseEntity<?> checkInfoFav(@PathVariable String chemId) {
        return ResponseEntity.ok(infoBookmarkService.checkInfoBookmark(chemId));
    }
}
