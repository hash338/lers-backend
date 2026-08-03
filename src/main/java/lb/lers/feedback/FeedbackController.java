package lb.lers.feedback;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final lb.lers.user.UserRepository userRepository;
    private final lb.lers.FirebaseMessagingService firebaseMessagingService;

    public FeedbackController(FeedbackRepository feedbackRepository,
                              lb.lers.user.UserRepository userRepository,
                              lb.lers.FirebaseMessagingService firebaseMessagingService) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.firebaseMessagingService = firebaseMessagingService;
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<java.util.Map<String, Object>>> getAll() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        List<java.util.Map<String, Object>> result = feedbacks.stream().map(f -> {
            java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("id", f.getId());
            map.put("message", f.getMessage());
            map.put("rating", f.getRating());
            map.put("readByAdmin", f.getReadByAdmin());
            map.put("submittedAt", f.getSubmittedAt());
            map.put("incidentId", f.getIncidentId());
            map.put("citizenId", f.getCitizenId());
            // Get citizen name
            if (f.getCitizenId() != null) {
                userRepository.findById(f.getCitizenId()).ifPresent(u -> {
                    map.put("citizenName", u.getFullName());
                    map.put("citizenPhone", u.getPhone());
                });
            }
            return map;
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> markRead(@PathVariable UUID id) {
        feedbackRepository.findById(id).ifPresent(f -> {
            f.setReadByAdmin(true);
            feedbackRepository.save(f);
            // Send FCM notification to citizen
            if (f.getCitizenId() != null) {
                userRepository.findById(f.getCitizenId()).ifPresent(citizen ->
                        firebaseMessagingService.sendNotification(
                                citizen.getFcmToken(),
                                "Feedback Received",
                                "Thank you for your feedback! - LERS Super Admin"
                        )
                );
            }
        });
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Feedback> create(
            @RequestBody java.util.Map<String, String> body,
            @AuthenticationPrincipal lb.lers.user.User currentUser) {
        Feedback feedback = new Feedback();
        feedback.setMessage(body.get("message"));
        feedback.setReadByAdmin(false);
        if (currentUser != null) {
            feedback.setCitizenId(currentUser.getId());
        }
        feedbackRepository.save(feedback);
        return ResponseEntity.ok(feedback);
    }
}