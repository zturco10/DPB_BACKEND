package com.proyecto_backend.FoodHub.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.proyecto_backend.FoodHub.exception.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Autowired
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile multipartFile) {
        File file = null;
        try {

            file = convertMultiPartToFile(multipartFile);


            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());


            String imageUrl = (String) uploadResult.get("secure_url");
            logger.info("Archivo subido exitosamente a Cloudinary: {}", imageUrl);
            return imageUrl;

        } catch (IOException e) {
            logger.error("Error al convertir o subir archivo: {}", e.getMessage());
            throw new FileUploadException("Error al procesar el archivo para subir.", e);
        } catch (Exception e) {

            logger.error("Error al subir archivo a Cloudinary: {}", e.getMessage());
            throw new FileUploadException("Error al subir archivo a Cloudinary.", e);
        } finally {

            if (file != null && file.exists()) {
                if (!file.delete()) {
                    logger.warn("No se pudo eliminar el archivo temporal: {}", file.getAbsolutePath());
                }
            }
        }
    }


    private File convertMultiPartToFile(MultipartFile file) throws IOException {

        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

}