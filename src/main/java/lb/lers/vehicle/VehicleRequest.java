package lb.lers.vehicle;

public class VehicleRequest {
    private String code;
    private String plate;
    private String type;
    private String model;
    private String status;
    private Integer mileage;

    public String getCode() { return code; }
    public void setCode(String v) { this.code = v; }
    public String getPlate() { return plate; }
    public void setPlate(String v) { this.plate = v; }
    public String getType() { return type; }
    public void setType(String v) { this.type = v; }
    public String getModel() { return model; }
    public void setModel(String v) { this.model = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public Integer getMileage() { return mileage; }
    public void setMileage(Integer v) { this.mileage = v; }
}