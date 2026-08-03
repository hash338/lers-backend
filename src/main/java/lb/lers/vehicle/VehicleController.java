package lb.lers.vehicle;

import lb.lers.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // GET all vehicles for my center
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAll(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                vehicleService.getByCenterId(currentUser.getCenterId()));
    }

    // GET available vehicles for my center
    @GetMapping("/available")
    public ResponseEntity<List<Vehicle>> getAvailable(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                vehicleService.getAvailable(currentUser.getCenterId()));
    }

    // GET one vehicle
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }

    // POST create — Center Admin only
    @PostMapping
    @PreAuthorize("hasRole('CENTER_ADMIN')")
    public ResponseEntity<Vehicle> create(
            @RequestBody VehicleRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                vehicleService.create(request,
                        currentUser.getCenterId(),
                        currentUser.getId()));
    }

    // PUT update
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CENTER_ADMIN')")
    public ResponseEntity<Vehicle> update(
            @PathVariable UUID id,
            @RequestBody VehicleRequest request) {
        return ResponseEntity.ok(vehicleService.update(id, request));
    }

    // PATCH update status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID id,
            @RequestBody java.util.Map<String, String> body) {
        vehicleService.setStatus(id, body.get("status"));
        return ResponseEntity.ok().build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CENTER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAll());
    }
}