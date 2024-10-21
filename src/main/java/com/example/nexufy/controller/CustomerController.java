package com.example.nexufy.controller;

import com.example.nexufy.payload.request.LoginRequest;
import com.example.nexufy.Dtos.CustomerContactDto;
import com.example.nexufy.payload.request.RegisterRequest;
import com.example.nexufy.payload.response.JwtResponse;
import com.example.nexufy.payload.response.MessageResponse;
import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.EnumRoles;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.security.jwt.JwtUtils;
import com.example.nexufy.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<Customer> customerOpt = customerService.findByUsername(loginRequest.getUsername());

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();

            if (customer.isStillSuspended()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Error: Tu cuenta está suspendida hasta " + customer.getSuspendedUntil()));
            }

            if (encoder.matches(loginRequest.getPassword(), customer.getPassword())) {
                String token = jwtUtils.generateToken(customer.getUsername());
                List<String> roles = customer.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList());

                return ResponseEntity.ok(new JwtResponse(
                        token,
                        customer.getId(),
                        customer.getUsername(),
                        customer.getEmail(),
                        roles
                ));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Contraseña incorrecta"));
            }
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Usuario no encontrado"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (customerRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (customerRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        Customer customer = new Customer();
        customer.setUsername(registerRequest.getUsername());
        customer.setEmail(registerRequest.getEmail());
        customer.setPassword(encoder.encode(registerRequest.getPassword()));

        Set<String> strRoles = registerRequest.getRoles();
        EnumRoles role;

        if (strRoles == null || strRoles.isEmpty()) {
            role = EnumRoles.ROLE_USER;
        } else {
            switch (strRoles.iterator().next()) {
                case "admin":
                    role = EnumRoles.ROLE_ADMIN;
                    break;
                case "super":
                    role = EnumRoles.ROLE_SUPERADMIN;
                    break;
                default:
                    role = EnumRoles.ROLE_USER;
                    break;
            }
        }

        customer.setRole(role);
        customerRepository.save(customer);

        return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addCustomer(@RequestBody Customer newCustomer,
                                              @RequestParam String creatorUsername) {
        try {
            Customer createdCustomer = customerService.addCustomer(newCustomer, creatorUsername);
            return ResponseEntity.ok("Customer added successfully: " + createdCustomer.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Customer> getAllCustomers() { // Corregido el nombre del método
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
        Optional<Customer> customerOpt = customerService.getCustomerById(id);

        if (customerOpt.isPresent()) {
            return ResponseEntity.ok(customerOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully");
    }

    @GetMapping("/{id}/contact")
    public CustomerContactDto getCustomerContact(@PathVariable String id) {
        return customerService.getCustomerContactById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customer);
            return ResponseEntity.ok("Customer updated successfully: " + updatedCustomer.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<Product>> getProductsByCustomerId(@PathVariable String id) {
        try {
            List<Product> products = customerService.getProductsByCustomerId(id);
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/search")
    public List<Customer> search(@RequestParam String username){
        return customerService.searchCustomers(username);
    }
}
