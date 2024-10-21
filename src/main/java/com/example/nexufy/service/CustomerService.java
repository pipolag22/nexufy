package com.example.nexufy.service;

import com.example.nexufy.Dtos.CustomerContactDto;
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

    @Autowired
    private PasswordEncoder encoder;

    // Encontrar un cliente por ID
    public Customer findById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    // Guardar un nuevo cliente
    public Customer saveCustomer(Customer customer) {
        validateCustomer(customer);
        return customerRepository.save(customer);
    }

    // Obtener todos los clientes
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Buscar cliente por nombre de usuario
    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    // Buscar cliente por email
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    // Actualizar la contraseña de un cliente
    public Customer updateCustomerPassword(Customer customer, String newPassword) {
        customer.setPassword(encoder.encode(newPassword));
        return customerRepository.save(customer);
    }

    // Buscar clientes por nombre de usuario (parcial)
    public List<Customer> searchCustomers(String username) {
        return customerRepository.findByNameContainingIgnoreCase(username);
    }

    // Obtener un cliente por ID
    public Optional<Customer> getCustomerById(String id) {
        return customerRepository.findById(id);
    }

    // Añadir un nuevo cliente con validación de roles y permisos
    public Customer addCustomer(Customer customer, String creatorUsername) {
        Optional<Customer> creatorOpt = findByUsername(creatorUsername);
        Customer creator = creatorOpt.orElseThrow(() -> new IllegalArgumentException("Creator user not found"));

        // Validar permisos
        validateRolePermissions(creator, customer);

        // Validar si el nombre de usuario o email ya existe
        if (findByUsername(customer.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        return customerRepository.save(customer);
    }

    // Eliminar un cliente por ID
    public void deleteCustomer(String id) {
        customerRepository.deleteById(id);
    }

    // Actualizar un cliente existente
    public Customer updateCustomer(String id, Customer customer) {
        customer.setId(String.valueOf(new ObjectId(id)));
        validateCustomer(customer);
        return customerRepository.save(customer);
    }

    // Obtener productos por ID de cliente
    public List<Product> getProductsByCustomerId(String customerId) {
        return productRepository.findByCustomerId(customerId);
    }

    // Validar permisos de creación según roles
    public void validateRolePermissions(Customer creator, Customer newCustomer) {
        EnumRoles creatorRole = creator.getRole();
        EnumRoles newCustomerRole = newCustomer.getRole();

        // Validar si el creador tiene los permisos para crear el nuevo rol
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

    // Obtener información de contacto del cliente
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

    // Validar el cliente antes de guardarlo
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

