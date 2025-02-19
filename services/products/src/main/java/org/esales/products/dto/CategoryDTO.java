package org.esales.products.dto;

import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDTO {
    private String id;

    @NotBlank(message = "Category name is required")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;
}
