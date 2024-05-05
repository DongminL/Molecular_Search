package com.example.molecularsearch.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.molecularsearch.dto.ByteMultpartFile;
import com.example.molecularsearch.exception.CustomException;
import com.example.molecularsearch.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;  // 사용할 S3 Bucket

    /* AWS S3에 이미지 저장 후 해당 이미지를 볼 수 있는 URL 받아오기 */
    public String saveImage(Long cid, byte[] byteImage) {
        String uuid = UUID.randomUUID().toString().replace("-", "");    // UUID 생성 후 "-"값 제거
        String fileName = uuid.concat("-" + cid);   // 파일 이름

        // InputStreamResource -> MultipartFile로 변환
        MultipartFile image = new ByteMultpartFile(fileName, byteImage);

        // Image File의 Metadata 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        try {
            InputStream inputStream = image.getInputStream();   // Image byte의 InputStream 생성

            // 해당 S3 Bucket에 Image File 저장
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)    // S3에 업로드
                    .withCannedAcl(CannedAccessControlList.PublicRead));    // 외부에 공개할 이미지임을 명시
        } catch (Exception e) {
            log.error("Image를 S3에 저장 실패", e);
            throw new CustomException(ErrorCode.EXTERNAL_API_REQUEST_FAILED);
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }
}
