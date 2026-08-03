package lb.lers.auth;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String phone;
    private String password;
}