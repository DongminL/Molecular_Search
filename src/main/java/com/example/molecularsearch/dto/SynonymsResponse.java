package com.example.molecularsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SynonymsResponse {

    @JsonProperty("InformationList")
    private Information informationList;

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
            log.error(e.toString());
            return null;
        }
    }
}
