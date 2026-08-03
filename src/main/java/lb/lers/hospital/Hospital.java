package lb.lers.hospital;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "hospitals")
public class Hospital {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String region;
    private String phone;
    private String address;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private HospitalType type = HospitalType.GENERAL;

    @Column(name = "admin_email")
    private String adminEmail;

    @Column(name = "admin_user_id")
    private UUID adminUserId;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Floor> floors;

    private LocalDateTime createdAt = LocalDateTime.now();
}