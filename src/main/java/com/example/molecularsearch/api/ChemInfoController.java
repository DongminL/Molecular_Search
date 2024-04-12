package com.example.molecularsearch.api;

import com.example.molecularsearch.service.ChemInfoService;
import com.example.molecularsearch.service.SearchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChemInfoController {

    private final ChemInfoService chemInfoService;
    private final SearchLogService searchLogService;

//    /* 분자 이름 또는 화학식으로 분자 정보 검색 */
//    @GetMapping(value = "/api/search/chem", params = "name")
//    public ResponseEntity<?> seearchChem(@RequestParam String name) {
//        return ResponseEntity.ok(chemInfoService.saveChemInfo(chemInfoService.getChemInfoByName(name)));
//    }

    /* SMILES 식을 통한 분자정보 검색 */
    @GetMapping(value = "/api/search/chem", params = "smiles")
    public ResponseEntity<?> smilesChem(@RequestParam String smiles) {
        searchLogService.saveSearchLog(smiles);
        return ResponseEntity.ok(chemInfoService.searchSmiles(smiles));
    }

    /* cid 값으로 분자 정보를 가져와 저장 */
    @PostMapping(value = "/api/save/chem")
    public ResponseEntity<?> saveInfo(@RequestBody Map<String, Long> cidMap) {
        return ResponseEntity.ok(chemInfoService.saveInfoByCid(cidMap.get("cid")));
    }
}
