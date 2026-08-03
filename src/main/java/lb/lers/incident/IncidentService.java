package lb.lers.incident;

import lb.lers.ai.GeminiService;
import lb.lers.center.Center;
import lb.lers.center.CenterRepository;
import lb.lers.employee.EmployeeStatusRepository;
import lb.lers.notification.WsBroadcaster;
import lb.lers.vehicle.VehicleService;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final CenterRepository centerRepository;
    private final GeminiService geminiService;
    private final WsBroadcaster wsBroadcaster;
    private final EmployeeStatusRepository statusRepository;
    private final VehicleService vehicleService;
    private final lb.lers.FirebaseMessagingService fcmService;
    private final lb.lers.user.UserRepository userRepository;

    public IncidentService(IncidentRepository incidentRepository,
                           CenterRepository centerRepository,
                           GeminiService geminiService,
                           WsBroadcaster wsBroadcaster,
                           EmployeeStatusRepository statusRepository,
                           VehicleService vehicleService,
                           lb.lers.FirebaseMessagingService fcmService,
                           lb.lers.user.UserRepository userRepository) {
        this.incidentRepository = incidentRepository;
        this.centerRepository = centerRepository;
        this.geminiService = geminiService;
        this.wsBroadcaster = wsBroadcaster;
        this.statusRepository = statusRepository;
        this.vehicleService = vehicleService;
        this.fcmService = fcmService;
        this.userRepository = userRepository;
    }

    public Incident report(IncidentRequest request, UUID citizenId) {

        Center nearest = centerRepository.findNearest(
                request.getLatitude(), request.getLongitude()
        );

        if (nearest == null)
            throw new RuntimeException("No active center found");

        String code = "INC-" + System.currentTimeMillis();

        Incident incident = new Incident();
        incident.setCode(code);
        incident.setType(request.getType());
        incident.setPriority("HIGH");
        incident.setStatus("NEW");
        incident.setLocationText(request.getLocationText());
        incident.setLatitude(request.getLatitude());
        incident.setLongitude(request.getLongitude());
        incident.setPhotoUrl(request.getPhotoUrl());
        incident.setTransferFrom(request.getTransferFrom());
        incident.setTransferTo(request.getTransferTo());
        incident.setTransferFloor(request.getTransferFloor());
        incident.setCitizenId(citizenId);
        incident.setCenterId(nearest.getId());
        incident.setReportedAt(Instant.now());

        incidentRepository.save(incident);

        // Gemini AI summary
        try {
            GeminiService.AiResult ai = geminiService.summarizeIncident(
                    request.getType(),
                    request.getLocationText() != null
                            ? request.getLocationText() : nearest.getRegion(),
                    request.getNotes()
            );
            incident.setAiSummary(ai.summary);
            incident.setAiRiskLevel(ai.riskLevel);
            incidentRepository.save(incident);
        } catch (Exception e) {
            System.out.println("AI summary skipped: " + e.getMessage());
        }

        // Broadcast to center
        wsBroadcaster.broadcastToCenter(
                nearest.getId().toString(), incident);

        // Notify dispatcher
        userRepository.findAll().stream()
                .filter(u -> "DISPATCHER".equals(u.getRole()) && nearest.getId().equals(u.getCenterId()))
                .findFirst()
                .ifPresent(dispatcher -> fcmService.sendNotification(
                        dispatcher.getFcmToken(),
                        "🚨 New Incident",
                        request.getType() + " reported at " + (request.getLocationText() != null ? request.getLocationText() : "unknown location")
                ));

        return incident;
    }

    public List<Incident> getByCenterId(UUID centerId) {
        return incidentRepository.findByCenterIdOrderByReportedAtDesc(centerId);
    }

    public List<Incident> getMine(UUID citizenId) {
        return incidentRepository.findByCitizenId(citizenId);
    }

    public List<Incident> getAssignedToEmployee(UUID employeeId) {
        return incidentRepository.findByDriverIdOrParamedicIdOrHelperId(
                employeeId, employeeId, employeeId);
    }

    public void updatePatientCondition(UUID id, String condition) {
        Incident inc = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        inc.setPatientCondition(condition);
        incidentRepository.save(inc);
    }

    public Incident getById(UUID id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
    }

    public Incident updateStatus(UUID id, String status) {
        Incident incident = getById(id);
        incident.setStatus(status);
        if ("ON_THE_WAY".equals(status)) incident.setAcceptedAt(Instant.now());
        if ("ON_SCENE".equals(status))   incident.setArrivedAt(Instant.now());
        if ("CLOSED".equals(status)) {
            incident.setClosedAt(Instant.now());
            // Release team back to ON_DUTY
            if (incident.getDriverId()    != null) updateEmployeeDuty(incident.getDriverId(),    "ON_DUTY");
            if (incident.getParamedicId() != null) updateEmployeeDuty(incident.getParamedicId(), "ON_DUTY");
            if (incident.getHelperId()    != null) updateEmployeeDuty(incident.getHelperId(),    "ON_DUTY");
            // Release vehicle back to available
            if (incident.getVehicleId()   != null) vehicleService.setStatus(incident.getVehicleId(), "available");
        }
        incidentRepository.save(incident);
        if (incident.getCitizenId() != null) {
            userRepository.findById(incident.getCitizenId()).ifPresent(citizen -> {
                String title = "Incident Update";
                String body = switch (status) {
                    case "DISPATCHED" -> "A team has been dispatched to your location!";
                    case "ON_THE_WAY" -> "The team is on the way!";
                    case "ON_SCENE" -> "The team has arrived at the scene!";
                    case "CLOSED" -> "Your incident has been resolved. Stay safe!";
                    default -> "Your incident status has been updated to " + status;
                };
                fcmService.sendNotification(citizen.getFcmToken(), title, body);
            });
        }
        wsBroadcaster.broadcastToCenter(incident.getCenterId().toString(), incident);
        wsBroadcaster.broadcastIncident(incident.getId().toString(), incident);
        return incident;
    }

    public Incident dispatch(UUID id, UUID driverId, UUID paramedicId, UUID helperId, UUID vehicleId) {
        Incident incident = getById(id);
        incident.setDriverId(driverId);
        incident.setParamedicId(paramedicId);
        incident.setHelperId(helperId);
        incident.setVehicleId(vehicleId);
        incident.setStatus("DISPATCHED");
        incident.setDispatchedAt(Instant.now());
        incidentRepository.save(incident);

        // Set team members to ON_MISSION
        if (driverId    != null) updateEmployeeDuty(driverId,    "ON_MISSION");
        if (paramedicId != null) updateEmployeeDuty(paramedicId, "ON_MISSION");
        if (helperId    != null) updateEmployeeDuty(helperId,    "ON_MISSION");

        // Set vehicle to on-mission
        if (vehicleId != null) vehicleService.setStatus(vehicleId, "on-mission");

        wsBroadcaster.broadcastToCenter(incident.getCenterId().toString(), incident);

        // Notify assigned employees
        if (driverId != null) userRepository.findById(driverId).ifPresent(u ->
                fcmService.sendNotification(u.getFcmToken(), "🚨 New Incident Assigned", "You have been dispatched to an incident!"));
        if (paramedicId != null) userRepository.findById(paramedicId).ifPresent(u ->
                fcmService.sendNotification(u.getFcmToken(), "🚨 New Incident Assigned", "You have been dispatched to an incident!"));
        if (helperId != null) userRepository.findById(helperId).ifPresent(u ->
                fcmService.sendNotification(u.getFcmToken(), "🚨 New Incident Assigned", "You have been dispatched to an incident!"));

        wsBroadcaster.broadcastIncident(incident.getId().toString(), incident);
        return incident;
    }

    public List<Incident> getAll() {
        return incidentRepository.findAll();
    }

    private void updateEmployeeDuty(UUID userId, String status) {
        try {
            statusRepository.findById(userId).ifPresent(s -> {
                s.setDutyStatus(status);
                s.setUpdatedAt(java.time.Instant.now());
                statusRepository.save(s);
            });
        } catch (Exception e) {
            System.out.println("Could not update employee duty: " + e.getMessage());
        }
    }
}