package com.example.nexufy.service;

import com.example.nexufy.Dtos.CustomerContactDto;
import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.EnumRoles;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder encoder;

    public Customer findById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    public Customer saveCustomer(Customer customer) {
        validateCustomer(customer);
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Customer updateCustomerPassword(Customer customer, String newPassword) {
        customer.setPassword(encoder.encode(newPassword));
        return customerRepository.save(customer);
    }

    public List<Customer> searchCustomers(String username) {
        return customerRepository.findByNameContainingIgnoreCase(username);
    }

    public Optional<Customer> getCustomerById(String id) {
        return customerRepository.findById(id);
    }

    public Customer addCustomer(Customer customer, String creatorUsername) {
        Optional<Customer> creatorOpt = findByUsername(creatorUsername);
        Customer creator = creatorOpt.orElseThrow(() -> new IllegalArgumentException("Creator user not found"));

        validateRolePermissions(creator, customer);

        if (findByUsername(customer.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        customer.setRegistrationDate(LocalDate.now()); // Establecer fecha de registro

        return customerRepository.save(customer);
    }

    public void deleteCustomer(String id) {
        customerRepository.deleteById(id);
    }

    public Customer updateCustomer(String id, Customer customer) {
        customer.setId(id);
        validateCustomer(customer);
        return customerRepository.save(customer);
    }

    public List<Product> getProductsByCustomerId(String customerId) {
        return productRepository.findByCustomerId(customerId);
    }

    public void validateRolePermissions(Customer creator, Customer newCustomer) {
        EnumRoles creatorRole = creator.getRole();
        EnumRoles newCustomerRole = newCustomer.getRole();

        if (EnumRoles.ROLE_USER.equals(newCustomerRole) &&
                !(EnumRoles.ROLE_ADMIN.equals(creatorRole) || EnumRoles.ROLE_SUPERADMIN.equals(creatorRole))) {
            throw new IllegalArgumentException("Only admins or superadmins can create users");
        }

        if (EnumRoles.ROLE_ADMIN.equals(newCustomerRole) && !EnumRoles.ROLE_SUPERADMIN.equals(creatorRole)) {
            throw new IllegalArgumentException("Only superadmins can create admins");
        }

        if (EnumRoles.ROLE_SUPERADMIN.equals(newCustomerRole)) {
            throw new IllegalArgumentException("Creating superadmin is not allowed");
        }
    }

    public CustomerContactDto getCustomerContactById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return new CustomerContactDto(
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress()
        );
    }

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
    }
}
