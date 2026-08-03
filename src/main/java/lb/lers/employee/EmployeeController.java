package lb.lers.employee;

import lb.lers.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // GET all employees of my center
    @GetMapping
    public ResponseEntity<List<java.util.Map<String, Object>>> getAll(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                employeeService.getByCenterIdWithStatus(currentUser.getCenterId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_CENTER_ADMIN')")
    public ResponseEntity<User> update(
            @PathVariable UUID id,
            @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    // GET available employees of my center
    @GetMapping("/available")
    public ResponseEntity<List<User>> getAvailable(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                employeeService.getAvailable(currentUser.getCenterId()));
    }

    // POST create employee — Center Admin only
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CENTER_ADMIN')")
    public ResponseEntity<User> create(
            @RequestBody EmployeeRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                employeeService.create(request,
                        currentUser.getCenterId(),
                        currentUser.getId()));
    }

    // PATCH update duty status
    @PatchMapping("/{id}/duty")
    public ResponseEntity<Void> updateDuty(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        employeeService.updateDutyStatus(id, body.get("dutyStatus"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<java.util.Map<String, Object>>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    // DELETE employee — Center Admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_CENTER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}