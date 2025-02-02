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
        Tenant tenant = tenantRepository.findBytMail(tenantEmail);
        if (tenant == null) {
            return "redirect:/tenantlogin"; // Redirect if tenant not found
        }

        model.addAttribute("tenantUsername", tenant.getUsername()); // Pass username to the frontend

        // ✅ Fetch Available Rooms (Vacant Only)
        List<Room> availableRooms = roomRepository.findByStatus(false); // false = vacant
        model.addAttribute("rooms", availableRooms);

        // ✅ Check if Tenant has an Approved Booking
        Booking approvedBooking = bookingRepository.findByUserIdAndRequestStatus(tenant.getId(), "APPROVED");
        if (approvedBooking != null) {
            model.addAttribute("allocatedRoom", approvedBooking.getRoomNo()); // Pass allocated room to frontend
        }

        return "tenantDash"; // Load tenant dashboard
    }

    // ✅ Book a Room
    @PostMapping("/book-room/{roomNo}")
    public String bookRoom(@PathVariable int roomNo,
                           @RequestParam("checkin") String checkinDate,
                           @RequestParam("checkout") String checkoutDate,
                           HttpSession session) {

        // ✅ Ensure session contains logged-in tenant
        String tenantEmail = (String) session.getAttribute("loggedInTenant");
        if (tenantEmail == null) {
            return "redirect:/tenantlogin"; // Redirect if not logged in
        }

        // ✅ Fetch Tenant
        Tenant tenant = tenantRepository.findBytMail(tenantEmail);
        if (tenant == null) {
            return "redirect:/tenantlogin"; // Redirect if tenant not found
        }

        Long userId = tenant.getId(); // Ensure userId is Long

        // ✅ Fetch Room
        Room room = roomRepository.findById(roomNo).orElse(null);
        if (room == null || room.isStatus()) {
            return "redirect:/tenant/dashboard"; // Room not available
        }

        // ✅ Save Booking
        Booking booking = new Booking(userId, roomNo, LocalDate.parse(checkinDate), LocalDate.parse(checkoutDate),
                "PENDING");
        bookingRepository.save(booking);

        return "redirect:/tenant/dashboard"; // Redirect after booking
    }
}
