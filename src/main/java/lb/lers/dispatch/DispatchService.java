package lb.lers.dispatch;

import lb.lers.incident.Incident;
import lb.lers.incident.IncidentRepository;
import lb.lers.notification.WsBroadcaster;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
public class DispatchService {

    private final IncidentRepository incidentRepository;
    private final BackupScheduler backupScheduler;
    private final WsBroadcaster wsBroadcaster;

    @Value("${backup.timeout-seconds:45}")
    private int timeoutSeconds;

    public DispatchService(IncidentRepository incidentRepository,
                           BackupScheduler backupScheduler,
                           WsBroadcaster wsBroadcaster) {
        this.incidentRepository = incidentRepository;
        this.backupScheduler = backupScheduler;
        this.wsBroadcaster = wsBroadcaster;
    }

    public Incident assign(DispatchRequest request, UUID dispatcherId) {
        Incident inc = incidentRepository.findById(request.getIncidentId())
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        inc.setDriverId(request.getDriverId());
        inc.setParamedicId(request.getParamedicId());
        inc.setHelperId(request.getHelperId());
        inc.setVehicleId(request.getVehicleId());
        inc.setDispatcherId(dispatcherId);
        inc.setStatus("DISPATCHED");
        inc.setDispatchedAt(Instant.now());

        incidentRepository.save(inc);

        // Start backup timer
        backupScheduler.startTimer(inc.getId(), timeoutSeconds);

        // Broadcast
        wsBroadcaster.broadcastToCenter(inc.getCenterId().toString(), inc);

        return inc;
    }

    public Incident accept(UUID incidentId, UUID employeeId) {
        Incident inc = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        setResponse(inc, employeeId, "ACCEPTED");
        incidentRepository.save(inc);

        wsBroadcaster.broadcastToCenter(inc.getCenterId().toString(), inc);
        return inc;
    }

    public Incident decline(UUID incidentId, UUID employeeId) {
        Incident inc = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        setResponse(inc, employeeId, "REJECTED");
        incidentRepository.save(inc);

        wsBroadcaster.broadcastToCenter(inc.getCenterId().toString(), inc);
        return inc;
    }

    private void setResponse(Incident inc, UUID employeeId, String response) {
        if (employeeId.equals(inc.getDriverId()))
            inc.setDriverResponse(response);
        else if (employeeId.equals(inc.getParamedicId()))
            inc.setParamedicResponse(response);
        else if (employeeId.equals(inc.getHelperId()))
            inc.setHelperResponse(response);
    }
}