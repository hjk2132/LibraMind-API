package com.no_plan.library_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String path = uploadDir.replace("\"", "").trim();

        if (!path.endsWith("/")) {
            path += "/";
        }

        String resourceLocation = "file://" + path;

        System.out.println("=============================================");
        System.out.println(">> 원본 경로: " + uploadDir);
        System.out.println(">> 다듬은 경로: " + path);
        System.out.println(">> 리소스 매핑 설정: " + resourceLocation);
        System.out.println("=============================================");

        registry.addResourceHandler("/images/**")
                .addResourceLocations(resourceLocation);
    }
}