package com.example.molecularsearch.search_log.web;

import com.example.molecularsearch.search_log.web.dto.SearchLogDto;
import com.example.molecularsearch.exception.error.CustomException;
import com.example.molecularsearch.exception.error.ErrorCode;
import com.example.molecularsearch.search_log.service.SearchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SearchLogController {

    private final SearchLogService searchLogService;

    /* 검색 기록 가져오기 */
    @GetMapping("/api/get/log/search")
    public ResponseEntity<?> showSearchLog() {
        return ResponseEntity.ok(searchLogService.findSearchLog());
    }

    /* 검색 기록 단일 삭제 */
    @DeleteMapping("/api/delete/log/search/{searchLogId}")
    public ResponseEntity<String> editSearchLog(@PathVariable Long searchLogId) {
        searchLogService.deleteSearchLog(searchLogId);

        return ResponseEntity.ok("검색기록 삭제 완료");
    }

    /* 검색 기록 편집을 통해 삭제 */
    @DeleteMapping("/api/edit/log/search")
    public ResponseEntity<String> editSearchLog(@RequestBody Map<String, List<SearchLogDto>> mapSearchLogDtos) {
        try {
            mapSearchLogDtos.get("searchLogList");
        } catch (NullPointerException e) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        searchLogService.editSearchLog(mapSearchLogDtos.get("searchLogList"));

        return ResponseEntity.ok("검색기록 삭제 완료");
    }

    /* 검색 기록 전체 삭제 */
    @DeleteMapping("/api/delete/all/log/search")
    public ResponseEntity<String> deleteAllSearchLog() {
        searchLogService.deleteAllSearchLog();

        return ResponseEntity.ok("검색기록 삭제 완료");
    }
}
