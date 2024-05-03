package com.example.molecularsearch.aws.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.findify.s3mock.S3Mock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@TestConfiguration
public class S3MockConfig {

    private final String bucketName = "aws-s3-mock";    // 가상의 버킷 이름

    private final String region = "US_Standard";  // 가상의 S3를 사용할 지역

    /* S3Mock 생성 */
    @Primary
    @Bean(name = "s3Mock")
    public S3Mock s3Mock() {
        return new S3Mock.Builder()
                .withPort(8000) // 8000번 포트 사용
                .withInMemoryBackend()  // 메모리에 파일 저장
                .build();
    }

    /* S3Mock 연결 설정 */
    @Bean(name = "amazonS3", destroyMethod = "shutdown")
    public AmazonS3Client s3MockClient(S3Mock s3Mock) {
        s3Mock.start();

        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", region);

        AmazonS3Client amazonS3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpointConfiguration)
                .build();

            amazonS3Client.createBucket(bucketName);

        return amazonS3Client;
    }
}
