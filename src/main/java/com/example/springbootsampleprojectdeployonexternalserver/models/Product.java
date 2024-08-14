package com.example.springbootsampleprojectdeployonexternalserver.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String brand;
    private String category;

    @Value("0.0")
    private Double price;

    @Column(columnDefinition = "Text")
    private String description;
    private Date createdAt;
    private String imageFileName;
}
