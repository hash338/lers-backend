package lb.lers.center;

import lb.lers.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/centers")
public class CenterController {

    private final CenterService centerService;

    public CenterController(CenterService centerService) {
        this.centerService = centerService;
    }

    // GET all centers — any authenticated user
    @GetMapping
    public ResponseEntity<List<Center>> getAll() {
        return ResponseEntity.ok(centerService.getAll());
    }

    // GET active centers for map — any authenticated user
    @GetMapping("/active")
    public ResponseEntity<List<Center>> getActive() {
        return ResponseEntity.ok(centerService.getActive());
    }

    // GET one center
    @GetMapping("/{id}")
    public ResponseEntity<Center> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(centerService.getById(id));
    }

    // POST create center — Super Admin only
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Center> create(
            @RequestBody CenterRequest request) {
        return ResponseEntity.ok(centerService.create(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteCenter(@PathVariable UUID id) {
        centerService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Center deleted"));
    }

    // PUT update center — Super Admin only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Center> update(
            @PathVariable UUID id,
            @RequestBody CenterRequest request) {
        return ResponseEntity.ok(centerService.update(id, request));
    }
}