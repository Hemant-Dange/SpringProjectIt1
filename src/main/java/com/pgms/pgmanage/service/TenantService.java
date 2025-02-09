package com.pgms.pgmanage.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pgms.pgmanage.dto.TenantDto;
import com.pgms.pgmanage.entity.Booking;
import com.pgms.pgmanage.entity.Room;
import com.pgms.pgmanage.entity.Tenant;
import com.pgms.pgmanage.repository.BookingRepository;
import com.pgms.pgmanage.repository.RoomRepository;
import com.pgms.pgmanage.repository.TenantRepository;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepo;

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private RoomRepository roomRepository;

    // ✅ Get Tenant by Email
    public Tenant getTenantByEmail(String email) {
        return tenantRepo.findByTMail(email);
    }

    // ✅ Get List of Available Rooms
    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus(false); // false = vacant
    }

    // ✅ Get Allocated Room Number (if Booking is Approved)
    public Integer getAllocatedRoomNumber(Long tenantId) {
        Booking approvedBooking = bookingRepository.findByTenantIdAndRequestStatus(tenantId, "APPROVED");
        return (approvedBooking != null) ? approvedBooking.getRoom().getRoomNo() : null;
    }

    // ✅ Tenant Registration (With Uniqueness Checks)
    public String registerTenant(TenantDto tenantDto) {
        if (tenantRepo.existsByTMail(tenantDto.gettMail())) {
            return "Email is already registered!";
        }

        if (tenantRepo.existsByUsername(tenantDto.getUsername())) {
            return "Username is already taken!";
        }

        if (tenantRepo.existsByPhNumber(tenantDto.getPhNumber())) {
            return "Phone number is already registered!";
        }

        if (!tenantDto.getPassword().equals(tenantDto.getConPassword())) {
            return "Passwords do not match!";
        }

        Tenant tenant = new Tenant();
        tenant.setUsername(tenantDto.getUsername());
        tenant.settMail(tenantDto.gettMail());
        tenant.setPhNumber(tenantDto.getPhNumber());
        tenant.setPassword(tenantDto.getPassword());

        tenantRepo.save(tenant);
        return "Registration Successful!";
    }

    // ✅ Tenant Login (Simple Email & Password Comparison)
    public TenantDto authenticateTenant(String email, String password) {
        Tenant tenant = tenantRepo.findByTMail(email);

        if (tenant != null && tenant.getPassword().equals(password)) {
            TenantDto tenantDto = new TenantDto();
            tenantDto.setUsername(tenant.getUsername());
            tenantDto.settMail(tenant.gettMail());
            tenantDto.setPhNumber(tenant.getPhNumber());
            return tenantDto;
        }
        return null;
    }

    // ✅ Prevent Multiple Bookings Per User - Can the tenant book a room?
    public boolean canBookRoom(Long tenantId) {
        Optional<Tenant> tenant = tenantRepo.findById(tenantId);
        if (tenant.isPresent()) {
            Booking existingBooking = bookingRepository.findByTenantIdAndRequestStatus(tenantId, "APPROVED");
            return existingBooking == null;
        }
        return false;
    }

    // ✅ Request Booking (Moved from Controller)
    public String requestBooking(String tenantEmail, int roomNo, String checkinDate, String checkoutDate) {
        Tenant tenant = tenantRepo.findByTMail(tenantEmail);
        if (tenant == null) {
            return "Tenant not found!";
        }

        if (hasActiveBooking(tenant.getId())) {
            return "You already have an active booking (Pending/Approved).";
        }

        Room room = roomRepository.findById(roomNo).orElse(null);
        if (room == null || room.isStatus()) {
            return "Selected room is not available.";
        }

        Booking booking = new Booking();
        booking.setTenant(tenant);
        booking.setRoom(room);
        booking.setCheckinDate(LocalDate.parse(checkinDate));
        booking.setCheckoutDate(LocalDate.parse(checkoutDate));
        booking.setRequestStatus("PENDING");

        bookingRepository.save(booking);
        return "Booking request sent successfully!";
    }

    // ✅ Reject Booking Request
    public void rejectBooking(Long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        bookingOptional.ifPresent(booking -> {
            booking.setRequestStatus("REJECTED");
            bookingRepository.save(booking);
        });
    }

    // ✅ Check if Tenant has Active Booking
    public boolean hasActiveBooking(Long tenantId) {
        return bookingRepository.existsByTenantIdAndRequestStatusIn(
            tenantId, List.of("PENDING", "APPROVED")
        );
    }
}
