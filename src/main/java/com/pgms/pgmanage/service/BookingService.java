package com.pgms.pgmanage.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pgms.pgmanage.entity.Booking;
import com.pgms.pgmanage.entity.Tenant;
import com.pgms.pgmanage.entity.Room;
import com.pgms.pgmanage.repository.BookingRepository;
import com.pgms.pgmanage.repository.TenantRepository;
import com.pgms.pgmanage.repository.RoomRepository;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private RoomRepository roomRepository;

    /**
     * ✅ Check if the tenant has an active booking.
     */
    public boolean hasActiveBooking(Long tenantId) {
        return bookingRepository.existsByTenantIdAndRequestStatusIn(
            tenantId, List.of("PENDING", "APPROVED")
        );
    }

    /**
     * ✅ Get approved booking for a tenant (if exists).
     */
    public Booking getApprovedBooking(Long tenantId) {
        return bookingRepository.findByTenantIdAndRequestStatus(tenantId, "APPROVED");
    }

    /**
     * ✅ Create a new booking request for a tenant.
     */
    @Transactional
    public String requestBooking(Long tenantId, int roomNo, String checkinDate, String checkoutDate) {
        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (tenantOptional.isEmpty()) {
            return "Tenant not found!";
        }
        Tenant tenant = tenantOptional.get();

        // ✅ Check if the tenant has an existing booking
        Booking existingBooking = bookingRepository.findByTenantId(tenantId);
        if (existingBooking != null) {
            if ("APPROVED".equals(existingBooking.getRequestStatus())) {
                return "You already have an active booking. You can’t request a new room.";
            } else if ("REJECTED".equals(existingBooking.getRequestStatus())) {
                bookingRepository.delete(existingBooking); // ✅ Remove rejected booking before creating a new one
            }
        }

        // ✅ Fetch Room
        Room room = roomRepository.findById(roomNo).orElse(null);
        if (room == null || room.isStatus()) {
            return "Selected room is not available.";
        }

        // ✅ Create and save new booking request (PENDING)
        Booking booking = new Booking();
        booking.setTenant(tenant);
        booking.setRoom(room);
        booking.setCheckinDate(LocalDate.parse(checkinDate));
        booking.setCheckoutDate(LocalDate.parse(checkoutDate));
        booking.setRequestStatus("PENDING");

        bookingRepository.save(booking);
        return "Booking request sent successfully!";
    }

    /**
     * ✅ Reject a booking.
     */
    @Transactional
    public boolean rejectBooking(Long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            booking.setRequestStatus("REJECTED");
            bookingRepository.save(booking);
            return true;
        }
        return false;
    }

    /**
     * ✅ Delete a booking.
     */
    @Transactional
    public boolean deleteBooking(Long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            bookingRepository.delete(bookingOptional.get());
            return true;
        }
        return false;
    }
}
