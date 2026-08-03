package lb.lers.user;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "center_id")
    private UUID centerId;

    @Column(name = "hospital_id")
    private Long hospitalId;

    private String language = "ENG";
    private String theme = "SYSTEM";
    private String status = "active";

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "created_by")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public User() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public UUID getCenterId() { return centerId; }
    public void setCenterId(UUID centerId) { this.centerId = centerId; }

    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final User user = new User();
        public Builder id(UUID id) { user.id = id; return this; }
        public Builder email(String email) { user.email = email; return this; }
        public Builder phone(String phone) { user.phone = phone; return this; }
        public Builder password(String password) { user.password = password; return this; }
        public Builder role(String role) { user.role = role; return this; }
        public Builder fullName(String fullName) { user.fullName = fullName; return this; }
        public Builder photoUrl(String photoUrl) { user.photoUrl = photoUrl; return this; }
        public Builder centerId(UUID centerId) { user.centerId = centerId; return this; }
        public Builder hospitalId(Long hospitalId) { user.hospitalId = hospitalId; return this; }
        public Builder language(String language) { user.language = language; return this; }
        public Builder theme(String theme) { user.theme = theme; return this; }
        public Builder status(String status) { user.status = status; return this; }
        public Builder fcmToken(String fcmToken) { user.fcmToken = fcmToken; return this; }
        public Builder createdBy(UUID createdBy) { user.createdBy = createdBy; return this; }
        public User build() { return user; }
    }
}