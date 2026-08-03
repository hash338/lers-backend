package lb.lers.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface VehicleMaintenanceRepository extends JpaRepository<VehicleMaintenance, UUID> {
    List<VehicleMaintenance> findByVehicleIdOrderByServiceDateDesc(UUID vehicleId);

    @Query("SELECT m FROM VehicleMaintenance m WHERE m.vehicleId IN " +
            "(SELECT v.id FROM Vehicle v WHERE v.centerId = :centerId)")
    List<VehicleMaintenance> findByCenterId(@Param("centerId") UUID centerId);
}