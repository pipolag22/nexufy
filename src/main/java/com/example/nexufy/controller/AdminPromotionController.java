package com.example.nexufy.controller;

import com.example.nexufy.payload.response.MessageResponse;
import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.EnumRoles;
import com.example.nexufy.persistence.entities.Role;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class AdminPromotionController {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    RoleRepository roleRepository;

    // Permitir solo a ROLE_USER para la auto-promoción
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/promote/admin")
    public ResponseEntity<?> promoteToAdmin(@RequestParam String username) {
        // Buscar el usuario
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Buscar el rol admin
        Role adminRole = roleRepository.findByName(EnumRoles.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Eliminar el rol de usuario (si está presente) para evitar duplicación
        customer.getRoles().removeIf(role -> role.getName().equals(EnumRoles.ROLE_USER));

        // Asignar el nuevo rol sin duplicación
        customer.getRoles().add(adminRole);
        customerRepository.save(customer);

        return ResponseEntity.ok(new MessageResponse("User promoted to Admin successfully!"));
    }
}
