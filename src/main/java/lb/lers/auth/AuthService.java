package lb.lers.auth;

import lb.lers.user.User;
import lb.lers.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest request) {
        User user;

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            System.out.println("LOGIN email: [" + request.getEmail().trim() + "]");
            user = userRepository.findByEmail(request.getEmail().trim()).orElse(null);
            if (user == null) {
                userRepository.findAll().forEach(u ->
                        System.out.println("DB has: [" + u.getEmail() + "]"));
                throw new RuntimeException("Invalid credentials");
            }
        } else if (request.getPhone() != null && !request.getPhone().isBlank()) {
            user = userRepository.findByPhone(request.getPhone())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        } else {
            throw new RuntimeException("Email or phone required");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("PASSWORD MISMATCH");
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.getStatus().equals("active")) {
            throw new RuntimeException("Account inactive");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .token(token).refreshToken(refreshToken)
                .userId(user.getId()).role(user.getRole())
                .fullName(user.getFullName()).email(user.getEmail())
                .phone(user.getPhone()).centerId(user.getCenterId())
                .build();
    }

    public AuthResponse registerCitizen(RegisterRequest request) {
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone()))
            throw new RuntimeException("Phone already registered");
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already registered");

        User user = User.builder()
                .fullName(request.getFullName()).phone(request.getPhone())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("CITIZEN").status("active").build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .token(token).refreshToken(refreshToken)
                .userId(user.getId()).role(user.getRole())
                .fullName(user.getFullName()).email(user.getEmail())
                .phone(user.getPhone()).build();
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtUtil.isValid(refreshToken))
            throw new RuntimeException("Invalid refresh token");

        UUID userId = jwtUtil.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newToken = jwtUtil.generateToken(user.getId(), user.getRole());
        String newRefresh = jwtUtil.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .token(newToken).refreshToken(newRefresh)
                .userId(user.getId()).role(user.getRole())
                .fullName(user.getFullName()).email(user.getEmail())
                .phone(user.getPhone()).centerId(user.getCenterId())
                .build();
    }

    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new RuntimeException("Old password incorrect");
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}