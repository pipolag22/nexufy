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
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class SuperAdminPromotionController {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    RoleRepository roleRepository;

    @PutMapping("/promote/superadmin")
    public ResponseEntity<?> promoteToSuperAdmin(@RequestParam String username) {
        // Buscar el usuario
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validar que el usuario ya sea admin
        if (customer.getRoles().stream().noneMatch(role -> role.getName().equals(EnumRoles.ROLE_ADMIN))) {
            return ResponseEntity.badRequest().body(new MessageResponse("User must be an Admin to promote to SuperAdmin"));
        }

        // Buscar el rol superadmin
        Role superAdminRole = roleRepository.findByName(EnumRoles.ROLE_SUPERADMIN)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Eliminar el rol admin duplicado (si está presente) antes de agregar superadmin
        customer.getRoles().removeIf(role -> role.getName().equals(EnumRoles.ROLE_ADMIN));

        // Asignar el rol de superadmin sin duplicación
        customer.getRoles().add(superAdminRole);
        customerRepository.save(customer);

        return ResponseEntity.ok(new MessageResponse("User promoted to SuperAdmin successfully!"));
    }
}
