package lb.lers.employee;

import lb.lers.user.User;
import lb.lers.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final UserRepository userRepository;
    private final EmployeeStatusRepository statusRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(UserRepository userRepository,
                           EmployeeStatusRepository statusRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.statusRepository = statusRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<java.util.Map<String, Object>> getByCenterIdWithStatus(UUID centerId) {
        List<User> users = userRepository.findAll().stream()
                .filter(u -> centerId.equals(u.getCenterId()))
                .filter(u -> u.getRole().equals("DRIVER")
                        || u.getRole().equals("PARAMEDIC")
                        || u.getRole().equals("PARAMEDIC_HELPER")
                        || u.getRole().equals("DISPATCHER"))
                .collect(Collectors.toList());

        return users.stream().map(u -> {
            java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("id", u.getId());
            map.put("fullName", u.getFullName());
            map.put("email", u.getEmail());
            map.put("phone", u.getPhone());
            map.put("role", u.getRole());
            map.put("centerId", u.getCenterId());
            // get duty status from employee_status table
            String duty = statusRepository.findById(u.getId())
                    .map(EmployeeStatus::getDutyStatus)
                    .orElse("OFF_DUTY");
            map.put("dutyStatus", duty.toUpperCase().replace("-","_"));
            return map;
        }).collect(Collectors.toList());
    }

    // Get all employees of a center
    public List<User> getByCenterId(UUID centerId) {
        return userRepository.findAll().stream()
                .filter(u -> centerId.equals(u.getCenterId()))
                .filter(u -> u.getRole().equals("DRIVER")
                        || u.getRole().equals("PARAMEDIC")
                        || u.getRole().equals("PARAMEDIC_HELPER"))
                .collect(Collectors.toList());
    }

    // Get available (on-duty) employees of a center by role
    public List<User> getAvailable(UUID centerId) {
        return getByCenterId(centerId).stream()
                .filter(u -> {
                    EmployeeStatus s = statusRepository.findById(u.getId()).orElse(null);
                    return s != null && "on-duty".equals(s.getDutyStatus());
                })
                .collect(Collectors.toList());
    }

    // Create employee (Center Admin only)
    public User create(EmployeeRequest request, UUID centerId, UUID createdBy) {
        if (request.getEmail() != null
                && userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already exists");

        if (request.getPhone() != null
                && userRepository.existsByPhone(request.getPhone()))
            throw new RuntimeException("Phone already exists");

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(
                        request.getPassword() != null ? request.getPassword() : "lers2024"))
                .role(request.getRole())
                .centerId(centerId)
                .status("active")
                .createdBy(createdBy)
                .build();

        userRepository.save(user);

        // Create employee status
        EmployeeStatus status = new EmployeeStatus();
        status.setUserId(user.getId());
        status.setDutyStatus("off-duty");
        status.setShift(request.getShift());
        status.setUpdatedAt(Instant.now());
        statusRepository.save(status);

        return user;
    }

    // Update duty status
    public void updateDutyStatus(UUID userId, String dutyStatus) {
        EmployeeStatus status = statusRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employee status not found"));
        status.setDutyStatus(dutyStatus.toUpperCase().replace("-","_"));
        status.setUpdatedAt(Instant.now());
        statusRepository.save(status);
    }

    public User update(UUID id, EmployeeRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getShift() == null && request.getFullName() != null) {
            // duty status comes from EmployeeRequest — need to add field
        }
        if (request.getDutyStatus() != null) {
            updateDutyStatus(id, request.getDutyStatus());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return userRepository.save(user);
    }

    public List<java.util.Map<String, Object>> getAll() {
        List<User> users = userRepository.findByRoleIn(
                java.util.Arrays.asList("DRIVER", "PARAMEDIC", "PARAMEDIC_HELPER", "DISPATCHER")
        );

        users.forEach(u -> {
            var s = statusRepository.findById(u.getId());
            System.out.println("User: " + u.getFullName() + " | status in DB: " + s.map(EmployeeStatus::getDutyStatus).orElse("NOT FOUND"));
        });

        return users.stream().map(u -> {
            java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("id", u.getId());
            map.put("fullName", u.getFullName());
            map.put("email", u.getEmail());
            map.put("phone", u.getPhone());
            map.put("role", u.getRole());
            map.put("centerId", u.getCenterId());
            String duty = statusRepository.findById(u.getId())
                    .map(EmployeeStatus::getDutyStatus)
                    .orElse("OFF_DUTY");
            map.put("dutyStatus", duty.toUpperCase().replace("-","_"));
            return map;
        }).collect(Collectors.toList());
    }

    // Delete employee
    public void delete(UUID id) {
        statusRepository.deleteById(id);
        userRepository.deleteById(id);
    }
}