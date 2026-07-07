package project.hotelreservationsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.hotelreservationsystem.dto.AuthResponseDto;
import project.hotelreservationsystem.dto.LoginDto;
import project.hotelreservationsystem.dto.RegisterDto;
import project.hotelreservationsystem.entity.Customer;
import project.hotelreservationsystem.entity.User;
import project.hotelreservationsystem.exception.DuplicateResourceException;
import project.hotelreservationsystem.exception.ResourceNotFoundException;
import project.hotelreservationsystem.repository.CustomerRepository;
import project.hotelreservationsystem.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Public self-registration — CUSTOMER kaliya
    public AuthResponseDto register(RegisterDto dto) {
        AuthResponseDto response = createUser(dto, "CUSTOMER");

        // Abuur diiwaan Customer ah oo la xiriira (Reservations waxay tixraacayaan Customer, ma ahan User)
        if (!customerRepository.existsByEmail(dto.getEmail())) {
            String fullName = dto.getFullName() == null ? "" : dto.getFullName().trim();
            int spaceIdx = fullName.indexOf(' ');
            String firstName = spaceIdx > 0 ? fullName.substring(0, spaceIdx) : fullName;
            String lastName = spaceIdx > 0 ? fullName.substring(spaceIdx + 1).trim() : "";

            Customer customer = Customer.builder()
                    .firstName(firstName.isEmpty() ? fullName : firstName)
                    .lastName(lastName)
                    .phone(dto.getPhone())
                    .email(dto.getEmail())
                    .address(dto.getAddress())
                    .build();

            customerRepository.save(customer);
        }

        return response;
    }

    // ADMIN kaliya ayaa isticmaali kara — abuuraya admin kale
    public AuthResponseDto registerAdmin(RegisterDto dto) {
        return createUser(dto, "ADMIN");
    }

    private AuthResponseDto createUser(RegisterDto dto, String role) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateResourceException("Email already exists");

        if (userRepository.existsByUsername(dto.getUsername()))
            throw new DuplicateResourceException("Username already exists");

        User user = User.builder()
                .fullName(dto.getFullName())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponseDto(token, user.getRole(), user.getFullName());
    }

    public AuthResponseDto login(LoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash()))
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid password");

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponseDto(token, user.getRole(), user.getFullName());
    }
}