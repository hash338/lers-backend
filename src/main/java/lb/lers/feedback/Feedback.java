package lb.lers.feedback;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "incident_id")
    private UUID incidentId;

    @Column(name = "citizen_id")
    private UUID citizenId;

    @Column(name = "rating", columnDefinition = "smallint")
    private Short rating;
    private String message;

    @Column(name = "read_by_admin")
    private Boolean readByAdmin = false;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    public UUID getId() { return id; }
    public UUID getIncidentId() { return incidentId; }
    public UUID getCitizenId() { return citizenId; }
    public Short getRating() { return rating; }
    public void setRating(Short rating) { this.rating = rating; }
    public String getMessage() { return message; }
    public Boolean getReadByAdmin() { return readByAdmin; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setId(UUID id) { this.id = id; }
    public void setIncidentId(UUID incidentId) { this.incidentId = incidentId; }
    public void setCitizenId(UUID citizenId) { this.citizenId = citizenId; }
    public void setMessage(String message) { this.message = message; }
    public void setReadByAdmin(Boolean readByAdmin) { this.readByAdmin = readByAdmin; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}