package com.pgms.pgmanage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pgms.pgmanage.service.ManagerService;
import com.pgms.pgmanage.entity.Manager;
import com.pgms.pgmanage.entity.Booking;
import com.pgms.pgmanage.entity.Room;
import com.pgms.pgmanage.entity.Tenant;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    // ✅ Manager Login Page
    @GetMapping("/login")
    public String managerLoginPage() {
        return "mngrLogin"; // Thymeleaf manager login page
    }

    // ✅ Process Manager Login
    @PostMapping("/login")
    public String loginManager(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        Manager manager = managerService.validateManager(email, password);

        if (manager == null) {
            model.addAttribute("error", "Invalid email or password. Please try again.");
            return "mngrLogin"; // Stay on login page if credentials are incorrect
        }

        // Store manager details in session
        session.setAttribute("loggedInManager", manager.getEmail());
        session.setAttribute("managerName", manager.getManagerName());

        return "redirect:/manager/dashboard";
    }

    // ✅ Manager Dashboard - Fetch Pending Bookings
    @GetMapping("/dashboard")
    public String managerDashboard(HttpSession session, Model model) {
        if (session.getAttribute("loggedInManager") == null) {
            return "redirect:/manager/login"; // Redirect to login if not logged in
        }

        // ✅ Fetch only PENDING bookings (so rejected ones disappear)
        List<Booking> pendingBookings = managerService.getPendingBookings();
        model.addAttribute("bookings", pendingBookings);

        // ✅ Fetch all tenants
        List<Tenant> tenants = managerService.getAllTenants();
        model.addAttribute("tenants", tenants);

        model.addAttribute("managerName", session.getAttribute("managerName"));
        return "managerDash";
    }

    // ✅ Approve Booking
    @PostMapping("/approve-booking/{bookingId}")
    public String approveBooking(@PathVariable Long bookingId, Model model) {
        try {
            managerService.approveBooking(bookingId);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "managerDash"; // Return to dashboard with error message
        }
        return "redirect:/manager/dashboard";
    }

    // ✅ Reject Booking
    @PostMapping("/reject-booking/{bookingId}")
    public String rejectBooking(@PathVariable Long bookingId) {
        managerService.rejectBooking(bookingId);
        return "redirect:/manager/dashboard";
    }

    // ✅ Logout Manager
    @GetMapping("/logout")
    public String logoutManager(HttpSession session) {
        session.invalidate(); // Clear session
        return "redirect:/manager/login";
    }

    // ✅ Manage Rooms Page
    @GetMapping("/manage-rooms")
    public String manageRooms(HttpSession session, Model model) {
        if (session.getAttribute("loggedInManager") == null) {
            return "redirect:/manager/login"; // Redirect if not logged in
        }

        List<Room> rooms = managerService.getAllRooms();
        model.addAttribute("rooms", rooms);

        return "manageRooms"; // Ensure Thymeleaf finds `manageRooms.html`
    }

    // ✅ Add Room
    @PostMapping("/add-room")
    public String addRoom(@RequestParam int roomNo, @RequestParam boolean type) {
        managerService.addRoom(roomNo, type);
        return "redirect:/manager/manage-rooms";
    }

    // ✅ Remove Room
    @PostMapping("/remove-room")
    public String removeRoom(@RequestParam int roomNo) {
        managerService.removeRoom(roomNo);
        return "redirect:/manager/manage-rooms";
    }
}
