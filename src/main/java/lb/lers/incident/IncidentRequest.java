package lb.lers.incident;

import java.math.BigDecimal;

public class IncidentRequest {
    private String type;
    private String locationText;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String photoUrl;
    private String transferFrom;
    private String transferTo;
    private String transferFloor;
    private String notes;

    public String getType() { return type; }
    public void setType(String v) { this.type = v; }
    public String getLocationText() { return locationText; }
    public void setLocationText(String v) { this.locationText = v; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal v) { this.latitude = v; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal v) { this.longitude = v; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String v) { this.photoUrl = v; }
    public String getTransferFrom() { return transferFrom; }
    public void setTransferFrom(String v) { this.transferFrom = v; }
    public String getTransferTo() { return transferTo; }
    public void setTransferTo(String v) { this.transferTo = v; }
    public String getTransferFloor() { return transferFloor; }
    public void setTransferFloor(String v) { this.transferFloor = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
}