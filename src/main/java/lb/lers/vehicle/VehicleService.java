package lb.lers.vehicle;

import lb.lers.user.User;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getAll() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> getByCenterId(UUID centerId) {
        return vehicleRepository.findByCenterId(centerId);
    }

    public List<Vehicle> getAvailable(UUID centerId) {
        return vehicleRepository.findByCenterIdAndStatus(centerId, "available");
    }

    public Vehicle getById(UUID id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public Vehicle create(VehicleRequest request, UUID centerId, UUID createdBy) {
        if (vehicleRepository.existsByCode(request.getCode()))
            throw new RuntimeException("Vehicle code already exists");
        if (vehicleRepository.existsByPlate(request.getPlate()))
            throw new RuntimeException("Plate already exists");

        Vehicle v = Vehicle.builder()
                .code(request.getCode())
                .plate(request.getPlate())
                .type(request.getType())
                .model(request.getModel())
                .centerId(centerId)
                .status("available")
                .createdBy(createdBy)
                .build();

        return vehicleRepository.save(v);
    }

    public Vehicle update(UUID id, VehicleRequest request) {
        Vehicle v = getById(id);
        if (request.getType() != null) v.setType(request.getType());
        if (request.getModel() != null) v.setModel(request.getModel());
        if (request.getStatus() != null) v.setStatus(request.getStatus());
        if (request.getMileage() != null) v.setMileage(request.getMileage());
        return vehicleRepository.save(v);
    }

    public void setStatus(UUID id, String status) {
        Vehicle v = getById(id);
        v.setStatus(status);
        vehicleRepository.save(v);
    }

    public void delete(UUID id) {
        vehicleRepository.deleteById(id);
    }
}