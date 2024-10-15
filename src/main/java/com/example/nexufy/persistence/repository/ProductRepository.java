package com.example.nexufy.persistence.repository;

import com.example.nexufy.persistence.entities.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    @Query("{ 'customer.id': ?0 }")
    List<Product> findByCustomerId(String customerId);
    List<Product> findByNameContainingIgnoreCase(String name);


