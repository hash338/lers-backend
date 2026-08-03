package lb.lers.incident;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, UUID> {
    List<Incident> findByCenterIdOrderByReportedAtDesc(UUID centerId);
    List<Incident> findByCitizenId(UUID citizenId);
    List<Incident> findByDriverIdOrParamedicIdOrHelperId(
            UUID driverId, UUID paramedicId, UUID helperId);
    Optional<Incident> findByCode(String code);
}