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

    // ✅ Tenant Registration (With Uniqueness Checks)
    public String registerTenant(TenantDto tenantDto) {
        // ✅ Check if email already exists
        if (tenantRepo.existsByTMail(tenantDto.gettMail())) {
            return "Email is already registered!";
        }

        // ✅ Check if username already exists
        if (tenantRepo.existsByUsername(tenantDto.getUsername())) {
            return "Username is already taken!";
        }

        // ✅ Check if phone number already exists
        if (tenantRepo.existsByPhNumber(tenantDto.getPhNumber())) {
            return "Phone number is already registered!";
        }

        // ✅ Check if passwords match
        if (!tenantDto.getPassword().equals(tenantDto.getConPassword())) {
            return "Passwords do not match!";
        }

        // ✅ Create Tenant entity (Ensure `conPassword` is not used)
        Tenant tenant = new Tenant();
        tenant.setUsername(tenantDto.getUsername());
        tenant.settMail(tenantDto.gettMail());
        tenant.setPhNumber(tenantDto.getPhNumber());
        tenant.setPassword(tenantDto.getPassword()); // ✅ Only saving password

        // ✅ Save tenant without validating `conPassword`
        tenantRepo.save(tenant);

        return "Registration Successful!";
    }

    // ✅ Tenant Login (Simple Email & Password Comparison)
    public TenantDto authenticateTenant(String email, String password) {
        Tenant tenant = tenantRepo.findByTMail(email);

        if (tenant != null && tenant.getPassword().equals(password)) {
            // ✅ Return only necessary fields
            TenantDto tenantDto = new TenantDto();
            tenantDto.setUsername(tenant.getUsername());
            tenantDto.settMail(tenant.gettMail());
            tenantDto.setPhNumber(tenant.getPhNumber());
            return tenantDto;
        }

        return null; // Authentication failed
    }

    // ✅ Prevent Multiple Bookings Per User - Can the tenant book a room?
    public boolean canBookRoom(Long tenantId) {
        Optional<Tenant> tenant = tenantRepo.findById(tenantId);
        if (tenant.isPresent()) {
            // ✅ Check if tenant already has an approved booking
            Booking existingBooking = bookingRepository.findByTenantIdAndRequestStatus(tenantId, "APPROVED");
            return existingBooking == null; // Can book only if no approved booking exists
        }
        return false;
    }

    // ✅ Logic to handle booking request from tenant
 // ✅ Logic to handle booking request from tenant
    public String requestBooking(Long tenantId, int roomNo, String checkinDate, String checkoutDate) {
        Optional<Tenant> tenantOptional = tenantRepo.findById(tenantId);
        if (tenantOptional.isEmpty()) {
            throw new IllegalStateException("Tenant not found!");
        }

        Tenant tenant = tenantOptional.get();

        // ✅ Check if tenant has any existing booking
        Booking existingBooking = bookingRepository.findByTenantId(tenant.getId());

        if (existingBooking != null) {
            if ("APPROVED".equals(existingBooking.getRequestStatus())) {
                return "You already have an active booking. You can’t request a new room until your current booking is resolved.";
            } else if ("REJECTED".equals(existingBooking.getRequestStatus())) {
                // ❌ Remove the rejected booking before creating a new one
                bookingRepository.delete(existingBooking);
            }
        }

        // ✅ Fetch the room using roomNo
        Room room = roomRepository.findById(roomNo).orElse(null);
        if (room == null || room.isStatus()) {
            return "Selected room is not available.";
        }

        // ✅ Create and save the new booking request (PENDING)
        Booking booking = new Booking();
        booking.setTenant(tenant);
        booking.setRoom(room);
        booking.setCheckinDate(LocalDate.parse(checkinDate));
        booking.setCheckoutDate(LocalDate.parse(checkoutDate));
        booking.setRequestStatus("PENDING");

        bookingRepository.save(booking);

        return "Booking request sent successfully!";
    }
    
    public boolean hasActiveBooking(Long tenantId) {
        return bookingRepository.existsByTenantIdAndRequestStatusIn(
            tenantId, List.of("PENDING", "APPROVED")
        );
    }

    
}
