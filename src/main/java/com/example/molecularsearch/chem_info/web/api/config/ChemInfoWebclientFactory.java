package com.example.molecularsearch.chem_info.web.api.config;

import com.example.molecularsearch.aws.service.AwsS3Service;
import com.example.molecularsearch.chem_info.repository.ChemInfoRepository;
import com.example.molecularsearch.chem_info.web.api.GenericWebclient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChemInfoWebclientFactory {

    private final AwsS3Service awsS3Service;
    private final ChemInfoRepository chemInfoRepository;

    /* SMILES 식으로 요청하는 WebClient */
    @Bean
    public GenericWebclient<String> webclientBySmiles() {
        return new GenericWebclient<>(awsS3Service, chemInfoRepository);
    }

    /* CID 식으로 요청하는 WebClient */
    @Bean
    public GenericWebclient<Long> webclientByCid() {
        return new GenericWebclient<>(awsS3Service, chemInfoRepository);
    }
}
