// config/SwaggerConfig.java
package com.example.employee_management.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Encoding;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.util.LinkedHashMap;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Define the multipart file upload schema
        Schema<?> fileSchema = new Schema<>()
            .type("object")
            .addProperty("file", new Schema<>()
                .type("string")
                .format("binary")
                .description("File to upload"));

        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .name("bearerAuth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"))
                .addSchemas("MultipartFile", fileSchema)
                .addRequestBodies("FileUploadRequest", 
                    new RequestBody()
                        .required(true)
                        .description("File upload request")
                        .content(new Content()
                            .addMediaType("multipart/form-data",
                                new MediaType()
                                    .schema(fileSchema)))))
            .info(new Info()
                .title("Employee Management API")
                .version("2.0")
                .description("Employee Management System with JWT, RBAC, Soft Delete, Departments, and more"));
    }

    /**
     * Custom OpenAPI manipulator to fix multipart endpoints that springdoc-openapi misdetects
     * This ensures file upload endpoints are properly rendered as multipart/form-data with a file input
     */
    @Bean
    public OpenApiCustomizer multipartCustomizer() {
        return openApi -> {
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((path, pathItem) -> {
                    // Fix /api/users/{id}/upload endpoint
                    if (path.contains("/api/users/") && path.contains("/upload")) {
                        if (pathItem.getPost() != null) {
                            var operation = pathItem.getPost();
                            
                            // Create proper multipart schema with file property
                            var fileProperty = new Schema<>()
                                .type("string")
                                .format("binary")
                                .description("File to upload");
                            
                            var multipartSchema = new Schema<>()
                                .type("object")
                                .addProperty("file", fileProperty)
                                .addRequiredItem("file");
                            
                            var encodingMap = new LinkedHashMap<String, Encoding>();
                            encodingMap.put("file", new Encoding()
                                .contentType("application/octet-stream"));
                            
                            var mediaType = new MediaType()
                                .schema(multipartSchema)
                                .encoding(encodingMap);
                            
                            operation.getRequestBody().setContent(
                                new Content().addMediaType("multipart/form-data", mediaType)
                            );
                        }
                    }
                    // Fix /api/employees/{id}/upload endpoint
                    if (path.contains("/api/employees/") && path.contains("/upload")) {
                        if (pathItem.getPost() != null) {
                            var operation = pathItem.getPost();
                            
                            // Create proper multipart schema with file property
                            var fileProperty = new Schema<>()
                                .type("string")
                                .format("binary")
                                .description("File to upload");
                            
                            var multipartSchema = new Schema<>()
                                .type("object")
                                .addProperty("file", fileProperty)
                                .addRequiredItem("file");
                            
                            var encodingMap = new LinkedHashMap<String, Encoding>();
                            encodingMap.put("file", new Encoding()
                                .contentType("application/octet-stream"));
                            
                            var mediaType = new MediaType()
                                .schema(multipartSchema)
                                .encoding(encodingMap);
                            
                            operation.getRequestBody().setContent(
                                new Content().addMediaType("multipart/form-data", mediaType)
                            );
                        }
                    }
                });
            }
        };
    }
}