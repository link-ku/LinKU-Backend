package com.linku.backend.global.util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageCompressor {
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 800;
    private static final float QUALITY = 0.8f;

    public static byte[] resizeAndCompress(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.Builder<? extends InputStream> builder =
                Thumbnails.of(new ByteArrayInputStream(file.getBytes()));

        if (originalWidth > MAX_WIDTH || originalHeight > MAX_HEIGHT) {
            builder.size(MAX_WIDTH, MAX_HEIGHT);
        } else {
            builder.scale(1.0);
        }

        builder.outputFormat("jpg")
                .outputQuality(QUALITY)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }
}