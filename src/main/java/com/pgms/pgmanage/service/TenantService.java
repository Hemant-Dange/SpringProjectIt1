package com.pgms.pgmanage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pgms.pgmanage.dto.TenantDto;
import com.pgms.pgmanage.entity.Booking;
import com.pgms.pgmanage.entity.Tenant;
import com.pgms.pgmanage.repository.TenantRepository;
import com.pgms.pgmanage.repository.BookingRepository;

import java.util.Optional;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepo;

    @Autowired
    private BookingRepository bookingRepository;

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

    // ✅ Prevent Multiple Bookings Per User
    public boolean canBookRoom(Long tenantId) {
        Optional<Tenant> tenant = tenantRepo.findById(tenantId);
        if (tenant.isPresent()) {
            Booking existingBooking = bookingRepository.findByTenantId(tenantId);
            return existingBooking == null; // Can book only if no existing booking
        }
        return false;
    }
}
