package lb.lers.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    List<Vehicle> findByCenterId(UUID centerId);
    List<Vehicle> findByCenterIdAndStatus(UUID centerId, String status);
    boolean existsByCode(String code);
    boolean existsByPlate(String plate);
}