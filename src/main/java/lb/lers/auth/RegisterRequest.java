package lb.lers.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String phone;
    private String email;
    private String password;

    private java.util.UUID centerId;
    private String role;

    public java.util.UUID getCenterId() { return centerId; }
    public void setCenterId(java.util.UUID v) { this.centerId = v; }
    public String getRole() { return role; }
    public void setRole(String v) { this.role = v; }
}