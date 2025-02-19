package org.esales.products.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "stocks") // Specifies the MongoDB collection name
public class Stock {

    @Id
    private String id;

    private String description;
    private BigDecimal price;
    private BigDecimal quantity;

    @DBRef
    private Product product; // Reference to Category (MongoDB relationship)
}