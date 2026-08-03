package lb.lers.dispatch;

import lb.lers.incident.Incident;
import lb.lers.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    // POST assign team to incident — Dispatcher only
    @PostMapping("/assign")
    @PreAuthorize("hasRole('DISPATCHER')")
    public ResponseEntity<Incident> assign(
            @RequestBody DispatchRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                dispatchService.assign(request, currentUser.getId()));
    }

    // POST employee accepts
    @PostMapping("/incidents/{id}/accept")
    public ResponseEntity<Incident> accept(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                dispatchService.accept(id, currentUser.getId()));
    }

    // POST employee declines
    @PostMapping("/incidents/{id}/decline")
    public ResponseEntity<Incident> decline(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                dispatchService.decline(id, currentUser.getId()));
    }
}