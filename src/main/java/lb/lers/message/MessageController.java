package lb.lers.message;

import lb.lers.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Citizen or Center Admin sends message
    @PostMapping
    public ResponseEntity<Message> send(
            @AuthenticationPrincipal User currentUser,
            @RequestBody Map<String, String> body) {
        UUID centerId = UUID.fromString(body.get("centerId"));
        String content = body.get("content");
        UUID receiverId = body.get("receiverId") != null ? UUID.fromString(body.get("receiverId")) : null;
        Message message = messageService.sendMessage(
                currentUser.getId(),
                currentUser.getRole(),
                centerId,
                content,
                receiverId
        );
        return ResponseEntity.ok(message);
    }

    // Citizen gets his conversation with a center
    @GetMapping("/conversation/{centerId}")
    public ResponseEntity<List<Message>> getConversation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID centerId) {
        return ResponseEntity.ok(
                messageService.getConversation(centerId, currentUser.getId())
        );
    }

    // Center Admin gets all messages for his center
    @GetMapping("/center")
    public ResponseEntity<List<Message>> getCenterMessages(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                messageService.getCenterMessages(currentUser.getCenterId())
        );
    }

    @DeleteMapping("/conversation/{citizenId}")
    public ResponseEntity<Map<String, String>> deleteConversation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID citizenId) {
        messageService.deleteConversation(currentUser.getCenterId(), citizenId);
        return ResponseEntity.ok(Map.of("message", "Conversation deleted"));
    }

    // Mark message as read
    @PatchMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markRead(@PathVariable UUID id) {
        messageService.markRead(id);
        return ResponseEntity.ok(Map.of("message", "Marked as read"));
    }
}