package com.pgms.pgmanage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(TenantController.class);

	@Autowired
	private TenantService tenantService;

	@GetMapping("/dashboard")
	public String tenantDashboard(HttpSession session, Model model) {
		if (session.getAttribute("loggedInTenant") == null) {
			logger.warn("Unauthorized access attempt to tenant dashboard");
			return "redirect:/tenantlogin";
		}

		String tenantEmail = (String) session.getAttribute("loggedInTenant");
		Tenant tenant = tenantService.getTenantByEmail(tenantEmail);
		if (tenant == null) {
			logger.warn("Tenant not found for email: {}", tenantEmail);
			return "redirect:/tenantlogin";
		}

		logger.info("Tenant dashboard accessed by: {}", tenantEmail);
		model.addAttribute("tenantUsername", tenant.getUsername());

		model.addAttribute("rooms", tenantService.getAvailableRooms());

		model.addAttribute("allocatedRoom", tenantService.getAllocatedRoomNumber(tenant.getId()));

		addSessionMessagesToModel(session, model);

		return "tenantDash";
	}

	@PostMapping("/book-room/{roomNo}")
	public String bookRoom(@PathVariable int roomNo, @RequestParam("checkin") String checkinDate,
			@RequestParam("checkout") String checkoutDate, HttpSession session, Model model) {

		String tenantEmail = (String) session.getAttribute("loggedInTenant");
		if (tenantEmail == null) {
			logger.warn("Unauthorized booking attempt - no logged-in tenant");
			return "redirect:/tenantlogin";
		}

		logger.info("Tenant {} requesting booking for Room {} (Check-in: {}, Check-out: {})", tenantEmail, roomNo,
				checkinDate, checkoutDate);

		String bookingResponse = tenantService.requestBooking(tenantEmail, roomNo, checkinDate, checkoutDate);

		session.setAttribute("bookingMessage", bookingResponse);

		logger.info("Booking response for {}: {}", tenantEmail, bookingResponse);
		return "redirect:/tenant/dashboard";
	}

	@PostMapping("/reject-booking/{bookingId}")
	public String rejectBooking(@PathVariable Long bookingId, HttpSession session) {
		logger.info("Tenant rejecting booking ID: {}", bookingId);
		tenantService.rejectBooking(bookingId);
		session.setAttribute("bookingRejectionMessage",
				"Your previous booking request was rejected. You can now request a new room.");
		return "redirect:/tenant/dashboard";
	}

	private void addSessionMessagesToModel(HttpSession session, Model model) {
		if (session.getAttribute("bookingMessage") != null) {
			model.addAttribute("bookingMessage", session.getAttribute("bookingMessage"));
			logger.info("Displaying booking message: {}", session.getAttribute("bookingMessage"));
			session.removeAttribute("bookingMessage");
		}
		if (session.getAttribute("bookingRejectionMessage") != null) {
			model.addAttribute("bookingRejectionMessage", session.getAttribute("bookingRejectionMessage"));
			logger.info("Displaying rejection message: {}", session.getAttribute("bookingRejectionMessage"));
			session.removeAttribute("bookingRejectionMessage");
		}
	}
}
