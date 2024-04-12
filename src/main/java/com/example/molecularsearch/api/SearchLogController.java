package com.example.molecularsearch.api;

import com.example.molecularsearch.service.SearchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchLogController {

    private final SearchLogService searchLogService;



}
