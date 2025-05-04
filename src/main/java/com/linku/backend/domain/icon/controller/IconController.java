package com.linku.backend.domain.icon.controller;

import com.linku.backend.domain.icon.service.IconService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/icons")
public class IconController {

    private final IconService iconService;

    @PostMapping("/upload")
    public ResponseEntity<?> saveIconWithImage(
            @RequestParam("iconName") String iconName,
            @RequestParam("imgFile") MultipartFile imgFile
    ){
        try {
            iconService.saveIconWithImageUpload(iconName, imgFile);
            return ResponseEntity.ok("아이콘 저장 및 S3 업로드 성공");
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }
}
