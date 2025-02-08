package com.pgms.pgmanage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pgms.pgmanage.entity.Booking;
import com.pgms.pgmanage.entity.Manager;
import com.pgms.pgmanage.entity.Room;
import com.pgms.pgmanage.entity.Tenant;
import com.pgms.pgmanage.service.ManagerService;

import jakarta.servlet.http.HttpSession;

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
	public String loginManager(@RequestParam String email, @RequestParam String password, HttpSession session,
			Model model) {
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
	public String addRoom(@RequestParam int roomNo, @RequestParam boolean type, RedirectAttributes redirectAttributes) {
	    try {
	        // Call the service to add the room
	        managerService.addRoom(roomNo, type);
	        redirectAttributes.addFlashAttribute("message", "Room added successfully!");
	    } catch (IllegalArgumentException e) {
	        // Handle the case where the room number is negative or invalid
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	    } catch (IllegalStateException e) {
	        // Handle the case where the room number already exists
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	    } catch (Exception e) {
	        // Catch any other unexpected exceptions
	        redirectAttributes.addFlashAttribute("error", "An unexpected error occurred: " + e.getMessage());
	    }
	    return "redirect:/manager/manage-rooms";  // Redirect back to the Manage Rooms page
	}




	// ✅ Remove Room and display success message
	@PostMapping("/remove-room")
	public String removeRoom(@RequestParam int roomNo, RedirectAttributes redirectAttributes) {
	    try {
	        // ✅ Call service to remove the room and handle associated bookings
	        managerService.removeRoom(roomNo);
	        redirectAttributes.addFlashAttribute("message", "Room and associated bookings removed successfully!");
	        
	    } catch (IllegalStateException e) { // ✅ Handle occupied room case
	        redirectAttributes.addFlashAttribute("error", "You cannot delete this room as it is occupied by a tenant.");
	        
	    } catch (Exception e) { // ✅ Handle other exceptions
	        redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the room.");
	    }
	    return "redirect:/manager/manage-rooms"; // Redirect back to manage rooms page
	}


}
