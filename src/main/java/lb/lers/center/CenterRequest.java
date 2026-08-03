package lb.lers.center;

public class CenterRequest {
    private String code;
    private String name;
    private String type;
    private String region;
    private String address;
    private String phone;
    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;
    private String status;

    public String getCode() { return code; }
    public void setCode(String v) { this.code = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getType() { return type; }
    public void setType(String v) { this.type = v; }
    public String getRegion() { return region; }
    public void setRegion(String v) { this.region = v; }
    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public java.math.BigDecimal getLatitude() { return latitude; }
    public void setLatitude(java.math.BigDecimal v) { this.latitude = v; }
    public java.math.BigDecimal getLongitude() { return longitude; }
    public void setLongitude(java.math.BigDecimal v) { this.longitude = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
}