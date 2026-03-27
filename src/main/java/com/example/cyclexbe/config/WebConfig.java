package com.example.cyclexbe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve all uploaded files at /uploads/** → ./uploads/ on disk
        // In Docker: /app/uploads/ (mounted as ./uploads:/app/uploads)
        Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
        String resourceLocation = uploadPath.toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation + "/");
    }
}
