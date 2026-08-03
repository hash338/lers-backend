package lb.lers.analytics;

import lb.lers.center.CenterRepository;
import lb.lers.incident.Incident;
import lb.lers.incident.IncidentRepository;
import lb.lers.user.User;
import lb.lers.user.UserRepository;
import lb.lers.vehicle.VehicleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final IncidentRepository incidentRepository;
    private final CenterRepository centerRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public AnalyticsController(IncidentRepository incidentRepository,
                               CenterRepository centerRepository,
                               UserRepository userRepository,
                               VehicleRepository vehicleRepository) {
        this.incidentRepository = incidentRepository;
        this.centerRepository = centerRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    // GET national overview — Super Admin
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> overview() {
        List<Incident> all = incidentRepository.findAll();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalIncidents", all.size());
        stats.put("totalCenters", centerRepository.count());
        stats.put("totalVehicles", vehicleRepository.count());
        stats.put("activeIncidents",
                all.stream().filter(i ->
                        !List.of("CLOSED").contains(i.getStatus())).count());
        stats.put("closedIncidents",
                all.stream().filter(i ->
                        "CLOSED".equals(i.getStatus())).count());
        stats.put("backupTriggered",
                all.stream().filter(i ->
                        Boolean.TRUE.equals(i.getBackupTriggered())).count());

        // By type
        Map<String, Long> byType = all.stream()
                .collect(Collectors.groupingBy(
                        Incident::getType, Collectors.counting()));
        stats.put("byType", byType);

        // By status
        Map<String, Long> byStatus = all.stream()
                .collect(Collectors.groupingBy(
                        Incident::getStatus, Collectors.counting()));
        stats.put("byStatus", byStatus);

        return ResponseEntity.ok(stats);
    }

    // GET center stats
    @GetMapping("/center/{centerId}")
    public ResponseEntity<Map<String, Object>> centerStats(
            @PathVariable UUID centerId) {
        List<Incident> incidents =
                incidentRepository.findByCenterIdOrderByReportedAtDesc(centerId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalIncidents", incidents.size());
        stats.put("activeIncidents",
                incidents.stream().filter(i ->
                        !List.of("CLOSED").contains(i.getStatus())).count());
        stats.put("closedIncidents",
                incidents.stream().filter(i ->
                        "CLOSED".equals(i.getStatus())).count());
        stats.put("backupTriggered",
                incidents.stream().filter(i ->
                        Boolean.TRUE.equals(i.getBackupTriggered())).count());
        stats.put("totalEmployees",
                userRepository.findAll().stream()
                        .filter(u -> centerId.equals(u.getCenterId()))
                        .filter(u -> List.of("DRIVER","PARAMEDIC","PARAMEDIC_HELPER")
                                .contains(u.getRole()))
                        .count());
        stats.put("totalVehicles",
                vehicleRepository.findByCenterId(centerId).size());

        Map<String, Long> byType = incidents.stream()
                .collect(Collectors.groupingBy(
                        Incident::getType, Collectors.counting()));
        stats.put("byType", byType);

        return ResponseEntity.ok(stats);
    }

    // GET my stats — Dispatcher
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> myStats(
            @AuthenticationPrincipal User currentUser) {
        List<Incident> all =
                incidentRepository.findByCenterIdOrderByReportedAtDesc(
                        currentUser.getCenterId());

        List<Incident> mine = all.stream()
                .filter(i -> currentUser.getId().equals(i.getDispatcherId()))
                .collect(Collectors.toList());

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDispatched", mine.size());
        stats.put("closed",
                mine.stream().filter(i ->
                        "CLOSED".equals(i.getStatus())).count());
        stats.put("backup",
                mine.stream().filter(i ->
                        Boolean.TRUE.equals(i.getBackupTriggered())).count());

        return ResponseEntity.ok(stats);
    }
}