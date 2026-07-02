package project.hotelreservationsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.hotelreservationsystem.dto.AuthResponseDto;
import project.hotelreservationsystem.dto.LoginDto;
import project.hotelreservationsystem.dto.RegisterDto;
import project.hotelreservationsystem.entity.User;
import project.hotelreservationsystem.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDto register(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("Email already exists");

        if (userRepository.existsByUsername(dto.getUsername()))
            throw new RuntimeException("Username already exists");

        User user = User.builder()
                .fullName(dto.getFullName())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole() != null ? dto.getRole() : "STAFF")
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponseDto(token, user.getRole(), user.getFullName());
    }

    public AuthResponseDto login(LoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash()))
            throw new RuntimeException("Invalid password");

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponseDto(token, user.getRole(), user.getFullName());
    }
}