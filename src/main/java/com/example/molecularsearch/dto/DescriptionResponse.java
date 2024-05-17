package com.example.molecularsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DescriptionResponse {

    @JsonProperty("InformationList")
    private Information informationList;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Information {
        @JsonProperty("Information")
        private List<DescriptionList> information;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class DescriptionList {
        @JsonProperty("Description")
        private String description;  // 해당 분자의 Description 정보
    }

    public String getDescription() {
        try {
            return informationList.information.get(1).getDescription();
        } catch (IndexOutOfBoundsException e) {
            log.debug("Description Null : timestemp: {}", LocalDateTime.now());
            return null;
        }
    }
}
