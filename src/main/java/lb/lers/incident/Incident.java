package lb.lers.incident;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String type;

    private String priority = "HIGH";
    private String status = "NEW";
    private String patientCondition;

    @Column(name = "hospital_latitude")
    private BigDecimal hospitalLatitude;

    @Column(name = "hospital_longitude")
    private BigDecimal hospitalLongitude;

    @Column(name = "hospital_name")
    private String hospitalName;

    @Column(name = "hospital_floor")
    private String hospitalFloor;

    @Column(name = "hospital_room")
    private String hospitalRoom;

    @Column(name = "hospital_address")
    private String hospitalAddress;

    @Column(name = "location_text")
    private String locationText;

    @Column(nullable = false, columnDefinition = "DECIMAL(9,6)")
    private BigDecimal latitude;

    @Column(nullable = false, columnDefinition = "DECIMAL(9,6)")
    private BigDecimal longitude;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "transfer_from")
    private String transferFrom;

    @Column(name = "transfer_to")
    private String transferTo;

    @Column(name = "transfer_floor")
    private String transferFloor;

    @Column(name = "citizen_id")
    private UUID citizenId;

    @Column(name = "center_id")
    private UUID centerId;

    @Column(name = "dispatcher_id")
    private UUID dispatcherId;

    @Column(name = "vehicle_id")
    private UUID vehicleId;

    @Column(name = "driver_id")
    private UUID driverId;

    @Column(name = "paramedic_id")
    private UUID paramedicId;

    @Column(name = "helper_id")
    private UUID helperId;

    @Column(name = "driver_response")
    private String driverResponse;

    @Column(name = "paramedic_response")
    private String paramedicResponse;

    @Column(name = "helper_response")
    private String helperResponse;

    @Column(name = "ai_summary")
    private String aiSummary;

    @Column(name = "ai_risk_level")
    private String aiRiskLevel;

    @Column(name = "backup_triggered")
    private Boolean backupTriggered = false;

    @Column(name = "backup_roles")
    private String backupRoles;

    @Column(name = "backup_center_id")
    private UUID backupCenterId;

    @Column(name = "backup_at")
    private Instant backupAt;

    @Column(name = "reported_at")
    private Instant reportedAt;

    @Column(name = "dispatched_at")
    private Instant dispatchedAt;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "arrived_at")
    private Instant arrivedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    public Incident() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLocationText() { return locationText; }
    public void setLocationText(String locationText) { this.locationText = locationText; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getTransferFrom() { return transferFrom; }
    public void setTransferFrom(String transferFrom) { this.transferFrom = transferFrom; }
    public String getTransferTo() { return transferTo; }
    public void setTransferTo(String transferTo) { this.transferTo = transferTo; }
    public String getTransferFloor() { return transferFloor; }
    public void setTransferFloor(String transferFloor) { this.transferFloor = transferFloor; }
    public UUID getCitizenId() { return citizenId; }
    public void setCitizenId(UUID citizenId) { this.citizenId = citizenId; }
    public UUID getCenterId() { return centerId; }
    public void setCenterId(UUID centerId) { this.centerId = centerId; }
    public UUID getDispatcherId() { return dispatcherId; }
    public void setDispatcherId(UUID dispatcherId) { this.dispatcherId = dispatcherId; }
    public UUID getVehicleId() { return vehicleId; }
    public void setVehicleId(UUID vehicleId) { this.vehicleId = vehicleId; }
    public UUID getDriverId() { return driverId; }
    public void setDriverId(UUID driverId) { this.driverId = driverId; }
    public UUID getParamedicId() { return paramedicId; }
    public void setParamedicId(UUID paramedicId) { this.paramedicId = paramedicId; }
    public UUID getHelperId() { return helperId; }
    public void setHelperId(UUID helperId) { this.helperId = helperId; }
    public String getDriverResponse() { return driverResponse; }
    public void setDriverResponse(String driverResponse) { this.driverResponse = driverResponse; }
    public String getParamedicResponse() { return paramedicResponse; }
    public void setParamedicResponse(String paramedicResponse) { this.paramedicResponse = paramedicResponse; }
    public String getHelperResponse() { return helperResponse; }
    public void setHelperResponse(String helperResponse) { this.helperResponse = helperResponse; }
    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
    public String getAiRiskLevel() { return aiRiskLevel; }
    public void setAiRiskLevel(String aiRiskLevel) { this.aiRiskLevel = aiRiskLevel; }
    public Boolean getBackupTriggered() { return backupTriggered; }
    public void setBackupTriggered(Boolean backupTriggered) { this.backupTriggered = backupTriggered; }
    public String getBackupRoles() { return backupRoles; }
    public void setBackupRoles(String backupRoles) { this.backupRoles = backupRoles; }
    public UUID getBackupCenterId() { return backupCenterId; }
    public void setBackupCenterId(UUID backupCenterId) { this.backupCenterId = backupCenterId; }
    public Instant getBackupAt() { return backupAt; }
    public void setBackupAt(Instant backupAt) { this.backupAt = backupAt; }
    public Instant getReportedAt() { return reportedAt; }
    public void setReportedAt(Instant reportedAt) { this.reportedAt = reportedAt; }
    public Instant getDispatchedAt() { return dispatchedAt; }
    public void setDispatchedAt(Instant dispatchedAt) { this.dispatchedAt = dispatchedAt; }
    public Instant getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(Instant acceptedAt) { this.acceptedAt = acceptedAt; }
    public Instant getArrivedAt() { return arrivedAt; }
    public void setArrivedAt(Instant arrivedAt) { this.arrivedAt = arrivedAt; }
    public Instant getClosedAt() { return closedAt; }
    public void setClosedAt(Instant closedAt) { this.closedAt = closedAt; }
    public String getPatientCondition() { return patientCondition; }
    public void setPatientCondition(String patientCondition) { this.patientCondition = patientCondition; }
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public String getHospitalFloor() { return hospitalFloor; }
    public void setHospitalFloor(String hospitalFloor) { this.hospitalFloor = hospitalFloor; }
    public String getHospitalRoom() { return hospitalRoom; }
    public void setHospitalRoom(String hospitalRoom) { this.hospitalRoom = hospitalRoom; }
    public String getHospitalAddress() { return hospitalAddress; }
    public void setHospitalAddress(String hospitalAddress) { this.hospitalAddress = hospitalAddress; }
    public BigDecimal getHospitalLatitude() { return hospitalLatitude; }
    public void setHospitalLatitude(BigDecimal v) { this.hospitalLatitude = v; }
    public BigDecimal getHospitalLongitude() { return hospitalLongitude; }
    public void setHospitalLongitude(BigDecimal v) { this.hospitalLongitude = v; }
}