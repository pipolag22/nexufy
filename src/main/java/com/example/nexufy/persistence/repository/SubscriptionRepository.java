package com.example.nexufy.persistence.repository;

import com.example.nexufy.persistence.entities.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    List<Subscription> findByCustomerId(String customerId);
}
