package com.example.molecularsearch.api;

import com.example.molecularsearch.dto.ChemInfoDto;
import com.example.molecularsearch.service.ChemInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChemInfoController {

    private final ChemInfoService chemInfoService;

    @GetMapping("/api/search/chem")
    public ResponseEntity<ChemInfoDto> seearchChem(@RequestParam String name) {
        return ResponseEntity.ok(chemInfoService.getChemInfo(name));
    }
}
