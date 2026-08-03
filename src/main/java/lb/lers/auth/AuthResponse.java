package lb.lers.auth;

import java.util.UUID;

public class AuthResponse {
    private String token;
    private String refreshToken;
    private UUID userId;
    private String role;
    private String fullName;
    private String email;
    private String phone;
    private UUID centerId;

    public AuthResponse() {}

    public String getToken() { return token; }
    public String getRefreshToken() { return refreshToken; }
    public UUID getUserId() { return userId; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public UUID getCenterId() { return centerId; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final AuthResponse r = new AuthResponse();
        public Builder token(String v) { r.token = v; return this; }
        public Builder refreshToken(String v) { r.refreshToken = v; return this; }
        public Builder userId(UUID v) { r.userId = v; return this; }
        public Builder role(String v) { r.role = v; return this; }
        public Builder fullName(String v) { r.fullName = v; return this; }
        public Builder email(String v) { r.email = v; return this; }
        public Builder phone(String v) { r.phone = v; return this; }
        public Builder centerId(UUID v) { r.centerId = v; return this; }
        public AuthResponse build() { return r; }
    }
}