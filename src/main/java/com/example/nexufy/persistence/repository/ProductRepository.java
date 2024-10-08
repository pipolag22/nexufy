package com.example.nexufy.persistence.repository;

import com.example.nexufy.persistence.entities.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    // Método para buscar productos por Customer ID
    @Query("{ 'customer.id': ?0 }")
    List<Product> findByCustomerId(String customerId);
    List<Product> findByNameContainingIgnoreCase(String name);
}