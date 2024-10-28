package com.example.nexufy.controller;

import com.example.nexufy.payload.request.LoginRequest;
import com.example.nexufy.payload.request.RegisterRequest;
import com.example.nexufy.payload.response.JwtResponse;
import com.example.nexufy.payload.response.MessageResponse;
import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.EnumRoles;
import com.example.nexufy.persistence.entities.Role;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.RoleRepository;
import com.example.nexufy.security.jwt.JwtUtils;
import com.example.nexufy.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Operaciones relacionadas con autenticación y gestión de usuarios")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Operation(summary = "Iniciar sesión", description = "Genera un token JWT para autenticar al usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "403", description = "Cuenta suspendida"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Customer customer = customerRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado."));

        if (customer.isStillSuspended()) {
            return ResponseEntity.status(403).body(
                    new MessageResponse("Error: Tu cuenta está suspendida hasta " + customer.getSuspendedUntil()));
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(),
                userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @Operation(summary = "Registrar un usuario", description = "Registra un nuevo usuario con el rol de usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El nombre de usuario o correo ya está en uso")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerAsUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (customerRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (customerRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        Customer customer = new Customer(registerRequest.getUsername(),
                registerRequest.getEmail(), encoder.encode(registerRequest.getPassword()));

        Role userRole = roleRepository.findByName(EnumRoles.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: User Role is not found."));
        customer.setRoles(Set.of(userRole));
        customer.setRegistrationDate(LocalDateTime.now());

        customerRepository.save(customer);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    @Operation(
            summary = "Registrar un administrador",
            description = "Permite a los administradores y superadministradores registrar nuevos usuarios.",
            security = @SecurityRequirement(name = "Authorization")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El nombre de usuario o correo ya está en uso"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (customerRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (customerRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        Customer customer = new Customer(registerRequest.getUsername(),
                registerRequest.getEmail(), encoder.encode(registerRequest.getPassword()));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Set<Role> roles = new HashSet<>();
        if (userDetails.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_SUPERADMIN"))) {
            Role adminRole = roleRepository.findByName(EnumRoles.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Admin Role is not found."));
            roles.add(adminRole);
        }

        Role userRole = roleRepository.findByName(EnumRoles.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: User Role is not found."));
        roles.add(userRole);

        customer.setRoles(roles);
        customer.setRegistrationDate(LocalDateTime.now());

        customerRepository.save(customer);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
