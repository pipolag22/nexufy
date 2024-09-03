package com.example.nexufy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectamos el codificador de contraseñas

    private InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        inMemoryUserDetailsManager.createUser(
                User.withUsername("user")
                        .password(passwordEncoder.encode("password"))
                        .roles("USER")
                        .build()
        );
        inMemoryUserDetailsManager.createUser(
                User.withUsername("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles("ADMIN")
                        .build()
        );
        return inMemoryUserDetailsManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return inMemoryUserDetailsManager.loadUserByUsername(username);
    }
}
