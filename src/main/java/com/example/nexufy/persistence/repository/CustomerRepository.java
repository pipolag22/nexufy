package com.example.nexufy.persistence.repository;
import com.example.nexufy.persistence.entities.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {
}
