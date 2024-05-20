package com.example.molecularsearch.chem_info.web.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConformerResponse {

    @JsonProperty("PC_Compounds")
    private List<CompoundList> compounds;

    public ConformerResponse() {
        this.compounds = Collections.emptyList();
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CompoundList {
        @JsonProperty("atoms")
        private Atoms atoms;    // 구성 원자

        @JsonProperty("bonds")
        private Bonds bonds;    // 결합

        @JsonProperty("coords")
        private List<Coords> coords;    // 좌표값
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Atoms {
        @JsonProperty("element")
        private ArrayList<Float> element; // 해당 원자의 주기율표 번호
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Bonds {
        @JsonProperty("aid1")
        private ArrayList<Integer> aid1;    // 첫 번째 원소

        @JsonProperty("aid2")
        private ArrayList<Integer> aid2;    // 두 번째 원소

        @JsonProperty("order")
        private ArrayList<Integer> order;   // 두 원소를 잇는 결합 수
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Coords {
        @JsonProperty("conformers")
        private List<Conformers> conformers;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Conformers {
        @JsonProperty("x")
        private ArrayList<Float> x; // x 좌표

        @JsonProperty("y")
        private ArrayList<Float> y; // y 좌표

        @JsonProperty("z")
        private ArrayList<Float> z; // z 좌표
    }
}
