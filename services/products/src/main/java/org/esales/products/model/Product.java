package org.esales.products.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products") // Specifies the MongoDB collection name
public class Product {

    @Id
    private String id;

    private String name;
    private String description;

    @DBRef
    private Category category; // Reference to Category (MongoDB relationship)
}
