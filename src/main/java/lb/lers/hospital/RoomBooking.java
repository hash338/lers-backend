package lb.lers.hospital;

import jakarta.persistence.*;
import lb.lers.user.User;
import lb.lers.incident.Incident;
import java.util.UUID;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "room_bookings")
public class RoomBooking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "citizen_id")
    private User citizen;

    @ManyToOne
    @JoinColumn(name = "incident_id")
    private Incident incident;

    private String bookedBy = "CITIZEN"; // CITIZEN or DISPATCHER

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    private String notes;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
