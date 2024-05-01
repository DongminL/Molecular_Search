package com.example.molecularsearch.api;

import com.example.molecularsearch.dto.InfoBookmarkDto;
import com.example.molecularsearch.service.InfoBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class InfoBookmarkController {

    private final InfoBookmarkService infoBookmarkService;

    /* 즐겨찾기 추가 */
    @PostMapping("/api/save/bookmark/{chemId}")
    public ResponseEntity<?> addInfoFav(@PathVariable String chemId) {
        infoBookmarkService.saveInfoFav(chemId);
        return ResponseEntity.ok("즐겨찾기 추가 성공");
    }

    /* 즐겨찾기 리스트 */
    @GetMapping("/api/search/bookmark")
    public ResponseEntity<?> showInfoFav() {
        return ResponseEntity.ok(infoBookmarkService.getBookmarkList());
    }

    /* 즐겨찾기 단일 삭제 */
    @DeleteMapping("/api/delete/bookmark/{infoBookmarkId}")
    public ResponseEntity<String> editInfoFav(@PathVariable Long infoBookmarkId) {
        infoBookmarkService.deleteInfoBookmark(infoBookmarkId);

        return ResponseEntity.ok("검색기록 삭제 완료");
    }

    /* 즐겨찾기 편집을 통해 삭제 */
    @DeleteMapping("/api/edit/bookmark")
    public ResponseEntity<String> editInfoFav(@RequestBody Map<String, List<InfoBookmarkDto>> mapInfoFavDtos) {
        infoBookmarkService.editInfoBookmark(mapInfoFavDtos.get("infoFavList"));

        return ResponseEntity.ok("검색기록 삭제 완료");
    }

    /* 즐겨찾기 유무 */
    @GetMapping("/api/check/bookmark/{chemId}")
    public ResponseEntity<?> checkInfoFav(@PathVariable String chemId) {
        return ResponseEntity.ok(infoBookmarkService.checkInfoBookmark(chemId));
    }
}