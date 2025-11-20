package com.linku.backend.domain.icon.controller;

import com.linku.backend.domain.icon.dto.request.IconRenameRequest;
import com.linku.backend.domain.icon.dto.request.IconUploadRequest;
import com.linku.backend.domain.icon.dto.response.IconInfoResponse;
import com.linku.backend.domain.icon.service.IconService;
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
            @Valid @ModelAttribute IconUploadRequest request
    ) {
        IconInfoResponse createdIcon = iconService.saveIconWithImageUpload(request.getName(), request.getFile());

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                createdIcon
        );
    }

    @GetMapping("/my")
    public BaseResponse<?> getUserIcons() {
        List<IconInfoResponse> icons = iconService.getUserIcons();

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
            @PathVariable Long iconId,
            @Valid @RequestBody IconRenameRequest request
    ) {
        IconInfoResponse renamedIcon = iconService.renameIcon(iconId, request.getName());

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                renamedIcon
        );
    }

    @DeleteMapping("/{iconId}")
    public BaseResponse<?> deleteIcon(@PathVariable Long iconId) {
        iconService.deleteIcon(iconId);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                null
        );
    }
}