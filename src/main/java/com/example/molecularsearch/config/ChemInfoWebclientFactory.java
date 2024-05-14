package com.example.molecularsearch.config;

import com.example.molecularsearch.service.GenericWebclient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChemInfoWebclientFactory {

    /* SMILES 식으로 요청하는 WebClient */
    @Bean
    public GenericWebclient<String> webclientBySmiles() {
        return new GenericWebclient<>();
    }

    /* CID 식으로 요청하는 WebClient */
    @Bean
    public GenericWebclient<Long> webclientByCid() {
        return new GenericWebclient<>();
    }
}
