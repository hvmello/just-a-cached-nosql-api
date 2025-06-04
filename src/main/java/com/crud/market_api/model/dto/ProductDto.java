package com.crud.market_api.model.dto;

import lombok.Data;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String type;
}