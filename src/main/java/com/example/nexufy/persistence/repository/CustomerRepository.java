package com.example.nexufy.persistence.repository;

import com.example.nexufy.persistence.entities.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findByUsername(String username);

    Optional<Customer> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<Customer> findByNameContainingIgnoreCase(String username);
}
