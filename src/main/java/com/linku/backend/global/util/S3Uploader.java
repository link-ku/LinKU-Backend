package com.linku.backend.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class S3Uploader {
    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private static final String ICON_PREFIX = "icon_";

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    private final S3Client s3Client;

    public String uploadFile(byte[] data, String originalFilename, String contentType) {
        if (data == null || data.length == 0) {
            throw new RuntimeException("File data is empty");
        }
        String fileName = buildFileName(originalFilename);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType(contentType)
                .contentLength((long) data.length)
                .key(fileName)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));

        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return s3Client.utilities().getUrl(getUrlRequest).toString();
    }

    public String buildFileName(String originalFileName) {
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        String fileExtension = originalFileName.substring(fileExtensionIndex);
        String fileName = originalFileName.substring(0, fileExtensionIndex);
        String uuid = UUID.randomUUID().toString();

        return ICON_PREFIX + fileName + "_" + uuid + fileExtension;
    }
}
