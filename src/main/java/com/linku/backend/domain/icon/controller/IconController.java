package com.linku.backend.domain.icon.controller;

import com.linku.backend.domain.icon.dto.request.IconRenameRequest;
import com.linku.backend.domain.icon.dto.request.IconUploadRequest;
import com.linku.backend.domain.icon.dto.response.IconInfoResponse;
import com.linku.backend.domain.icon.service.IconService;
import com.linku.backend.global.resolver.CurrentUserId;
import com.linku.backend.global.response.BaseResponse;
import com.linku.backend.global.response.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/icons")
public class IconController {

    private final IconService iconService;

    @PostMapping
    public BaseResponse<?> uploadIcon(
            @CurrentUserId Long userId,
            @Valid @ModelAttribute IconUploadRequest request
    ) {
        IconInfoResponse createdIcon = iconService.saveIconWithImageUpload(userId, request.getName(), request.getFile());

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                createdIcon
        );
    }

    @GetMapping("/my")
    public BaseResponse<?> getUserIcons(
            @CurrentUserId Long userId
    ) {
        List<IconInfoResponse> icons = iconService.getUserIcons(userId);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                icons
        );
    }

    @GetMapping("/default")
    public BaseResponse<?> getDefaultIcons() {
        List<IconInfoResponse> icons = iconService.getDefaultIcons();

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                icons
        );
    }

    @PutMapping("/{iconId}/rename")
    public BaseResponse<?> renameIcon(
            @CurrentUserId Long userId,
            @PathVariable Long iconId,
            @Valid @RequestBody IconRenameRequest request
    ) {
        IconInfoResponse renamedIcon = iconService.renameIcon(userId, iconId, request.getName());

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                renamedIcon
        );
    }

    @DeleteMapping("/{iconId}")
    public BaseResponse<?> deleteIcon(
            @CurrentUserId Long userId,
            @PathVariable Long iconId
    ) {
        iconService.deleteIcon(userId, iconId);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                null
        );
    }
}