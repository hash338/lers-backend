package lb.lers.vehicle;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(unique = true, nullable = false)
    private String plate;

    private String type;
    private String model;

    @Column(name = "center_id")
    private UUID centerId;

    private String status = "available";

    private Integer mileage = 0;

    @Column(name = "last_service")
    private LocalDate lastService;

    @Column(name = "created_by")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    public Vehicle() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public UUID getCenterId() { return centerId; }
    public void setCenterId(UUID centerId) { this.centerId = centerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }
    public LocalDate getLastService() { return lastService; }
    public void setLastService(LocalDate lastService) { this.lastService = lastService; }
    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
    public Instant getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Vehicle v = new Vehicle();
        public Builder code(String val) { v.code = val; return this; }
        public Builder plate(String val) { v.plate = val; return this; }
        public Builder type(String val) { v.type = val; return this; }
        public Builder model(String val) { v.model = val; return this; }
        public Builder centerId(UUID val) { v.centerId = val; return this; }
        public Builder status(String val) { v.status = val; return this; }
        public Builder createdBy(UUID val) { v.createdBy = val; return this; }
        public Vehicle build() { return v; }
    }
}