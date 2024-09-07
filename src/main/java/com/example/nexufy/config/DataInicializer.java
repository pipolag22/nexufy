package com.example.nexufy.config;

import com.example.nexufy.persistence.entities.EnumRoles;
import com.example.nexufy.persistence.entities.Role;
import com.example.nexufy.persistence.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class DataInicializer implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (roleRepository.count() == 0) {
            Role userRole = new Role(EnumRoles.ROLE_USER);
            Role adminRole = new Role(EnumRoles.ROLE_ADMIN);
            Role superAdminRole = new Role(EnumRoles.ROLE_SUPERADMIN);
            roleRepository.save(userRole);
            roleRepository.save(adminRole);
            roleRepository.save(superAdminRole);
        }
    }
}
