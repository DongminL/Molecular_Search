package com.example.molecularsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SynonymsResponse {

    @JsonProperty("InformationList")
    private Information informationList;

    public SynonymsResponse() {
        this.informationList = null;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Information {
        @JsonProperty("Information")
        private List<SynonymsList> information;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class SynonymsList {
        @JsonProperty("Synonym")
        private List<String> synonyms;  // 해당 분자의 Synonyms 정보
    }

    public List<String> getSynonyms() {
        try {
            return informationList.information.get(0).synonyms;
        } catch (Exception e) {
            log.debug("Synonyms Null : timestemp: {}", LocalDateTime.now());
            return null;
        }
    }
}
