package com.linku.backend.domain.icon.service;

import com.linku.backend.domain.common.enums.Status;
import com.linku.backend.domain.icon.Icon;
import com.linku.backend.domain.icon.dto.IconMapper;
import com.linku.backend.domain.icon.dto.response.IconInfoResponse;
import com.linku.backend.domain.icon.repository.IconRepository;
import com.linku.backend.domain.template.repository.TemplateItemRepository;
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
    private final TemplateItemRepository templateItemRepository;

    @Transactional
    public IconInfoResponse saveIconWithImageUpload(Long userId, String iconName, MultipartFile file) {
        try {
            validateFile(file);

            byte[] compressedImage = ImageCompressor.resizeAndCompress(file);
            String imgUrl = s3Uploader.uploadFile(compressedImage, file.getOriginalFilename(), file.getContentType());

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> LinkuException.of(ResponseCode.USER_NOT_FOUND));

            Icon icon = Icon.builder()
                    .name(iconName)
                    .imageUrl(imgUrl)
                    .owner(user)
                    .isDefault(false)
                    .status(Status.ACTIVE)
                    .build();

            iconRepository.save(icon);

            return IconMapper.toIconInfoResponse(icon);

        } catch (IOException e) {
            throw LinkuException.of(ResponseCode.ICON_UPLOAD_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public List<IconInfoResponse> getUserIcons(Long userId) {
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
    public IconInfoResponse renameIcon(Long userId, Long iconId, String newName) {
        Icon icon = iconRepository.findByIconIdAndStatus(iconId, Status.ACTIVE)
                .orElseThrow(() -> LinkuException.of(ResponseCode.ICON_NOT_FOUND));

        User owner = icon.getOwner();

        if(!userId.equals(owner.getUserId())){
            throw LinkuException.of(ResponseCode.ICON_NOT_OWNER);
        }

        icon.setName(newName);
        iconRepository.save(icon);

        return IconMapper.toIconInfoResponse(icon);
    }

    @Transactional
    public void deleteIcon(Long userId, Long iconId) {
        Icon icon = iconRepository.findByIconIdAndStatus(iconId, Status.ACTIVE)
                .orElseThrow(() -> LinkuException.of(ResponseCode.ICON_NOT_FOUND));

        User owner = icon.getOwner();

        if(!userId.equals(owner.getUserId())){
            throw LinkuException.of(ResponseCode.ICON_NOT_OWNER);
        }

        if(templateItemRepository.existsByIcon_IconIdAndStatus(iconId, Status.ACTIVE)) {
            throw LinkuException.of(ResponseCode.ICON_IN_USE);
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
}