package com.example.nexufy.controller;

import com.example.nexufy.dtos.CustomerDTO;
import com.example.nexufy.dtos.ProductDTO;
import com.example.nexufy.dtos.CustomerContactDto;
import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.EnumRoles;
import com.example.nexufy.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/{id}")
    public CustomerDTO getCustomerById(@PathVariable String id) {
        return customerService.getCustomerById(id);
    }

    @PutMapping("/promote")
    public ResponseEntity<String> promoteCustomerToRole(
            @RequestParam String customerId, @RequestParam EnumRoles role) {
        customerService.updateCustomerRole(customerId, role);
        return ResponseEntity.ok("Customer role updated successfully to " + role);
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductDTO>> getProductsByCustomerId(@PathVariable String id) {
        try {
            List<ProductDTO> products = customerService.getProductsByCustomerId(id);
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/all")
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
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
    public ResponseEntity<String> updateCustomer(
            @PathVariable String id,
            @RequestBody CustomerDTO customerDTO) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customerDTO);
            return ResponseEntity.ok("Customer updated successfully: " + updatedCustomer.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public List<CustomerDTO> search(@RequestParam String username) {
        return customerService.searchCustomers(username);
    }
}
