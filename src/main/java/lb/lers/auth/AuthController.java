package lb.lers.auth;

import lb.lers.user.User;
import lb.lers.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register/staff")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<lb.lers.user.User> registerStaff(@RequestBody RegisterRequest request) {
        lb.lers.user.User user = lb.lers.user.User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                        .encode(request.getPassword() != null ? request.getPassword() : "lers2024"))
                .role(request.getRole() != null ? request.getRole() : "CENTER_ADMIN")
                .status("active")
                .build();
        if (request.getCenterId() != null) {
            user.setCenterId(request.getCenterId());
        }
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable java.util.UUID id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }

    @PostMapping("/register/citizen")
    public ResponseEntity<AuthResponse> registerCitizen(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerCitizen(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.refresh(body.get("refreshToken")));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal User currentUser,
            @RequestBody Map<String, String> body) {
        authService.changePassword(
                currentUser.getId(),
                body.get("oldPassword"),
                body.get("newPassword")
        );
        return ResponseEntity.ok(Map.of("message", "Password changed"));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PatchMapping("/fcm-token")
    public ResponseEntity<Map<String, String>> updateFcmToken(
            @AuthenticationPrincipal User currentUser,
            @RequestBody Map<String, String> body) {
        currentUser.setFcmToken(body.get("fcmToken"));
        userRepository.save(currentUser);
        return ResponseEntity.ok(Map.of("message", "FCM token updated"));
    }

    // TEMPORARY - DELETE AFTER USE
    @GetMapping("/generate-hash")
    public ResponseEntity<String> generateHash(@RequestParam String password) {
        return ResponseEntity.ok(
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                        .encode(password)
        );
    }
}