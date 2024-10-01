package com.example.nexufy.controller;

import com.example.nexufy.persistence.entities.Subscription;
import com.example.nexufy.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<Subscription> createSubscription(@RequestBody Subscription subscription) {
        return ResponseEntity.ok(subscriptionService.createSubscription(subscription));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable String id) {
        Optional<Subscription> subscription = subscriptionService.getSubscriptionById(id);
        return subscription.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public List<Subscription> getSubscriptionsByCustomerId(@PathVariable String customerId) {
        return subscriptionService.getSubscriptionsByCustomerId(customerId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subscription> updateSubscription(@PathVariable String id, @RequestBody Subscription subscription) {
        try {
            return ResponseEntity.ok(subscriptionService.updateSubscription(id, subscription));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable String id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok().build();
    }
}
