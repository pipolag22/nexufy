package com.example.nexufy.persistence.repository;

import com.example.nexufy.persistence.entities.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

}
