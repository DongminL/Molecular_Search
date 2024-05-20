package com.example.molecularsearch.chem_info.web.dto;

import com.example.molecularsearch.chem_info.domain.ChemInfo;
import com.example.molecularsearch.chem_info.web.api.dto.ConformerResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.*;
import java.util.stream.Stream;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Deserialize를 필요한 필드만 적용
public class ChemInfoDto {

    @JsonProperty("id")
    private String id;    // ChemInfo PK

    @JsonProperty("cid")
    private Long cid;   // 분자 고유 ID

    @JsonProperty("inpac_name")
    private String inpacName;   // 유기 화합물 이름

    @JsonProperty("molecular_formula")
    private String molecularFormula; // 화학식

    @JsonProperty("molecular_weight")
    private Double molecularWeight; // 분자량 (g/mol)

    @JsonProperty("isomeric_smiles")
    private String isomericSmiles;  // 이성질체 SMILES

    @JsonProperty("inchi")
    private String inchi;   // 국제 화학 식별자

    @JsonProperty("inchiKey")
    private String inchiKey;    // InChI Key 값

    @JsonProperty("canonical_smiles")
    private String canonicalSmiles; // 표준 SMILES

    @JsonProperty("synonyms")
    private List<String> synonyms;    // 비슷한 화합물들

    @JsonProperty("description")
    private String description; // 화합물에 대한 설명

    @JsonProperty("image_2D_url")
    private String image2DUrl;   // 2D 이미지 경로

    @JsonProperty("image_3D_conformer")
    private Map<String, ArrayList<?>> image3DConformer;   // 3D 이미지 정보

    /* Dto -> Entity로 변환 */
    public ChemInfo toEntity() {
        return ChemInfo.builder()
                .cid(this.cid)
                .inpacName(this.inpacName)
                .molecularFormula(this.molecularFormula)
                .molecularWeight(this.molecularWeight)
                .inchi(this.inchi)
                .inchiKey(this.inchiKey)
                .canonicalSmiles(this.canonicalSmiles)
                .isomericSmiles(this.isomericSmiles)
                .description(this.description)
                .image2DUrl(this.getImage2DUrl())
                .image3DConformer(this.image3DConformer)
                .build();
    }

    /* Dto -> Entity로 변환 */
    public ChemInfo toEntity(List<String> synonyms) {
        return ChemInfo.builder()
                .cid(this.cid)
                .inpacName(this.inpacName)
                .molecularFormula(this.molecularFormula)
                .molecularWeight(this.molecularWeight)
                .inchi(this.inchi)
                .inchiKey(this.inchiKey)
                .canonicalSmiles(this.canonicalSmiles)
                .isomericSmiles(this.isomericSmiles)
                .synonyms(synonyms)
                .description(this.description)
                .image2DUrl(this.getImage2DUrl())
                .image3DConformer(this.image3DConformer)
                .build();
    }

    /* Entity -> Dto로 변환 */
    public ChemInfoDto(ChemInfo chemInfo, List<String> synonyms) {
        this.id = chemInfo.getId();
        this.cid = chemInfo.getCid();
        this.inpacName = chemInfo.getInpacName();
        this.molecularFormula = chemInfo.getMolecularFormula();
        this.molecularWeight = chemInfo.getMolecularWeight();
        this.isomericSmiles = chemInfo.getIsomericSmiles();
        this.inchi = chemInfo.getInchi();
        this.inchiKey = chemInfo.getInchiKey();
        this.canonicalSmiles = chemInfo.getCanonicalSmiles();
        this.synonyms = synonyms;
        this.description = chemInfo.getDescription();
        this.image2DUrl = chemInfo.getImage2DUrl();
        this.image3DConformer = chemInfo.getImage3DConformer();
    }

    /* Synonyms 정보 업데이트 */
    public void updateSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    /* Description 정보 업데이트 */
    public void updateDescription(String description) {
        this.description = description;
    }

    /* 2D Image URL 정보 업데이트 */
    public void update2DImage(String image2DUrl) {
        this.image2DUrl = image2DUrl;
    }

    /* 3D Images 정보 업데이트 */
    public void update3DImage(ConformerResponse conformer) {
        // 좌표에 대한 정보 (4개의 배열을 하나로 합치기)
        ArrayList<Float> coords = new ArrayList<>(Stream.of(conformer.getCompounds().get(0).getCoords().get(0).getConformers().get(0).getX(),
                        conformer.getCompounds().get(0).getCoords().get(0).getConformers().get(0).getY(),
                        conformer.getCompounds().get(0).getCoords().get(0).getConformers().get(0).getZ(),
                        conformer.getCompounds().get(0).getAtoms().getElement())
                .flatMap(Collection::stream)
                .toList());

        // 결합에 대한 정보 (3개의 배열을 하나로 합치기)
        ArrayList<Integer> bonds = new ArrayList<>(Stream.of(conformer.getCompounds().get(0).getBonds().getAid1(),
                        conformer.getCompounds().get(0).getBonds().getAid2(),
                        conformer.getCompounds().get(0).getBonds().getOrder())
                .flatMap(Collection::stream)
                .toList());

        Map<String, ArrayList<?>> image3DConformer = new HashMap<>();
        image3DConformer.put("coords", coords);
        image3DConformer.put("bonds", bonds);

        this.image3DConformer = image3DConformer;
    }
}
