package com.linku.backend.domain.icon.controller;

import com.linku.backend.domain.icon.service.IconService;
import com.linku.backend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/icons")
public class IconController {

    private final IconService iconService;

    @PostMapping("/upload")
    public ResponseEntity<BaseResponse<String>> saveIconWithImage(
            @RequestParam("iconName") String iconName,
            @RequestParam("imgFile") MultipartFile imgFile
    ) {
        return iconService.saveIconWithImageUpload(iconName, imgFile);
    }
}
