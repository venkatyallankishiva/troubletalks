package com.example.TroubleTalks.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile multipartFile) {
        File uploadedFile = null;
        try {
            uploadedFile = Files.createTempFile(UUID.randomUUID().toString(), "").toFile();
            multipartFile.transferTo(uploadedFile);

            Map uploadResult = cloudinary.uploader().upload(
                    uploadedFile,
                    ObjectUtils.asMap(
                            "upload_preset", "troubletalks_public",
                            "resource_type", "auto",
                            "access_mode", "public"
                    )
            );

            if (!uploadedFile.delete()) {
                System.out.println("Warning: Could not delete temp file: " + uploadedFile.getAbsolutePath());
            }

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            if (uploadedFile != null) uploadedFile.delete();
            throw new RuntimeException("File upload failed to Cloudinary: " + e.getMessage(), e);
        }
    }
}
