package com.example.molecularsearch.api;

import com.example.molecularsearch.dto.SearchLogDto;
import com.example.molecularsearch.service.SearchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SearchLogController {

    private final SearchLogService searchLogService;

    @GetMapping("/api/get/log/search")
    public ResponseEntity<?> showSearchLog() {
        return ResponseEntity.ok(searchLogService.findSearchLog());
    }

    @DeleteMapping("/api/delete/log/search/{searchLogId}")
    public ResponseEntity<String> editSearchLog(@PathVariable Long searchLogId) {
        searchLogService.deleteSearchLog(searchLogId);

        return ResponseEntity.ok("검색기록 삭제 완료");
    }

    @DeleteMapping("/api/edit/search-log")
    public ResponseEntity<String> editSearchLog(@RequestBody Map<String, List<SearchLogDto>> searchLogDtos) {
        searchLogService.editSearchLog(searchLogDtos.get("searchLogList"));

        return ResponseEntity.ok("검색기록 삭제 완료");
    }

}
