package com.vishal.electronicsstore.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public String uploadFile(MultipartFile file, String imagePath) throws IOException {
        String originalFileName = file.getOriginalFilename();
        log.info("Original filename : {}", originalFileName);

        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("Filename is null or empty!");
        }

        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        if (extension.equalsIgnoreCase(".png")
                || extension.equalsIgnoreCase(".jpg")
                || extension.equalsIgnoreCase(".jpeg")) {
            String fileName = UUID.randomUUID().toString();
            String fileNameWithExtension = fileName + extension;
            log.info("New filename : {}", fileNameWithExtension);

            // Resolve the absolute project root (from class location, not working dir)
            Path projectRoot;
            try {
                Path codeSourcePath = Paths.get(
                        UserServiceImpl.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                projectRoot = codeSourcePath.getParent().getParent(); // classes -> target -> project root
            } catch (URISyntaxException e) {
                throw new RuntimeException("Failed to resolve project root path", e);
            }

            // Build image path
            Path imageFilePath = projectRoot.resolve(Paths.get(imagePath, fileNameWithExtension));

            // Attempt to write file to the stream
            try {
                File folder = imageFilePath.getParent().toFile();
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                Files.copy(file.getInputStream(), imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileNameWithExtension;
        } else {
            throw new BadAPIRequestException("File with " + extension + " extension not allowed!");
        }
    }

    @Override
    public InputStream getResource(String path, String name) throws FileNotFoundException {
        String fullPath = Paths.get(path, name).toString();
        return new FileInputStream(fullPath);
    }

}
