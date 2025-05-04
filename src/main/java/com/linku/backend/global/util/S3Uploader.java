package com.linku.backend.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class S3Uploader {
    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private static final String ICON_PREFIX = "icon_";

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    private final S3Client s3Client;

    public String uploadFile(MultipartFile multipartFile) {
        if(multipartFile.isEmpty()) {
            throw new RuntimeException("MultipartFile is empty");
        }
        String fileName = getFileName(multipartFile);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .contentType(multipartFile.getContentType())
                    .contentLength(multipartFile.getSize())
                    .key(fileName)
                    .build();
            RequestBody requestBody = RequestBody.fromBytes(multipartFile.getBytes());
            s3Client.putObject(putObjectRequest, requestBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return s3Client.utilities().getUrl(getUrlRequest).toString();
    }

    public String getFileName(MultipartFile multipartFile) {
        return buildFileName(multipartFile.getOriginalFilename());
    }

    public String buildFileName(String originalFileName) {
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        String fileExtension = originalFileName.substring(fileExtensionIndex);
        String fileName = originalFileName.substring(0, fileExtensionIndex);
        String uuid = UUID.randomUUID().toString();

        return ICON_PREFIX + fileName + "_" + uuid + fileExtension;
    }
}