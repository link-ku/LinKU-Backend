package com.linku.backend.domain.icon.service;

import com.linku.backend.domain.icon.Icon;
import com.linku.backend.domain.icon.repository.IconRepository;
import com.linku.backend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.linku.backend.global.common.enums.Status.ACTIVE;

@RequiredArgsConstructor
@Service
public class IconService {

    private final S3Uploader s3Uploader;
    private final IconRepository iconRepository;

    public void saveIconWithImageUpload(String iconName, MultipartFile multipartFile){
        String imgUrl = s3Uploader.uploadFile(multipartFile);
        Icon icon = Icon.builder()
                .name(iconName)
                .url(imgUrl)
                .build();
        icon.setStatus(ACTIVE);
        iconRepository.save(icon);
    }
}
