package com.example.nexufy.controller;

import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.service.CustomerService;
import com.example.nexufy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/customer")

public class CustomerController {  // Cambiado a CustomerController
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;


    @GetMapping
    public List<Customer> getAllCustomer() {
        return customerService.getAllCustomer();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{customerId}/products")
    public List<Product> getProductsByCustomerId(@PathVariable String customerId) {
        return customerService.getProductsByCustomerId(customerId);
    }

    @PostMapping
    public Customer addCustomer(@RequestBody Customer customer) {
        return customerService.addCustomer(customer);
    }
    @PostMapping("/{customerId}/products")
    public Product addProductToCustomer(@PathVariable String customerId, @RequestBody Product product) {
        // Encuentra el cliente por ID
        Customer customer = customerService.getCustomerById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Asocia el cliente al producto
        product.setCustomer(customer);

        // Guarda el producto
        return productService.addProduct(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
        Optional<Customer> optionalCustomer = customerService.getCustomerById(id);
        if (optionalCustomer.isPresent()) {
            return ResponseEntity.ok(customerService.updateCustomer(id, customer));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        if (customerService.getCustomerById(id).isPresent()) {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
