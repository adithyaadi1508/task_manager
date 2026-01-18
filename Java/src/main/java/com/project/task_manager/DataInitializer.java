package com.project.task_manager;

import com.project.task_manager.model.Role;
import com.project.task_manager.model.User;
import com.project.task_manager.repository.RoleRepository;
import com.project.task_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Create default roles if they don't exist
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    role.setDescription("Administrator role with full access");
                    return roleRepository.save(role);
                });

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("USER");
                    role.setDescription("Regular user role");
                    return roleRepository.save(role);
                });

        // 2. Create default admin user if doesn't exist
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@taskmanager.com");
            admin.setPassword(passwordEncoder.encode("Admin@123")); // Change this!
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setIsActive(true);
            admin.setIsVerified(true);
            admin.getRoles().add(adminRole); // Assign admin role

            userRepository.save(admin);
            System.out.println("Default admin user created with ADMIN role");
        }
    }
}
