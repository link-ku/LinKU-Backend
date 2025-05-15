package com.linku.backend.domain.icon.service;

import com.linku.backend.domain.icon.Icon;
import com.linku.backend.domain.icon.repository.IconRepository;
import com.linku.backend.global.response.BaseResponse;
import com.linku.backend.global.response.ResponseCode;
import com.linku.backend.global.util.ImageCompressor;
import com.linku.backend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.linku.backend.global.common.enums.Status.ACTIVE;

@RequiredArgsConstructor
@Service
public class IconService {
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB

    private final S3Uploader s3Uploader;
    private final IconRepository iconRepository;

    public ResponseEntity<BaseResponse<String>> saveIconWithImageUpload(String iconName, MultipartFile multipartFile) {
        try {
            validateFileSize(multipartFile);

            byte[] compressedImage = compressImage(multipartFile);
            String imgUrl = uploadToS3(compressedImage, multipartFile);

            saveIconEntity(iconName, imgUrl);

            return ResponseEntity.ok(new BaseResponse<>(ResponseCode.SUCCESS, "아이콘 업로드 성공, URL: " + imgUrl));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new BaseResponse<>(ResponseCode.FAILURE, "아이콘 업로드 실패"));
        }
    }

    private void validateFileSize(MultipartFile multipartFile) {
        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("이미지 용량 초과 (최대 20MB)");
        }
    }

    private byte[] compressImage(MultipartFile multipartFile) throws IOException {
        return ImageCompressor.resizeAndCompress(multipartFile);
    }

    private String uploadToS3(byte[] imageData, MultipartFile multipartFile) {
        return s3Uploader.uploadFile(imageData, multipartFile.getOriginalFilename(), multipartFile.getContentType());
    }

    private void saveIconEntity(String iconName, String imgUrl) {
        Icon icon = Icon.builder()
                .name(iconName)
                .url(imgUrl)
                .build();
        icon.setStatus(ACTIVE);
        iconRepository.save(icon);
    }
}
