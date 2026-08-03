package lb.lers.center;

import lb.lers.user.User;
import lb.lers.user.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class CenterService {

    private final CenterRepository centerRepository;
    private final UserRepository userRepository;

    public CenterService(CenterRepository centerRepository,
                         UserRepository userRepository) {
        this.centerRepository = centerRepository;
        this.userRepository = userRepository;
    }

    // Get all centers
    public List<Center> getAll() {
        return centerRepository.findAll();
    }

    // Get active centers only (for map)
    public List<Center> getActive() {
        return centerRepository.findByStatus("active");
    }

    // Get one center by ID
    public Center getById(UUID id) {
        return centerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Center not found"));
    }

    // Create center (Super Admin only)
    public Center create(CenterRequest request) {
        if (centerRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Center code already exists");
        }

        Center center = Center.builder()
                .code(request.getCode())
                .name(request.getName())
                .type(request.getType())
                .region(request.getRegion())
                .address(request.getAddress())
                .phone(request.getPhone())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status("active")
                .build();

        return centerRepository.save(center);
    }

    // Update center
    public Center update(UUID id, CenterRequest request) {
        Center center = getById(id);
        if (request.getName() != null) center.setName(request.getName());
        if (request.getType() != null) center.setType(request.getType());
        if (request.getRegion() != null) center.setRegion(request.getRegion());
        if (request.getAddress() != null) center.setAddress(request.getAddress());
        if (request.getPhone() != null) center.setPhone(request.getPhone());
        if (request.getLatitude() != null) center.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) center.setLongitude(request.getLongitude());
        if (request.getStatus() != null) center.setStatus(request.getStatus());
        return centerRepository.save(center);
    }

    // Assign admin to center
    public Center assignAdmin(UUID centerId, UUID adminId) {
        Center center = getById(centerId);
        center.setAdminId(adminId);
        return centerRepository.save(center);
    }

    // Delete center
    public void delete(UUID id) {
        centerRepository.deleteById(id);
    }
}