package lb.lers.center;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "centers")
public class Center {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    private String region;
    private String address;
    private String phone;

    @Column(nullable = false, columnDefinition = "DECIMAL(9,6)")
    private java.math.BigDecimal latitude;

    @Column(nullable = false, columnDefinition = "DECIMAL(9,6)")
    private java.math.BigDecimal longitude;

    @Column(name = "admin_id")
    private UUID adminId;

    private String status = "active";

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    public Center() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public java.math.BigDecimal getLatitude() { return latitude; }
    public void setLatitude(java.math.BigDecimal latitude) { this.latitude = latitude; }
    public java.math.BigDecimal getLongitude() { return longitude; }
    public void setLongitude(java.math.BigDecimal longitude) { this.longitude = longitude; }
    public UUID getAdminId() { return adminId; }
    public void setAdminId(UUID adminId) { this.adminId = adminId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Center c = new Center();
        public Builder code(String v) { c.code = v; return this; }
        public Builder name(String v) { c.name = v; return this; }
        public Builder type(String v) { c.type = v; return this; }
        public Builder region(String v) { c.region = v; return this; }
        public Builder address(String v) { c.address = v; return this; }
        public Builder phone(String v) { c.phone = v; return this; }
        public Builder latitude(java.math.BigDecimal v) { c.latitude = v; return this; }
        public Builder longitude(java.math.BigDecimal v) { c.longitude = v; return this; }
        public Builder adminId(UUID v) { c.adminId = v; return this; }
        public Builder status(String v) { c.status = v; return this; }
        public Center build() { return c; }
    }
}