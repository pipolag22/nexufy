package com.example.nexufy.service;

import Dtos.CustomerContactDto;
import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.EnumRoles;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.ProductRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    public Customer findById(String id) {

        Optional<Customer> optionalCustomer = customerRepository.findById(id);

        return optionalCustomer.orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public Optional<Customer> findByEmail(String email) { // Añadido aquí
        return customerRepository.findByEmail(email);
    }

    public Customer saveCustomer(Customer customer) {
        validateCustomer(customer);
        return customerRepository.save(customer);
    }
    @Autowired
    private PasswordEncoder encoder;
    public Customer updateCustomerPassword(Customer customer, String newPassword) {
        customer.setPassword(encoder.encode(newPassword));
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

    // Método corregido para obtener productos por Customer ID
    public List<Product> getProductsByCustomerId(String customerId) {
        // Buscar los productos relacionados con el cliente en la colección de productos
        return productRepository.findByCustomerId(customerId);
    }

    // Método para validar permisos de creación según roles
    public void validateRolePermissions(Customer creator, Customer newCustomer) {
        EnumRoles creatorRole = creator.getRole();
        EnumRoles newCustomerRole = newCustomer.getRole();

        if (EnumRoles.ROLE_USER.equals(newCustomerRole) && !(EnumRoles.ROLE_ADMIN.equals(creatorRole) || EnumRoles.ROLE_SUPERADMIN.equals(creatorRole))) {
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

        EnumRoles role = customer.getRole();
        if (!EnumRoles.ROLE_USER.equals(role) && !EnumRoles.ROLE_ADMIN.equals(role) && !EnumRoles.ROLE_SUPERADMIN.equals(role)) {
            throw new IllegalArgumentException("Invalid role");
        }
    }
}
