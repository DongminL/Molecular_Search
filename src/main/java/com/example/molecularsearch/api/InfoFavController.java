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
public class InfoFavController {

    private final InfoBookmarkService infoBookmarkService;

    /* 즐겨찾기 추가 */
    @PostMapping("/api/save/fav/{chemId}")
    public ResponseEntity<?> addInfoFav(@PathVariable String chemId) {
        infoBookmarkService.saveInfoFav(chemId);
        return ResponseEntity.ok("즐겨찾기 추가 성공");
    }

    /* 즐겨찾기 리스트 */
    @GetMapping("/api/search/fav")
    public ResponseEntity<?> showInfoFav() {
        return ResponseEntity.ok(infoBookmarkService.getBookmarkList());
    }

    /* 즐겨찾기 단일 삭제 */
    @DeleteMapping("/api/delete/fav/{infoFavId}")
    public ResponseEntity<String> editInfoFav(@PathVariable Long infoFavId) {
        infoBookmarkService.deleteInfoBookmark(infoFavId);

        return ResponseEntity.ok("검색기록 삭제 완료");
    }

    /* 즐겨찾기 편집을 통해 삭제 */
    @DeleteMapping("/api/edit/fav")
    public ResponseEntity<String> editInfoFav(@RequestBody Map<String, List<InfoBookmarkDto>> mapInfoFavDtos) {
        infoBookmarkService.editInfoBookmark(mapInfoFavDtos.get("infoFavList"));

        return ResponseEntity.ok("검색기록 삭제 완료");
    }

    /* 즐겨찾기 유무 */
    @GetMapping("/api/check/fav/{chemId}")
    public ResponseEntity<?> checkInfoFav(@PathVariable String chemId) {
        return ResponseEntity.ok(infoBookmarkService.checkInfoBookmark(chemId));
    }
}
