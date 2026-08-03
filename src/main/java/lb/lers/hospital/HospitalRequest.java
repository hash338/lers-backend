package lb.lers.hospital;

import lombok.Data;

@Data
public class HospitalRequest {
    private String name;
    private String region;
    private String phone;
    private String address;
    private Double latitude;
    private Double longitude;
    private HospitalType type;
    private String adminEmail;
    private String adminPassword;
}