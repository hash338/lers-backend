package lb.lers.hospital;

import lb.lers.FirebaseMessagingService;
import lb.lers.user.User;
import lb.lers.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final RoomBookingRepository bookingRepository;
    private final FirebaseMessagingService firebaseMessagingService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final lb.lers.incident.IncidentRepository incidentRepository;

    // ── CREATE HOSPITAL + ADMIN ACCOUNT ──
    public Hospital createHospital(HospitalRequest req) {
        // Create hospital
        Hospital h = new Hospital();
        h.setName(req.getName());
        h.setRegion(req.getRegion());
        h.setPhone(req.getPhone());
        h.setAddress(req.getAddress());
        h.setLatitude(req.getLatitude());
        h.setLongitude(req.getLongitude());
        h.setType(req.getType());
        h.setAdminEmail(req.getAdminEmail());
        hospitalRepository.save(h);

        // Create Hospital Admin account
        if (req.getAdminEmail() != null && req.getAdminPassword() != null) {
            User admin = new User();
            admin.setEmail(req.getAdminEmail());
            admin.setFullName(req.getName() + " Admin");
            admin.setPassword(passwordEncoder.encode(req.getAdminPassword()));
            admin.setRole("HOSPITAL_ADMIN");
            admin.setHospitalId(h.getId());
            admin.setStatus("active");
            User savedAdmin = userRepository.save(admin);
            h.setAdminUserId(savedAdmin.getId());
            hospitalRepository.save(h);
        }

        return h;
    }

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    public List<Hospital> getHospitalsByType(HospitalType type) {
        return hospitalRepository.findByType(type);
    }

    public Hospital getHospital(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
    }

    public Hospital getHospitalByAdminId(UUID adminId) {
        return hospitalRepository.findByAdminUserId(adminId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
    }

    // ── FLOORS ──
    public Floor addFloor(Long hospitalId, int floorNumber) {
        Hospital hospital = getHospital(hospitalId);
        Floor floor = new Floor();
        floor.setFloorNumber(floorNumber);
        floor.setHospital(hospital);
        return floorRepository.save(floor);
    }

    public List<Floor> getFloors(Long hospitalId) {
        return floorRepository.findByHospitalId(hospitalId);
    }

    // ── ROOMS ──
    public Room addRoom(Long floorId, RoomRequest req) {
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new RuntimeException("Floor not found"));
        Room room = new Room();
        room.setRoomNumber(req.getRoomNumber());
        room.setType(req.getType());
        room.setStatus(RoomStatus.AVAILABLE);
        room.setFloor(floor);
        return roomRepository.save(room);
    }

    public List<Room> getRoomsByHospital(Long hospitalId) {
        return roomRepository.findByFloorHospitalIdAndStatus(hospitalId, RoomStatus.AVAILABLE);
    }

    // ── DISPATCHER BOOKS ROOM ──
    public RoomBooking dispatcherBook(Long roomId, UUID incidentId, UUID driverUserId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        // Update incident with hospital info
        if (incidentId != null) {
            incidentRepository.findById(incidentId).ifPresent(inc -> {
                inc.setHospitalName(room.getFloor().getHospital().getName());
                inc.setHospitalFloor("Floor " + room.getFloor().getFloorNumber());
                inc.setHospitalRoom("Room " + room.getRoomNumber());
                inc.setHospitalLatitude(room.getFloor().getHospital().getLatitude() != null ?
                        java.math.BigDecimal.valueOf(room.getFloor().getHospital().getLatitude()) : null);
                inc.setHospitalLongitude(room.getFloor().getHospital().getLongitude() != null ?
                        java.math.BigDecimal.valueOf(room.getFloor().getHospital().getLongitude()) : null);
                inc.setHospitalAddress(
                        room.getFloor().getHospital().getAddress() != null
                                ? room.getFloor().getHospital().getAddress()
                                : room.getFloor().getHospital().getRegion()
                );
                incidentRepository.save(inc);
            });
        }

        RoomBooking booking = new RoomBooking();
        booking.setRoom(room);
        booking.setBookedBy("DISPATCHER");
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // Notify driver
        User driver = userRepository.findById(driverUserId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        if (driver.getFcmToken() != null) {
            firebaseMessagingService.sendNotification(
                    driver.getFcmToken(),
                    "Hospital Assigned 🏥",
                    "Go to: " + room.getFloor().getHospital().getName()
                            + " | Floor " + room.getFloor().getFloorNumber()
                            + " | Room " + room.getRoomNumber()
                            + " | " + (room.getFloor().getHospital().getAddress() != null
                            ? room.getFloor().getHospital().getAddress()
                            : room.getFloor().getHospital().getRegion())
            );
        }
        return booking;
    }

    // ── CITIZEN BOOKS ROOM ──
    public RoomBooking citizenBook(Long roomId, UUID citizenId, String notes) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new RuntimeException("Room not available");
        }



        User citizen = userRepository.findById(citizenId)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));

        RoomBooking booking = new RoomBooking();
        booking.setRoom(room);
        booking.setCitizen(citizen);
        booking.setBookedBy("CITIZEN");
        booking.setStatus(BookingStatus.PENDING);
        booking.setNotes(notes);
        return bookingRepository.save(booking);
    }

    // ── ACCEPT CITIZEN BOOKING ──
    public RoomBooking acceptBooking(Long bookingId) {
        RoomBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        if (booking.getCitizen() != null && booking.getCitizen().getFcmToken() != null) {
            firebaseMessagingService.sendNotification(
                    booking.getCitizen().getFcmToken(),
                    "Booking Confirmed! 🏥",
                    "Your room " + room.getRoomNumber() + " at "
                            + room.getFloor().getHospital().getName() + " is confirmed."
            );
        }
        return booking;
    }

    // ── DENY CITIZEN BOOKING ──
    public RoomBooking denyBooking(Long bookingId) {
        RoomBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.DENIED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        if (booking.getCitizen() != null && booking.getCitizen().getFcmToken() != null) {
            firebaseMessagingService.sendNotification(
                    booking.getCitizen().getFcmToken(),
                    "Booking Denied",
                    "Your booking at " + room.getFloor().getHospital().getName() + " was denied."
            );
        }
        return booking;
    }

    // ── RELEASE ROOM (patient discharged) ──
    public void releaseRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        bookingRepository.findByRoomFloorHospitalId(
                        room.getFloor().getHospital().getId()
                ).stream()
                .filter(b -> b.getRoom().getId().equals(roomId)
                        && b.getStatus() == BookingStatus.CONFIRMED)
                .findFirst()
                .ifPresent(b -> {
                    b.setStatus(BookingStatus.DISCHARGED);
                    b.setUpdatedAt(LocalDateTime.now());
                    bookingRepository.save(b);

                    if (b.getCitizen() != null && b.getCitizen().getFcmToken() != null) {
                        firebaseMessagingService.sendNotification(
                                b.getCitizen().getFcmToken(),
                                "Patient Discharged",
                                "Your patient has been discharged from "
                                        + room.getFloor().getHospital().getName() + "."
                        );
                    }
                });
    }

    // ── MY BOOKINGS (citizen) ──
    public List<RoomBooking> getMyBookings(UUID citizenId) {
        return bookingRepository.findByCitizenId(citizenId);
    }

    public void deleteHospital(Long id) {
        Hospital h = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        List<Floor> floors = floorRepository.findByHospitalId(id);
        if (!floors.isEmpty()) {
            long roomCount = floors.stream()
                    .mapToLong(f -> f.getRooms() != null ? f.getRooms().size() : 0)
                    .sum();
            if (roomCount > 0) {
                throw new RuntimeException("Cannot delete hospital with existing rooms");
            }
        }

        UUID adminUserId = h.getAdminUserId();

        // 1. Null out admin_user_id on hospital
        h.setAdminUserId(null);
        hospitalRepository.save(h);

        // 2. Null out hospital_id on the admin user
        if (adminUserId != null) {
            userRepository.findById(adminUserId).ifPresent(u -> {
                u.setHospitalId(null);
                userRepository.save(u);
            });
        }

        // 3. Delete hospital
        hospitalRepository.deleteById(id);

        // 4. Delete admin user
        if (adminUserId != null) {
            userRepository.deleteById(adminUserId);
        }
    }

    // ── HOSPITAL BOOKINGS ──
    public List<RoomBooking> getHospitalBookings(Long hospitalId) {
        return bookingRepository.findByRoomFloorHospitalId(hospitalId);
    }
}