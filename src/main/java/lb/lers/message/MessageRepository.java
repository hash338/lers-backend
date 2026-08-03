package lb.lers.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByCenterIdOrderByCreatedAtAsc(UUID centerId);
    List<Message> findBySenderIdOrReceiverIdOrderByCreatedAtAsc(UUID senderId, UUID receiverId);
    @Query("SELECT m FROM Message m WHERE m.centerId = :centerId AND (m.senderId = :citizenId OR m.receiverId = :citizenId) ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("centerId") UUID centerId, @Param("citizenId") UUID citizenId);
}