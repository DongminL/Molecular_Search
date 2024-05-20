package com.example.molecularsearch.config;

import com.example.molecularsearch.service.AwsS3Service;
import com.example.molecularsearch.service.GenericWebclient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChemInfoWebclientFactory {

    private final AwsS3Service awsS3Service;

    /* SMILES 식으로 요청하는 WebClient */
    @Bean
    public GenericWebclient<String> webclientBySmiles() {
        return new GenericWebclient<>(awsS3Service);
    }

    /* CID 식으로 요청하는 WebClient */
    @Bean
    public GenericWebclient<Long> webclientByCid() {
        return new GenericWebclient<>(awsS3Service);
    }
}
