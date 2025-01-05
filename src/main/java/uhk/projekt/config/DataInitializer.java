package uhk.projekt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import uhk.projekt.model.Role;
import uhk.projekt.model.User;
import uhk.projekt.repository.RoleRepository;
import uhk.projekt.repository.UserRepository;

import java.util.Optional;

@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {
            String[] roles = {"ADMIN", "USER"};

            for (String roleName : roles) {
                if (!roleRepository.existsByName(roleName)) {
                    Role role = new Role();
                    role.setName(roleName);
                    roleRepository.save(role);
                }
            }

            String adminEmail = "admin@company.com";
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setName("Admin");
                admin.setSurname("User");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("adminpass"));

                Optional<Role> adminRole = roleRepository.findByName("ADMIN");
                adminRole.ifPresent(role -> {
                    admin.getRoles().add(role);
                });

                userRepository.save(admin);
            }

            String userEmail = "user@comapny.com";
            if (!userRepository.existsByEmail(userEmail)) {
                User user = new User();
                user.setName("Regular");
                user.setSurname("User");
                user.setEmail(userEmail);
                user.setPassword(passwordEncoder.encode("userpass"));

                Optional<Role> userRole = roleRepository.findByName("USER");
                userRole.ifPresent(role -> {
                    user.getRoles().add(role);
                });

                userRepository.save(user);
            }
        };
    }
}