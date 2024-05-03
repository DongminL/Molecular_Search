package com.example.molecularsearch.aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.molecularsearch.aws.config.S3MockConfig;
import com.example.molecularsearch.exception.CustomException;
import com.example.molecularsearch.exception.ErrorCode;
import io.findify.s3mock.S3Mock;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@ActiveProfiles("test")
@Import(S3MockConfig.class)
public class AwsS3ServiceTest {

    @Autowired
    private AmazonS3 amazonS3;

    @AfterAll
    static void tearDown(@Autowired S3Mock s3Mock, @Autowired AmazonS3 amazonS3) {
        amazonS3.shutdown();
        s3Mock.stop();
    }

    @Test
    void AwsS3_저장() throws IOException {
        // given
        Long cid = 2244L;
        String base64File = "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";
        byte[] file = Base64.decodeBase64(base64File);

        // when
        // InputStreamResource -> MultipartFile로 변환
        MockMultipartFile image = new MockMultipartFile("uuid_random_" + cid, cid.toString(), "image/png", file);

        String fileName = UUID.randomUUID().toString().concat("_"+ image.getOriginalFilename());    // UUID로 난수화 하여 이름으로 저장

        // Image File의 Metadata 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        try {
            InputStream inputStream = image.getInputStream();   // Image byte의 InputStream 생성

            // 해당 S3 Bucket에 Image File 저장
            amazonS3.putObject(new PutObjectRequest("aws-s3-mock", fileName, inputStream, metadata)    // S3에 업로드
                    .withCannedAcl(CannedAccessControlList.PublicRead));    // 외부에 공개할 이미지임을 명시
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EXTERNAL_API_REQUEST_FAILED);
        }

        String urlPath = amazonS3.getUrl("aws-s3-mock", fileName).toString();
        System.out.println(urlPath);

        // then
        assertThat(urlPath).contains(cid.toString());
    }

}
