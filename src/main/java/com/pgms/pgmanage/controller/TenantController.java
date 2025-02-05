package com.pgms.pgmanage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pgms.pgmanage.entity.Booking;
import com.pgms.pgmanage.entity.Room;
import com.pgms.pgmanage.entity.Tenant;
import com.pgms.pgmanage.repository.BookingRepository;
import com.pgms.pgmanage.repository.RoomRepository;
import com.pgms.pgmanage.repository.TenantRepository;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tenant")
public class TenantController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TenantRepository tenantRepository;

    // ✅ Display Tenant Dashboard with Available Rooms
    @GetMapping("/dashboard")
    public String tenantDashboard(HttpSession session, Model model) {
        if (session.getAttribute("loggedInTenant") == null) {
            return "redirect:/tenantlogin"; // Redirect if not logged in
        }

        // ✅ Get Tenant Email from Session
        String tenantEmail = (String) session.getAttribute("loggedInTenant");
        Tenant tenant = tenantRepository.findByTMail(tenantEmail);
        if (tenant == null) {
            return "redirect:/tenantlogin"; // Redirect if tenant not found
        }

        model.addAttribute("tenantUsername", tenant.getUsername()); // Pass username to frontend

        // ✅ Fetch Available Rooms (Vacant Only)
        List<Room> availableRooms = roomRepository.findByStatus(false); // false = vacant
        model.addAttribute("rooms", availableRooms);

        // ✅ Check if Tenant has an Approved Booking
        Booking approvedBooking = bookingRepository.findByTenantIdAndRequestStatus(tenant.getId(), "APPROVED");
        if (approvedBooking != null) {
            model.addAttribute("allocatedRoom", approvedBooking.getRoom().getRoomNo()); // Pass allocated room to frontend
        }

        // ✅ Show rejection message (if any)
        if (session.getAttribute("bookingRejectionMessage") != null) {
            model.addAttribute("bookingRejectionMessage", session.getAttribute("bookingRejectionMessage"));
            session.removeAttribute("bookingRejectionMessage"); // Remove after displaying
        }

        // ✅ Pass booking error messages if any
        if (session.getAttribute("bookingError") != null) {
            model.addAttribute("bookingError", session.getAttribute("bookingError"));
            session.removeAttribute("bookingError"); // Remove after displaying
        }

        // ✅ Success message for booking request
        if (session.getAttribute("bookingSuccessMessage") != null) {
            model.addAttribute("bookingSuccessMessage", session.getAttribute("bookingSuccessMessage"));
            session.removeAttribute("bookingSuccessMessage"); // Remove after displaying
        }

        return "tenantDash"; // Load tenant dashboard
    }

    // ✅ Book a Room (Saving Booking Directly in Controller)
    @PostMapping("/book-room/{roomNo}")
    public String bookRoom(@PathVariable int roomNo,
                           @RequestParam("checkin") String checkinDate,
                           @RequestParam("checkout") String checkoutDate,
                           HttpSession session,
                           Model model) {

        // ✅ Ensure session contains logged-in tenant
        String tenantEmail = (String) session.getAttribute("loggedInTenant");
        if (tenantEmail == null) {
            return "redirect:/tenantlogin"; // Redirect if not logged in
        }

        // ✅ Fetch Tenant
        Tenant tenant = tenantRepository.findByTMail(tenantEmail);
        if (tenant == null) {
            return "redirect:/tenantlogin"; // Redirect if tenant not found
        }

        // ✅ Check if the tenant already has an approved booking
        Booking existingBooking = bookingRepository.findByTenantIdAndRequestStatus(tenant.getId(), "APPROVED");
        if (existingBooking != null) {
            model.addAttribute("error", "You already have an approved booking. You can’t request a new room until your current booking is resolved.");
            return tenantDashboard(session, model); // ✅ Return the tenant dashboard with the error message
        }

        // ✅ Fetch Room
        Room room = roomRepository.findById(roomNo).orElse(null);
        if (room == null || room.isStatus()) {
            model.addAttribute("error", "Selected room is not available.");
            return tenantDashboard(session, model); // ✅ Return the tenant dashboard with the error message
        }

        // ✅ Save Booking (Request Status: PENDING)
        Booking booking = new Booking();
        booking.setTenant(tenant);
        booking.setRoom(room);
        booking.setCheckinDate(LocalDate.parse(checkinDate));
        booking.setCheckoutDate(LocalDate.parse(checkoutDate));
        booking.setRequestStatus("PENDING");

        bookingRepository.save(booking);

        // ✅ Set success message for booking request
        session.setAttribute("bookingSuccessMessage", "Booking request sent successfully!");

        return "redirect:/tenant/dashboard"; // Redirect to the tenant dashboard after booking
    }

    // ✅ Handle Rejected Booking
    @PostMapping("/reject-booking/{bookingId}")
    public String rejectBooking(@PathVariable Long bookingId, HttpSession session) {
        // Fetch booking by ID
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null) {
            // Change the booking status to REJECTED
            booking.setRequestStatus("REJECTED");
            bookingRepository.save(booking);

            // Set rejection message for tenant dashboard
            session.setAttribute("bookingRejectionMessage", "Your previous booking request was rejected. You can now request a new room.");
        }

        return "redirect:/tenant/dashboard"; // Redirect back to the tenant dashboard
    }
    

}
