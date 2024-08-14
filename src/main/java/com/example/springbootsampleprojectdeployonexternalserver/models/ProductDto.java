package com.example.springbootsampleprojectdeployonexternalserver.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductDto {
    @NotEmpty(message = "The name is required")
    private String name;
    @NotEmpty(message = "The brand is required")
    private String brand;
    @NotEmpty(message = "The category is required")
    private String category;
    @Min(0)
    @NotNull(message = "Cannot be blank")
    private Double price;
    @Size(min = 10, message = "the description should be at least 10 characters")
    @Size(max = 2000, message = "the description should be max of 2000 characters")
    private String description;

    private MultipartFile imageFile;

}
