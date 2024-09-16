package com.example.nexufy.security.services;

import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SuspensionService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    // Verificar cada 24 horas si hay suspensiones que han expirado
    @Scheduled(fixedRate = 86400000) // 24 horas (86400000 milisegundos)
    public void checkSuspensions() {
        // Verificar suspensiones de clientes
        List<Customer> suspendedCustomers = customerRepository.findAll().stream()
                .filter(Customer::isSuspended)
                .filter(customer -> customer.getSuspendedUntil().isBefore(LocalDateTime.now()))
                .toList();

        for (Customer customer : suspendedCustomers) {
            customer.setSuspended(false);
            customer.setSuspendedUntil(null);
            customer.setSuspendedReason(null);
            customerRepository.save(customer);
        }

        // Verificar suspensiones de productos
        List<Product> suspendedProducts = productRepository.findAll().stream()
                .filter(Product::isSuspended)
                .filter(product -> product.getSuspendedUntil().isBefore(LocalDateTime.now()))
                .toList();

        for (Product product : suspendedProducts) {
            product.setSuspended(false);
            product.setSuspendedUntil(null);
            product.setSuspendedReason(null);
            productRepository.save(product);
        }
    }
}
