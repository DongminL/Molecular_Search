package com.example.molecularsearch.api;

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

    /* 분자 이름 또는 화학식으로 분자 정보 검색 */
    @GetMapping(value = "/api/search/chem", params = "name")
    public ResponseEntity<?> seearchChem(@RequestParam String name) {
        return ResponseEntity.ok(chemInfoService.saveChemInfo(chemInfoService.getChemInfoByName(name)));
    }

    /* SMILES 식을 통한 분자정보 검색 */
    @GetMapping(value = "/api/search/chem", params = "smiles")
    public ResponseEntity<?> smilesChem(@RequestParam String smiles) {
        return ResponseEntity.ok(chemInfoService.searchSmiles(smiles));
    }
}
