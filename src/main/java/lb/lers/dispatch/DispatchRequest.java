package lb.lers.dispatch;

import java.util.UUID;

public class DispatchRequest {
    private UUID incidentId;
    private UUID driverId;
    private UUID paramedicId;
    private UUID helperId;
    private UUID vehicleId;

    public UUID getIncidentId() { return incidentId; }
    public void setIncidentId(UUID v) { this.incidentId = v; }
    public UUID getDriverId() { return driverId; }
    public void setDriverId(UUID v) { this.driverId = v; }
    public UUID getParamedicId() { return paramedicId; }
    public void setParamedicId(UUID v) { this.paramedicId = v; }
    public UUID getHelperId() { return helperId; }
    public void setHelperId(UUID v) { this.helperId = v; }
    public UUID getVehicleId() { return vehicleId; }
    public void setVehicleId(UUID v) { this.vehicleId = v; }
}