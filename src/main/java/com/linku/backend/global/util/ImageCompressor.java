package com.linku.backend.global.util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageCompressor {
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 800;
    private static final float QUALITY = 0.8f;

    public static byte[] resizeAndCompress(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
                .size(MAX_WIDTH, MAX_HEIGHT)
                .outputFormat("jpg")
                .outputQuality(QUALITY)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }
}
