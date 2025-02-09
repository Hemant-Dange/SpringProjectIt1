package com.pgms.pgmanage.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);

	@Autowired
	private ManagerService managerService;

	@GetMapping("/login")
	public String managerLoginPage() {
		logger.info("Manager login page accessed");
		return "mngrLogin";
	}

	@PostMapping("/login")
	public String loginManager(@RequestParam String email, @RequestParam String password, HttpSession session,
			Model model) {
		logger.info("Manager login attempt with email: {}", email);

		Manager manager = managerService.validateManager(email, password);

		if (manager == null) {
			logger.warn("Failed login attempt for email: {}", email);
			model.addAttribute("error", "Invalid email or password. Please try again.");
			return "mngrLogin";
		}

		session.setAttribute("loggedInManager", manager.getEmail());
		session.setAttribute("managerName", manager.getManagerName());

		logger.info("Manager login successful: {}", email);
		return "redirect:/manager/dashboard";
	}

	@GetMapping("/dashboard")
	public String managerDashboard(HttpSession session, Model model) {
		if (session.getAttribute("loggedInManager") == null) {
			logger.warn("Unauthorized access attempt to manager dashboard");
			return "redirect:/manager/login";
		}

		logger.info("Manager accessing dashboard");

		List<Booking> pendingBookings = managerService.getPendingBookings();
		model.addAttribute("bookings", pendingBookings);

		List<Tenant> tenants = managerService.getAllTenants();
		model.addAttribute("tenants", tenants);

		List<Booking> allBookings = managerService.getAllBookings();
		model.addAttribute("allBookings", allBookings);

		model.addAttribute("managerName", session.getAttribute("managerName"));

		return "managerDash";
	}

	@PostMapping("/approve-booking/{bookingId}")
	public String approveBooking(@PathVariable Long bookingId, Model model) {
		try {
			logger.info("Manager approving booking ID: {}", bookingId);
			managerService.approveBooking(bookingId);
		} catch (IllegalStateException e) {
			logger.warn("Failed to approve booking ID: {} - Reason: {}", bookingId, e.getMessage());
			model.addAttribute("error", e.getMessage());
			return "managerDash";
		}
		return "redirect:/manager/dashboard";
	}

	@PostMapping("/reject-booking/{bookingId}")
	public String rejectBooking(@PathVariable Long bookingId) {
		logger.info("Manager rejecting booking ID: {}", bookingId);
		managerService.rejectBooking(bookingId);
		return "redirect:/manager/dashboard";
	}

	@GetMapping("/logout")
	public String logoutManager(HttpSession session) {
		String managerEmail = (String) session.getAttribute("loggedInManager");

		if (managerEmail != null) {
			logger.info("Manager logged out: {}", managerEmail);
		} else {
			logger.info("Logout attempted, but no manager was logged in");
		}

		session.invalidate();
		return "redirect:/manager/login";
	}

	@GetMapping("/manage-rooms")
	public String manageRooms(HttpSession session, Model model) {
		if (session.getAttribute("loggedInManager") == null) {
			logger.warn("Unauthorized access attempt to Manage Rooms page");
			return "redirect:/manager/login";
		}

		logger.info("Manager accessing room management page");

		List<Room> rooms = managerService.getAllRooms();
		model.addAttribute("rooms", rooms);

		return "manageRooms";
	}

	@PostMapping("/add-room")
	public String addRoom(@RequestParam int roomNo, @RequestParam boolean type, RedirectAttributes redirectAttributes) {
		try {
			logger.info("Adding new room: Room No {}, Type: {}", roomNo, type ? "AC" : "Non-AC");
			managerService.addRoom(roomNo, type);
			redirectAttributes.addFlashAttribute("message", "Room added successfully!");
		} catch (IllegalArgumentException e) {
			logger.warn("Failed to add room: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		} catch (IllegalStateException e) {
			logger.warn("Room already exists: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		} catch (Exception e) {
			logger.error("Unexpected error while adding room: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("error", "An unexpected error occurred: " + e.getMessage());
		}
		return "redirect:/manager/manage-rooms";
	}

	@PostMapping("/remove-room")
	public String removeRoom(@RequestParam int roomNo, RedirectAttributes redirectAttributes) {
		try {
			logger.info("Attempting to remove room: Room No {}", roomNo);
			managerService.removeRoom(roomNo);
			redirectAttributes.addFlashAttribute("message", "Room removed successfully!");
		} catch (IllegalStateException e) {
			logger.warn("Cannot remove room {}: Room is occupied", roomNo);
			redirectAttributes.addFlashAttribute("error", "You cannot delete this room as it is occupied by a tenant.");
		} catch (Exception e) {
			logger.error("Unexpected error while removing room: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/manager/manage-rooms";
	}
}
