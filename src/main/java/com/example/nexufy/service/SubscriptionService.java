package com.example.nexufy.service;

import com.example.nexufy.persistence.entities.Subscription;
import com.example.nexufy.persistence.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public Subscription createSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    public Optional<Subscription> getSubscriptionById(String id) {
        return subscriptionRepository.findById(id);
    }

    public List<Subscription> getSubscriptionsByCustomerId(String customerId) {
        return subscriptionRepository.findByCustomerId(customerId);
    }

    public Subscription updateSubscription(String id, Subscription subscription) {
        if (subscriptionRepository.existsById(id)) {
            subscription.setId(id);
            return subscriptionRepository.save(subscription);
        } else {
            throw new RuntimeException("Subscription not found");
        }
    }

    public void deleteSubscription(String id) {
        subscriptionRepository.deleteById(id);
    }
}
