package lb.lers.hospital;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {
    List<RoomBooking> findByCitizenId(java.util.UUID citizenId);
    List<RoomBooking> findByRoomFloorHospitalId(Long hospitalId);
}
