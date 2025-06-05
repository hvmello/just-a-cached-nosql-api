package com.crud.market_api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Product Data Transfer Object")
public class ProductDto {

    @Schema(description = "Product ID", example = "507f1f77bcf86cd799439011")
    private String id;

    @Schema(description = "Product name", example = "Smartphone", required = true)
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String name;

    @Schema(description = "Product type", example = "Electronics", required = true)
    @NotBlank(message = "Product type is required")
    @Size(min = 3, max = 50, message = "Product type must be between 3 and 50 characters")
    private String type;
}