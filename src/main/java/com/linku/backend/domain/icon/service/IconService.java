package com.linku.backend.domain.icon.service;

import com.linku.backend.domain.common.enums.Status;
import com.linku.backend.domain.icon.Icon;
import com.linku.backend.domain.icon.dto.IconMapper;
import com.linku.backend.domain.icon.dto.response.IconInfoResponse;
import com.linku.backend.domain.icon.repository.IconRepository;
import com.linku.backend.domain.user.User;
import com.linku.backend.domain.user.repository.UserRepository;
import com.linku.backend.global.exception.LinkuException;
import com.linku.backend.global.response.ResponseCode;
import com.linku.backend.global.util.ImageCompressor;
import com.linku.backend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IconService {

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB

    private final IconRepository iconRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public IconInfoResponse saveIconWithImageUpload(String iconName, MultipartFile file) {
        try {
            validateFile(file);

            byte[] compressedImage = ImageCompressor.resizeAndCompress(file);
            String imgUrl = s3Uploader.uploadFile(compressedImage, file.getOriginalFilename(), file.getContentType());

            Long userId = getCurrentUserId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> LinkuException.of(ResponseCode.USER_NOT_FOUND));

            Icon icon = Icon.builder()
                    .name(iconName)
                    .imageUrl(imgUrl)
                    .owner(user)
                    .isDefault(false)
                    .build();
            icon.setStatus(Status.ACTIVE);

            iconRepository.save(icon);
            
            return IconMapper.toIconInfoResponse(icon);

        } catch (IOException e) {
            throw LinkuException.of(ResponseCode.ICON_UPLOAD_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public List<IconInfoResponse> getUserIcons() {
        Long userId = getCurrentUserId();
        List<Icon> icons = iconRepository.findAllByOwner_UserIdAndStatus(userId, Status.ACTIVE);

        List<IconInfoResponse> responses = icons.stream()
                .map(IconMapper::toIconInfoResponse)
                .collect(Collectors.toList());

        return responses;
    }

    @Transactional(readOnly = true)
    public List<IconInfoResponse> getDefaultIcons() {
        List<Icon> icons = iconRepository.findAllByIsDefaultAndStatus(true, Status.ACTIVE);

        List<IconInfoResponse> responses = icons.stream()
                .map(IconMapper::toIconInfoResponse)
                .collect(Collectors.toList());

        return responses;
    }

    @Transactional
    public IconInfoResponse renameIcon(Long iconId, String newName) {
        Icon icon = iconRepository.findByIconIdAndStatus(iconId, Status.ACTIVE)
                .orElseThrow(() -> LinkuException.of(ResponseCode.ICON_NOT_FOUND));

        Long userId = getCurrentUserId();
        User owner = icon.getOwner();

        if(!userId.equals(owner.getUserId())){
            throw LinkuException.of(ResponseCode.ICON_NOT_OWNER);
        }

        icon.setName(newName);
        iconRepository.save(icon);

        return IconMapper.toIconInfoResponse(icon);
    }

    @Transactional
    public void deleteIcon(Long iconId) {
        Icon icon = iconRepository.findByIconIdAndStatus(iconId, Status.ACTIVE)
                .orElseThrow(() -> LinkuException.of(ResponseCode.ICON_NOT_FOUND));

        Long userId = getCurrentUserId();
        User owner = icon.getOwner();

        if(!userId.equals(owner.getUserId())){
            throw LinkuException.of(ResponseCode.ICON_NOT_OWNER);
        }

        icon.setStatus(Status.DELETED);
        icon.setDeletedAt(LocalDateTime.now());
        iconRepository.save(icon);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty() || file.getSize() > MAX_FILE_SIZE) {
            throw LinkuException.of(ResponseCode.ICON_OVER_SIZE);
        }
    }

    private Long getCurrentUserId() {
        // TODO 인증&인가 측 구현 완료 후 SecurityContext 기반 사용자 ID 반환 로직 추가
        return 1L;
    }
}
