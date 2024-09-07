package com.example.nexufy.controller;

import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody Customer loginRequest) {
        Optional<Customer> customerOpt = customerService.findByUsername(loginRequest.getUsername());

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (customer.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok("Login successful");
            } else {
                return ResponseEntity.badRequest().body("Invalid password");
            }
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer newCustomer,
                                               @RequestParam(required = false) String creatorUsername) {

        if (creatorUsername != null) {
            Optional<Customer> creatorOpt = customerService.findByUsername(creatorUsername);
            if (!creatorOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Creator user not found");
            }

            Customer creator = creatorOpt.get();

            try {
                customerService.validateRolePermissions(creator, newCustomer);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            newCustomer.setRole(Customer.ROLE_USER);
        }

        if (customerService.findByUsername(newCustomer.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (customerService.findByEmail(newCustomer.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        customerService.saveCustomer(newCustomer);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping
    public ResponseEntity<String> addCustomer(@RequestBody Customer customer, @RequestParam String creatorUsername) {
        Optional<Customer> creatorOpt = customerService.findByUsername(creatorUsername);

        if (!creatorOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Creator user not found");
        }

        Customer creator = creatorOpt.get();

        if ("ROLE_USER".equals(customer.getRole()) && !"ROLE_ADMIN".equals(creator.getRole()) && !"ROLE_SUPERADMIN".equals(creator.getRole())) {
            return ResponseEntity.badRequest().body("Only admins or superadmins can create users");
        }

        if ("ROLE_ADMIN".equals(customer.getRole()) && !"ROLE_SUPERADMIN".equals(creator.getRole())) {
            return ResponseEntity.badRequest().body("Only superadmins can create admins");
        }

        if ("ROLE_SUPERADMIN".equals(customer.getRole())) {
            return ResponseEntity.badRequest().body("Creating superadmin is not allowed");
        }

        if (customerService.findByUsername(customer.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (customerService.findByEmail(customer.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        customerService.saveCustomer(customer);
        return ResponseEntity.ok("Customer added successfully");
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomer() {
        List<Customer> customers = customerService.getAllCustomer();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
        if (customerService.getCustomerById(id).isPresent()) {
            Customer updatedCustomer = customerService.updateCustomer(id, customer);
            return ResponseEntity.ok(updatedCustomer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{customerId}/products")
    public ResponseEntity<List<Product>> getProductsByCustomerId(@PathVariable String customerId) {
        List<Product> products = customerService.getProductsByCustomerId(customerId);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        if (customerService.getCustomerById(id).isPresent()) {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
