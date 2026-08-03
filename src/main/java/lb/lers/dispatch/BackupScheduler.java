package lb.lers.dispatch;

import lb.lers.center.Center;
import lb.lers.center.CenterRepository;
import lb.lers.incident.Incident;
import lb.lers.incident.IncidentRepository;
import lb.lers.ai.GeminiService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class BackupScheduler {

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(10);

    private final IncidentRepository incidentRepository;
    private final CenterRepository centerRepository;
    private final GeminiService geminiService;

    public BackupScheduler(IncidentRepository incidentRepository,
                           CenterRepository centerRepository,
                           GeminiService geminiService) {
        this.incidentRepository = incidentRepository;
        this.centerRepository = centerRepository;
        this.geminiService = geminiService;
    }

    public void startTimer(UUID incidentId, int timeoutSeconds) {
        scheduler.schedule(() -> {
            evaluateAndBackup(incidentId);
        }, timeoutSeconds, TimeUnit.SECONDS);
    }

    private void evaluateAndBackup(UUID incidentId) {
        Incident inc = incidentRepository.findById(incidentId).orElse(null);
        if (inc == null || "CLOSED".equals(inc.getStatus())) return;

        boolean driverOk    = "ACCEPTED".equals(inc.getDriverResponse());
        boolean paramedicOk = "ACCEPTED".equals(inc.getParamedicResponse());
        boolean helperOk    = "ACCEPTED".equals(inc.getHelperResponse());

        // Mark non-responders as TIMEOUT
        if (inc.getDriverResponse() == null)
            inc.setDriverResponse("TIMEOUT");
        if (inc.getParamedicResponse() == null)
            inc.setParamedicResponse("TIMEOUT");
        if (inc.getHelperResponse() == null)
            inc.setHelperResponse("TIMEOUT");

        // All accepted — no backup needed
        if (driverOk && paramedicOk && helperOk) {
            inc.setStatus("ON_THE_WAY");
            inc.setAcceptedAt(Instant.now());
            incidentRepository.save(inc);
            return;
        }

        // Collect missing roles
        List<String> missing = new ArrayList<>();
        if (!driverOk)    missing.add("DRIVER");
        if (!paramedicOk) missing.add("PARAMEDIC");
        if (!helperOk)    missing.add("PARAMEDIC_HELPER");

        // Driver accepted — vehicle moves
        if (driverOk) {
            inc.setStatus("PARTIALLY_ACCEPTED");
        } else {
            inc.setStatus("BACKUP");
        }

        // Trigger backup
        triggerBackup(inc, missing);
        incidentRepository.save(inc);
    }

    private void triggerBackup(Incident inc, List<String> missingRoles) {
        try {
            // Find nearest backup center
            List<Center> nearbyCenters = centerRepository.findNearestExcluding(
                    inc.getLatitude(), inc.getLongitude(), inc.getCenterId()
            );

            if (nearbyCenters.isEmpty()) return;

            Center backupCenter = nearbyCenters.get(0);

            inc.setBackupTriggered(true);
            inc.setBackupRoles(String.join(",", missingRoles));
            inc.setBackupCenterId(backupCenter.getId());
            inc.setBackupAt(Instant.now());

            System.out.println("BACKUP TRIGGERED for incident: " + inc.getCode());
            System.out.println("Missing roles: " + missingRoles);
            System.out.println("Backup center: " + backupCenter.getName());

        } catch (Exception e) {
            System.out.println("Backup error: " + e.getMessage());
        }
    }
}