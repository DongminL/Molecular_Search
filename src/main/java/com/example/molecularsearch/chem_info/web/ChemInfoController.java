package com.example.molecularsearch.chem_info.web;

import com.example.molecularsearch.chem_info.service.ChemInfoService;
import com.example.molecularsearch.chem_info.service.SynonymsService;
import com.example.molecularsearch.chem_info.web.api.GenericWebclient;
import com.example.molecularsearch.search_log.service.SearchLogService;
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
    private final GenericWebclient<Long> webclientBycid;

    /* 분자 이름 또는 화학식으로 분자 정보 검색 */
    @GetMapping(value = "/api/search/chem", params = {"keyword", "page"})
    public ResponseEntity<?> seearchChem(@RequestParam("keyword") String keyword, @RequestParam(value = "page", defaultValue = "0") int page) {
        searchLogService.saveSearchLog(keyword);
        return ResponseEntity.ok(synonymsService.searchChemInfo(keyword, page));
    }

    /* SMILES 식을 통한 분자정보 검색 */
    @GetMapping(value = "/api/search/chem", params = "smiles")
    public ResponseEntity<?> smilesChem(@RequestParam String smiles) {
        return ResponseEntity.ok(chemInfoService.searchSmiles(smiles));
    }

    /* cid 값으로 분자 정보를 가져와 저장 */
    @PostMapping("/api/save/chem")
    public ResponseEntity<?> saveInfo(@RequestBody Map<String, Long> cidMap) {
        return ResponseEntity.ok(chemInfoService.saveInfoByCid(cidMap.get("cid")));
    }

    /* 즐겨찾기에서 클릭 시 chem_id를 통한 분자정보 가져오기 */
    @GetMapping("/api/search/{chemId}")
    public ResponseEntity<?> idChem(@PathVariable String chemId) {
        return ResponseEntity.ok(chemInfoService.findChemInfoById(chemId));
    }

    @GetMapping("/test/{cid}")
    public ResponseEntity<?> test(@PathVariable Long cid) {
        return ResponseEntity.ok(chemInfoService.update3DImage(cid));
    }
}
