package lb.lers.incident;

import lb.lers.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // POST citizen reports incident
    @PostMapping("/report")
    public ResponseEntity<Map<String, Object>> report(
            @RequestBody IncidentRequest request,
            @AuthenticationPrincipal User currentUser) {
        Incident incident = incidentService.report(request, currentUser.getId());
        return ResponseEntity.ok(Map.of(
                "incidentId", incident.getId(),
                "code", incident.getCode(),
                "message", "Request received ✓",
                "centerId", incident.getCenterId()
        ));
    }

    // GET all incidents for dispatcher/center admin
    @GetMapping
    public ResponseEntity<List<Incident>> getAll(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                incidentService.getByCenterId(currentUser.getCenterId()));
    }

    // GET citizen's own incidents
    @GetMapping("/mine")
    public ResponseEntity<List<Incident>> getMine(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                incidentService.getMine(currentUser.getId()));
    }

    // GET incidents assigned to employee
    @GetMapping("/assigned/me")
    public ResponseEntity<List<Incident>> getAssigned(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                incidentService.getAssignedToEmployee(currentUser.getId()));
    }

    // GET one incident
    @GetMapping("/{id}")
    public ResponseEntity<Incident> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(incidentService.getById(id));
    }

    // PATCH update status (employee)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Incident> updateStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                incidentService.updateStatus(id, body.get("status")));
    }

    @PatchMapping("/{id}/condition")
    public ResponseEntity<Void> updateCondition(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        incidentService.updatePatientCondition(id, body.get("patientCondition"));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/dispatch")
    @PreAuthorize("hasRole('DISPATCHER')")
    public ResponseEntity<Incident> dispatch(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        UUID driverId    = body.get("driverId")    != null ? UUID.fromString(body.get("driverId"))    : null;
        UUID paramedicId = body.get("paramedicId") != null ? UUID.fromString(body.get("paramedicId")) : null;
        UUID helperId    = body.get("helperId")    != null ? UUID.fromString(body.get("helperId"))    : null;
        UUID vehicleId   = body.get("vehicleId")   != null ? UUID.fromString(body.get("vehicleId"))   : null;
        return ResponseEntity.ok(incidentService.dispatch(id, driverId, paramedicId, helperId, vehicleId));
    }

    // GET all incidents — Super Admin only
    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Incident>> getAll() {
        return ResponseEntity.ok(incidentService.getAll());
    }
}