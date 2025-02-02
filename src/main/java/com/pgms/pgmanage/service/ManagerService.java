package com.pgms.pgmanage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pgms.pgmanage.entity.Manager;
import com.pgms.pgmanage.entity.Booking;
import com.pgms.pgmanage.entity.Room;
import com.pgms.pgmanage.entity.Tenant;
import com.pgms.pgmanage.repository.ManagerRepository;
import com.pgms.pgmanage.repository.BookingRepository;
import com.pgms.pgmanage.repository.RoomRepository;
import com.pgms.pgmanage.repository.TenantRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ManagerService {
 
    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TenantRepository tenantRepository;

    // ✅ Validate Manager Credentials
    public Manager validateManager(String email, String password) {
        Manager manager = managerRepository.findByEmail(email);
        return (manager != null && manager.getPassword().equals(password)) ? manager : null;
    }

    // ✅ Fetch all rooms for management
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // ✅ Fetch all tenants
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    // ✅ Fetch pending bookings
    public List<Booking> getPendingBookings() {
        return bookingRepository.findByRequestStatus("PENDING");
    }

    // ✅ Approve booking & update room status
    public void approveBooking(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setRequestStatus("APPROVED");
            bookingRepository.save(booking);

            // Fetch and update room status
            Optional<Room> optionalRoom = roomRepository.findById(booking.getRoomNo());
            if (optionalRoom.isPresent()) {
                Room room = optionalRoom.get();
                room.setStatus(true); // Set room as occupied
                roomRepository.save(room);
            }
        }
    }

    // ✅ Reject booking request
    public void rejectBooking(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setRequestStatus("REJECTED");
            bookingRepository.save(booking);
        }
    }

    // ✅ Add a new room
    public void addRoom(int roomNo, boolean type) {
        // Ensure room does not already exist
        if (!roomRepository.existsById(roomNo)) {
            roomRepository.save(new Room(roomNo, type, false)); // New room is vacant
        }
    }

    // ✅ Remove a room
    public void removeRoom(int roomNo) {
        if (roomRepository.existsById(roomNo)) {
            roomRepository.deleteById(roomNo);
        }
    }
}
