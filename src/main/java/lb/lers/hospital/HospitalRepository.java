package lb.lers.hospital;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    List<Hospital> findByType(HospitalType type);
    Optional<Hospital> findByAdminUserId(UUID adminUserId);
}