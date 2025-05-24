package com.vishal.electronicsstore.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ImagesDirectoryInitializer {

    @PostConstruct
    public void init() {
        List<String> dirs = List.of("images/users", "images/categories", "images/products");

        Path currentPath = Paths.get("").toAbsolutePath();
        Path modulePath;

        if (currentPath.getFileName().toString().equalsIgnoreCase("electronicsstore-springboot")) {
            modulePath = currentPath;
        } else {
            modulePath = currentPath.resolve("electronicsstore-springboot");
        }

        for (String dir : dirs) {
            Path fullPath = modulePath.resolve(dir);
            if (Files.notExists(fullPath)) {
                try {
                    Files.createDirectories(fullPath);
                    log.info("Created missing image directory: {}", fullPath);
                } catch (IOException e) {
                    log.error("Failed to create image directory: {}", fullPath, e);
                }
            } else {
                log.info("Image directory already exists: {}", fullPath);
            }
        }
    }

}
