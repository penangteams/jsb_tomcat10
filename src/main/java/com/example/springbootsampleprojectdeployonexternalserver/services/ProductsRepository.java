package com.example.springbootsampleprojectdeployonexternalserver.services;
import com.example.springbootsampleprojectdeployonexternalserver.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ProductsRepository extends JpaRepository<Product, Integer> {
}
