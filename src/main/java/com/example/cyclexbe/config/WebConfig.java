package com.example.cyclexbe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.dir:uploads/chat}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded chat images at /uploads/chat/** → uploadDir on disk
        // uploadDir is relative to the working directory (e.g. /app/uploads/chat in
        // Docker)
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String resourceLocation = uploadPath.toUri().toString();
        registry.addResourceHandler("/uploads/chat/**")
                .addResourceLocations(resourceLocation + "/");
    }
}
