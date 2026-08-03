package lb.lers.hospital;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByFloorId(Long floorId);
    List<Room> findByFloorHospitalIdAndStatus(Long hospitalId, RoomStatus status);
}
