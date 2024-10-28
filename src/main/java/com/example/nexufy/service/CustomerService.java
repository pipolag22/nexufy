package com.example.nexufy.service;

import com.example.nexufy.dtos.CustomerContactDto;
import com.example.nexufy.dtos.CustomerDTO;
import com.example.nexufy.dtos.ProductDTO;
import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.EnumRoles;
import com.example.nexufy.persistence.entities.Role;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RoleRepository roleRepository;

    private CustomerDTO convertToCustomerDTO(Customer customer) {
        List<ProductDTO> products = productService.getProductsByCustomerId(customer.getId());

        Set<String> roles = customer.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return new CustomerDTO(
                customer.getId(),
                customer.getUsername(),
                customer.getName(),
                customer.getLastname(),
                customer.getEmail(),
                customer.getPhone(),
                customer.isSuspended(),
                customer.getRole(),
                roles,
                products
        );
    }

    public CustomerDTO getCustomerById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return convertToCustomerDTO(customer);
    }

    public List<ProductDTO> getProductsByCustomerId(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return productService.getProductsByCustomerId(customerId);
    }

    public Optional<CustomerDTO> findByUsername(String username) {
        return customerRepository.findByUsername(username).map(this::convertToCustomerDTO);
    }

    public Optional<CustomerDTO> findByEmail(String email) {
        return customerRepository.findByEmail(email).map(this::convertToCustomerDTO);
    }

    public Customer saveCustomer(Customer customer) {
        validateCustomer(customer);
        return customerRepository.save(customer);
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::convertToCustomerDTO)
                .collect(Collectors.toList());
    }

    public List<CustomerDTO> searchCustomers(String username) {
        return customerRepository.findByNameContainingIgnoreCase(username)
                .stream()
                .map(this::convertToCustomerDTO)
                .collect(Collectors.toList());
    }

    public Customer updateCustomerRole(String customerId, EnumRoles newRole) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Role role = roleRepository.findByName(newRole)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        customer.getRoles().clear(); // Limpiar roles actuales
        customer.getRoles().add(role); // Asignar el nuevo rol

        return customerRepository.save(customer);
    }

    public void deleteCustomer(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        customerRepository.delete(customer);
    }

    public CustomerContactDto getCustomerContactById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return new CustomerContactDto(
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress()
        );
    }

    public Customer updateCustomer(String id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        customer.setName(customerDTO.getName());
        customer.setLastname(customerDTO.getLastname());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhone(customerDTO.getPhone());
        customer.setSuspended(customerDTO.isSuspended());

        return customerRepository.save(customer);
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
