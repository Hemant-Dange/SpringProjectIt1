package com.pgms.pgmanage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pgms.pgmanage.entity.Tenant;
import com.pgms.pgmanage.service.TenantService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/tenant")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    // ✅ Display Tenant Dashboard with Available Rooms
    @GetMapping("/dashboard")
    public String tenantDashboard(HttpSession session, Model model) {
        if (session.getAttribute("loggedInTenant") == null) {
            return "redirect:/tenantlogin"; // Redirect if not logged in
        }

        // ✅ Fetch Tenant Information
        String tenantEmail = (String) session.getAttribute("loggedInTenant");
        Tenant tenant = tenantService.getTenantByEmail(tenantEmail);
        if (tenant == null) {
            return "redirect:/tenantlogin";
        }

        model.addAttribute("tenantUsername", tenant.getUsername());

        // ✅ Fetch Available Rooms
        model.addAttribute("rooms", tenantService.getAvailableRooms());

        // ✅ Fetch Approved Booking (if exists)
        model.addAttribute("allocatedRoom", tenantService.getAllocatedRoomNumber(tenant.getId()));

        // ✅ Pass session messages to model
        addSessionMessagesToModel(session, model);

        return "tenantDash"; // Load tenant dashboard
    }

    // ✅ Book a Room (Delegating Logic to Service)
    @PostMapping("/book-room/{roomNo}")
    public String bookRoom(@PathVariable int roomNo, 
                           @RequestParam("checkin") String checkinDate,
                           @RequestParam("checkout") String checkoutDate, 
                           HttpSession session, Model model) {

        String tenantEmail = (String) session.getAttribute("loggedInTenant");
        if (tenantEmail == null) {
            return "redirect:/tenantlogin";
        }

        // ✅ Request booking through service
        String bookingResponse = tenantService.requestBooking(tenantEmail, roomNo, checkinDate, checkoutDate);

        // ✅ Store response message in session
        session.setAttribute("bookingMessage", bookingResponse);

        return "redirect:/tenant/dashboard"; 
    }

    // ✅ Handle Rejected Booking
    @PostMapping("/reject-booking/{bookingId}")
    public String rejectBooking(@PathVariable Long bookingId, HttpSession session) {
        tenantService.rejectBooking(bookingId);
        session.setAttribute("bookingRejectionMessage", "Your previous booking request was rejected. You can now request a new room.");
        return "redirect:/tenant/dashboard";
    }

    // ✅ Utility Method: Transfer Session Messages to Model
    private void addSessionMessagesToModel(HttpSession session, Model model) {
        if (session.getAttribute("bookingMessage") != null) {
            model.addAttribute("bookingMessage", session.getAttribute("bookingMessage"));
            session.removeAttribute("bookingMessage");
        }
        if (session.getAttribute("bookingRejectionMessage") != null) {
            model.addAttribute("bookingRejectionMessage", session.getAttribute("bookingRejectionMessage"));
            session.removeAttribute("bookingRejectionMessage");
        }
    }
}
