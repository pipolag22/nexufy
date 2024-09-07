package com.example.nexufy.service;

import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.repository.CustomerRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Customer saveCustomer(Customer customer) {
        validateCustomer(customer);
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomer() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(String id) {
        return customerRepository.findById(id);
    }

    public Customer addCustomer(Customer customer, String creatorUsername) {
        Optional<Customer> creatorOpt = findByUsername(creatorUsername);

        if (!creatorOpt.isPresent()) {
            throw new IllegalArgumentException("Creator user not found");
        }

        Customer creator = creatorOpt.get();

        // Validar que el rol del creador tenga permisos para crear el tipo de usuario solicitado
        validateRolePermissions(creator, customer);

        // Validar que el usuario o email no existan previamente
        if (findByUsername(customer.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        return customerRepository.save(customer);
    }

    public void deleteCustomer(String id) {
        customerRepository.deleteById(id);
    }

    public Customer updateCustomer(String id, Customer customer) {
        customer.setId(String.valueOf(new ObjectId(id)));
        validateCustomer(customer);
        return customerRepository.save(customer);
    }

    public List<Product> getProductsByCustomerId(String customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isPresent()) {
            return optionalCustomer.get().getProducts();
        } else {
            throw new RuntimeException("Customer not found with id: " + customerId);
        }
    }

    // Método para validar permisos de creación según roles
    public void validateRolePermissions(Customer creator, Customer newCustomer) {
        String creatorRole = creator.getRole();
        String newCustomerRole = newCustomer.getRole();

        if ("ROLE_USER".equals(newCustomerRole) && !"ROLE_ADMIN".equals(creatorRole) && !"ROLE_SUPERADMIN".equals(creatorRole)) {
            throw new IllegalArgumentException("Only admins or superadmins can create users");
        }

        if ("ROLE_ADMIN".equals(newCustomerRole) && !"ROLE_SUPERADMIN".equals(creatorRole)) {
            throw new IllegalArgumentException("Only superadmins can create admins");
        }

        if ("ROLE_SUPERADMIN".equals(newCustomerRole)) {
            throw new IllegalArgumentException("Creating superadmin is not allowed");
        }
    }

    // Validar que el objeto Customer cumpla con las restricciones
    private void validateCustomer(Customer customer) {
        if (customer.getUsername() == null || customer.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (customer.getPassword() == null || customer.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        String role = customer.getRole();
        if (!Customer.ROLE_USER.equals(role) && !Customer.ROLE_ADMIN.equals(role) && !Customer.ROLE_SUPERADMIN.equals(role)) {
            throw new IllegalArgumentException("Invalid role");
        }
    }
}
