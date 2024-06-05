package com.example.nexufy.persistence.repository;

import com.example.nexufy.persistence.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
