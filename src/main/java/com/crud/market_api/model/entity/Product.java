package com.crud.market_api.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document(collection = "products")
@Data
public class Product {
    @Id
    private String id;
    private String name;
    private String type;
}