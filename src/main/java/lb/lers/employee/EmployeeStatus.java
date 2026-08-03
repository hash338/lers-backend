package lb.lers.employee;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "employee_status")
public class EmployeeStatus {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "duty_status")
    private String dutyStatus = "off-duty";

    @Column(name = "vehicle_id")
    private UUID vehicleId;

    private String shift;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public EmployeeStatus() {}

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getDutyStatus() { return dutyStatus; }
    public void setDutyStatus(String dutyStatus) { this.dutyStatus = dutyStatus; }
    public UUID getVehicleId() { return vehicleId; }
    public void setVehicleId(UUID vehicleId) { this.vehicleId = vehicleId; }
    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}