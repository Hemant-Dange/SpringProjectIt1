package com.pgms.pgmanage.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pgms.pgmanage.entity.Booking;
import com.pgms.pgmanage.entity.Manager;
import com.pgms.pgmanage.entity.Room;
import com.pgms.pgmanage.entity.Tenant;
import com.pgms.pgmanage.repository.BookingRepository;
import com.pgms.pgmanage.repository.ManagerRepository;
import com.pgms.pgmanage.repository.RoomRepository;
import com.pgms.pgmanage.repository.TenantRepository;

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

    // ✅ Delete tenant and update room status (make room available again)
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
        // Check if the room number is negative
        if (roomNo < 1) {
            throw new IllegalArgumentException("Room number must be a positive number!");
        }

        // Check if the room already exists
        if (roomRepository.existsById(roomNo)) {
            throw new IllegalStateException("Room number " + roomNo + " already exists!");
        }

        // Create and save the new room if valid
        roomRepository.save(new Room(roomNo, type, false));  // Default status is Vacant
    }

    // ✅ Remove a room and associated bookings in one transaction
    @Transactional
    public void removeRoom(int roomNo) {
        Room room = roomRepository.findById(roomNo).orElse(null);
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }

        // ✅ Step 1: Check if room has any "APPROVED" bookings (i.e., occupied)
        boolean isOccupied = bookingRepository.existsByRoomAndRequestStatus(room, "APPROVED");
        if (isOccupied) {
            throw new IllegalStateException("Cannot delete room as it is occupied by a tenant.");
        }

        // ✅ Step 2: Find and delete all bookings for the room
        List<Booking> bookings = bookingRepository.findByRoom(room);
        bookingRepository.deleteAll(bookings);

        // ✅ Step 3: Delete the room
        roomRepository.delete(room);
    }

    // ✅ Logic to handle tenant booking (Check if already booked)
    public String bookRoom(Long tenantId, int roomNo, String checkinDate, String checkoutDate) {
        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (tenantOptional.isEmpty()) {
            throw new IllegalStateException("Tenant not found!");
        }

        Tenant tenant = tenantOptional.get();

        // ✅ Check if the tenant already has an approved booking
        Booking existingBooking = bookingRepository.findByTenantIdAndRequestStatus(tenant.getId(), "APPROVED");
        if (existingBooking != null) {
            return "You are already occupying a room. You can't request another booking until your current one is resolved.";
        }

        // ✅ Proceed with booking logic
        Room room = roomRepository.findById(roomNo).orElse(null);
        if (room == null || room.isStatus()) {
            return "Selected room is not available.";
        }

        // Create new booking request
        Booking booking = new Booking();
        booking.setTenant(tenant);
        booking.setRoom(room);
        booking.setCheckinDate(LocalDate.parse(checkinDate));
        booking.setCheckoutDate(LocalDate.parse(checkoutDate));
        booking.setRequestStatus("PENDING");

        bookingRepository.save(booking);

        return "Booking request sent successfully!";
    }
}
