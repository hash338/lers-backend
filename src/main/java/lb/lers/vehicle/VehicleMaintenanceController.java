package lb.lers.vehicle;

import lb.lers.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/maintenance")
public class VehicleMaintenanceController {

    private final VehicleMaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;

    public VehicleMaintenanceController(
            VehicleMaintenanceRepository maintenanceRepository,
            VehicleRepository vehicleRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping("/center")
    @PreAuthorize("hasRole('CENTER_ADMIN')")
    public ResponseEntity<List<VehicleMaintenance>> getByCenter(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                maintenanceRepository.findByCenterId(currentUser.getCenterId()));
    }

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasRole('CENTER_ADMIN')")
    public ResponseEntity<List<VehicleMaintenance>> getByVehicle(
            @PathVariable UUID vehicleId) {
        return ResponseEntity.ok(
                maintenanceRepository.findByVehicleIdOrderByServiceDateDesc(vehicleId));
    }

    @PostMapping
    @PreAuthorize("hasRole('CENTER_ADMIN')")
    public ResponseEntity<VehicleMaintenance> create(
            @RequestBody Map<String, Object> body) {
        VehicleMaintenance m = new VehicleMaintenance();
        m.setVehicleId(UUID.fromString(body.get("vehicleId").toString()));
        m.setServiceDate(LocalDate.parse(body.get("serviceDate").toString()));
        m.setDescription(body.getOrDefault("description", "").toString());
        if (body.get("mileageAtService") != null)
            m.setMileageAtService(Integer.parseInt(body.get("mileageAtService").toString()));
        if (body.get("cost") != null && !body.get("cost").toString().isEmpty())
            m.setCost(new BigDecimal(body.get("cost").toString()));
        return ResponseEntity.ok(maintenanceRepository.save(m));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CENTER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        maintenanceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}