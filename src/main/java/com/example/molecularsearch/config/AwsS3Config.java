package com.example.molecularsearch.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")   // Test 시 영향 X
public class AwsS3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;   // 발급받은 Access Key

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;   // 발급받은 Secret Key

    @Value("${cloud.aws.region.static}")
    private String region;  // S3를 사용할 지역

    /* Amazon S3 서비스 연결 설정 */
    @Bean
    public AmazonS3 s3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}
