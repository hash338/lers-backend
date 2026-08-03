package lb.lers.employee;

public class EmployeeRequest {
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String password;
    private String shift;
    private String dutyStatus;

    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getRole() { return role; }
    public void setRole(String v) { this.role = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
    public String getShift() { return shift; }
    public void setShift(String v) { this.shift = v; }
    public String getDutyStatus() { return dutyStatus; }
    public void setDutyStatus(String v) { this.dutyStatus = v; }
}