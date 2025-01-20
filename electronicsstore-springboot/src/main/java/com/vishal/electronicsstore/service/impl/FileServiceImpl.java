package com.vishal.electronicsstore.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vishal.electronicsstore.exception.BadAPIRequestException;
import com.vishal.electronicsstore.service.FileService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(MultipartFile file, String path) throws IOException {
        String originalFileName = file.getOriginalFilename();
        log.info("Filename : {}", originalFileName);

        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("Filename is null or empty!");
        }

        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        if (extension.equalsIgnoreCase(".png")
                || extension.equalsIgnoreCase(".jpg")
                || extension.equalsIgnoreCase(".jpeg")) {
            String fileName = UUID.randomUUID().toString();
            String fileNameWithExtension = fileName + extension;
            String fullPath = path + File.separator + fileNameWithExtension;
            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            Files.copy(file.getInputStream(), Paths.get(fullPath));
            return fileNameWithExtension;
        } else {
            throw new BadAPIRequestException("File with " + extension + " extension not allowed!");
        }
    }

    @Override
    public InputStream getResource(String path, String name) throws FileNotFoundException {
        String fullPath = path + File.separator + name;
        return new FileInputStream(fullPath);
    }

}
