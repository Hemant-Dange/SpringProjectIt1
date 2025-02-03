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

    // ✅ Fetch only PENDING bookings (So REJECTED ones disappear)
    public List<Booking> getPendingBookings() {
        return bookingRepository.findByRequestStatus("PENDING");
    }

    // ✅ Approve booking & update room status (Ensures tenant has only one booking)
    public void approveBooking(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            Tenant tenant = booking.getTenant();
            Room room = booking.getRoom();

            // 🚀 Check if tenant already has an approved booking
            if (bookingRepository.findByTenantIdAndRequestStatus(tenant.getId(), "APPROVED") != null) {
                throw new IllegalStateException("Tenant already has an approved booking!");
            }

            // ✅ Approve booking and update status
            booking.setRequestStatus("APPROVED");
            bookingRepository.save(booking);

            // ✅ Update room status to occupied
            room.setStatus(true);
            roomRepository.save(room);
        }
    }

    // ✅ Reject booking request (update status only, do NOT update room status)
    public void rejectBooking(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();

            // ✅ Mark the booking as REJECTED (Do NOT change room status)
            booking.setRequestStatus("REJECTED");
            bookingRepository.save(booking);
        }
    }

    // ✅ Delete tenant and update room status
    public void deleteTenant(Long tenantId) {
        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (tenantOptional.isPresent()) {
            Tenant tenant = tenantOptional.get();

            // ✅ Fetch the tenant's active booking safely
            Booking activeBooking = bookingRepository.findByTenantIdAndRequestStatus(tenant.getId(), "APPROVED");
            if (activeBooking != null) {
                Room room = activeBooking.getRoom();
                room.setStatus(false); // Make room vacant
                roomRepository.save(room);
            }

            // ✅ Delete tenant (cascade deletes their booking)
            tenantRepository.delete(tenant);
        }
    }

    // ✅ Add a new room
    public void addRoom(int roomNo, boolean type) {
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
