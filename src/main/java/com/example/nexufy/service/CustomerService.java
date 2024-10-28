package com.example.nexufy.service;

import com.example.nexufy.dtos.CustomerContactDto;
import com.example.nexufy.dtos.CustomerDTO;
import com.example.nexufy.dtos.ProductDTO;
import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.EnumRoles;
import com.example.nexufy.persistence.entities.Role;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.RoleRepository;
import com.example.nexufy.service.ProductService;
import org.bson.types.ObjectId;
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
    private ProductService productService; // Cambio aquí: Se usa ProductService en lugar de ProductRepository

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    // Conversión de entidad Customer a DTO con productos asociados
    private CustomerDTO convertToCustomerDTO(Customer customer) {
        List<ProductDTO> products = productService.getProductsByCustomerId(customer.getId());

        // Mapear el Set<Role> a un Set<String> de nombres de roles
        Set<String> roles = customer.getRoles().stream()
                .map(role -> role.getName().name())  // Convertir Role a su nombre en String
                .collect(Collectors.toSet());

        return new CustomerDTO(
                customer.getId(),
                customer.getUsername(),
                customer.getName(),
                customer.getLastname(),
                customer.getEmail(),
                customer.getPhone(),
                customer.isSuspended(),
                customer.getRole(),  // EnumRoles como rol principal
                roles,  // Otros roles como Set<String>
                customer.getAddress(),
                products // Incluimos la lista de productos
        );
    }

    // Método para convertir Customer a CustomerContactDto
    private CustomerContactDto convertToCustomerContactDto(Customer customer) {
        return new CustomerContactDto(
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress()
        );
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
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

    public Customer updateCustomerPassword(Customer customer, String newPassword) {
        customer.setPassword(encoder.encode(newPassword));
        return customerRepository.save(customer);
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::convertToCustomerDTO)
                .collect(Collectors.toList());
    }

    public List<CustomerDTO> searchCustomers(String username) {
        return customerRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .map(this::convertToCustomerDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // Mapear el Set<Role> a un Set<String> de nombres de roles
        Set<String> roles = customer.getRoles().stream()
                .map(role -> role.getName().name())  // Convertir EnumRoles en String
                .collect(Collectors.toSet());

        return new CustomerDTO(
                customer.getId(),
                customer.getUsername(),
                customer.getName(),
                customer.getLastname(),
                customer.getEmail(),
                customer.getPhone(),
                customer.isSuspended(),
                customer.getRole(),  // EnumRoles como rol principal
                roles,  // Otros roles
                customer.getAddress(),
                customer.getProducts().stream()
                        .map(ProductDTO::new)
                        .collect(Collectors.toList())
        );
    }

    public Customer addCustomer(Customer customer, String creatorUsername) {
        Optional<CustomerDTO> creatorOpt = findByUsername(creatorUsername);

        if (creatorOpt.isEmpty()) {
            throw new IllegalArgumentException("Creator user not found");
        }

        CustomerDTO creator = creatorOpt.get();
        validateRolePermissions(creator, customer);

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

    public Customer updateCustomer(String id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // Actualizamos solo los campos permitidos
        customer.setName(customerDTO.getName());
        customer.setLastname(customerDTO.getLastname());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhone(customerDTO.getPhone());
        customer.setSuspended(customerDTO.isSuspended());
        customer.setAddress(customerDTO.getAddress());

        return customerRepository.save(customer);
    }


    // Cambiado: Ahora usamos el servicio de productos para obtener productos por cliente
    public List<ProductDTO> getProductsByCustomerId(String customerId) {
        return productService.getProductsByCustomerId(customerId);
    }

    public CustomerContactDto getCustomerContactById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return convertToCustomerContactDto(customer);
    }

    public void validateRolePermissions(CustomerDTO creator, Customer newCustomer) {
        EnumRoles creatorRole = creator.getRole();
        EnumRoles newCustomerRole = newCustomer.getRole();

        if (EnumRoles.ROLE_USER.equals(newCustomerRole) &&
                !(EnumRoles.ROLE_ADMIN.equals(creatorRole) || EnumRoles.ROLE_SUPERADMIN.equals(creatorRole))) {
            throw new IllegalArgumentException("Only admins or superadmins can create users");
        }

        if (EnumRoles.ROLE_ADMIN.equals(newCustomerRole) &&
                !EnumRoles.ROLE_SUPERADMIN.equals(creatorRole)) {
            throw new IllegalArgumentException("Only superadmins can create admins");
        }

        if (EnumRoles.ROLE_SUPERADMIN.equals(newCustomerRole)) {
            throw new IllegalArgumentException("Creating superadmin is not allowed");
        }
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

    public Customer updateCustomerRole(String customerId, EnumRoles newRole) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Role role = roleRepository.findByName(newRole)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        customer.getRoles().clear(); // Limpiar roles actuales
        customer.getRoles().add(role); // Asignar el nuevo rol

        return customerRepository.save(customer);
    }
}
