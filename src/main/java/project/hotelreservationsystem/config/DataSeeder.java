package project.hotelreservationsystem.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.hotelreservationsystem.entity.User;
import project.hotelreservationsystem.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedAdmin() {
        return args -> {
            if (!userRepository.existsByEmail("admin@hotel.com")) {
                User admin = User.builder()
                        .fullName("Hotel Admin")
                        .username("admin")
                        .email("admin@hotel.com")
                        .passwordHash(passwordEncoder.encode("Admin@123"))
                        .role("ADMIN")
                        .build();

                userRepository.save(admin);
                System.out.println("✅ Admin user created successfully!");
            } else {
                System.out.println("ℹ️ Admin user already exists.");
            }
        };
    }
}