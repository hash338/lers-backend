package lb.lers.hospital;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof lb.lers.user.User user) {
            return user.getId();
        }
        return UUID.fromString(auth.getName());
    }

    @PostMapping
    public ResponseEntity<Hospital> create(@RequestBody HospitalRequest req) {
        return ResponseEntity.ok(hospitalService.createHospital(req));
    }

    @GetMapping
    public ResponseEntity<List<Hospital>> getAll(@RequestParam(required = false) HospitalType type) {
        if (type != null) return ResponseEntity.ok(hospitalService.getHospitalsByType(type));
        return ResponseEntity.ok(hospitalService.getAllHospitals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hospital> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getHospital(id));
    }

    @GetMapping("/my")
    public ResponseEntity<Hospital> getMyHospital() {
        return ResponseEntity.ok(hospitalService.getHospitalByAdminId(getCurrentUserId()));
    }

    @PostMapping("/{id}/floors")
    public ResponseEntity<Floor> addFloor(@PathVariable Long id, @RequestParam int floorNumber) {
        return ResponseEntity.ok(hospitalService.addFloor(id, floorNumber));
    }

    @GetMapping("/{id}/floors")
    public ResponseEntity<List<Floor>> getFloors(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getFloors(id));
    }

    @PostMapping("/floors/{floorId}/rooms")
    public ResponseEntity<Room> addRoom(@PathVariable Long floorId, @RequestBody RoomRequest req) {
        return ResponseEntity.ok(hospitalService.addRoom(floorId, req));
    }

    @GetMapping("/{id}/rooms/available")
    public ResponseEntity<List<Room>> getAvailableRooms(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getRoomsByHospital(id));
    }

    @PostMapping("/rooms/{roomId}/book/dispatcher")
    public ResponseEntity<RoomBooking> dispatcherBook(
            @PathVariable Long roomId,
            @RequestParam(required = false) UUID incidentId,
            @RequestParam UUID driverUserId) {
        return ResponseEntity.ok(hospitalService.dispatcherBook(roomId, incidentId, driverUserId));
    }

    @PostMapping("/rooms/{roomId}/book")
    public ResponseEntity<RoomBooking> citizenBook(
            @PathVariable Long roomId,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(hospitalService.citizenBook(roomId, getCurrentUserId(), notes));
    }

    @PatchMapping("/bookings/{bookingId}/accept")
    public ResponseEntity<RoomBooking> accept(@PathVariable Long bookingId) {
        return ResponseEntity.ok(hospitalService.acceptBooking(bookingId));
    }

    @PatchMapping("/bookings/{bookingId}/deny")
    public ResponseEntity<RoomBooking> deny(@PathVariable Long bookingId) {
        return ResponseEntity.ok(hospitalService.denyBooking(bookingId));
    }

    @PatchMapping("/rooms/{roomId}/release")
    public ResponseEntity<Void> release(@PathVariable Long roomId) {
        hospitalService.releaseRoom(roomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bookings/my")
    public ResponseEntity<List<RoomBooking>> myBookings() {
        return ResponseEntity.ok(hospitalService.getMyBookings(getCurrentUserId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hospitalService.deleteHospital(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<RoomBooking>> hospitalBookings(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getHospitalBookings(id));
    }
}