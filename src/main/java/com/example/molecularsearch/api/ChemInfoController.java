package com.example.molecularsearch.api;

import com.example.molecularsearch.service.ChemInfoService;
import com.example.molecularsearch.service.ChemInfoWebClient;
import com.example.molecularsearch.service.SearchLogService;
import com.example.molecularsearch.service.SynonymsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChemInfoController {

    private final ChemInfoService chemInfoService;
    private final SearchLogService searchLogService;
    private final SynonymsService synonymsService;
    private final ChemInfoWebClient chemInfoWebClient;

    /* 분자 이름 또는 화학식으로 분자 정보 검색 */
    @GetMapping(value = "/api/search/chem", params = {"keyword", "page"})
    public ResponseEntity<?> seearchChem(@RequestParam("keyword") String keyword, @RequestParam(value = "page", defaultValue = "0") int page) {
        searchLogService.saveSearchLog(keyword);
        return ResponseEntity.ok(synonymsService.searchChemInfo(keyword, page));
    }

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

    /* 즐겨찾기에서 chem_id를 통한 분자정보 가져오기 */
    @GetMapping(value = "/api/search/{chemId}")
    public ResponseEntity<?> idChem(@PathVariable String chemId) {
        return ResponseEntity.ok(chemInfoService.findChemInfoById(chemId));
    }
}
