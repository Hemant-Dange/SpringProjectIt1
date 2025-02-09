package com.pgms.pgmanage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pgms.pgmanage.dto.TenantDto;
import com.pgms.pgmanage.service.TenantService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private TenantService tenantService;

	@GetMapping("/tenantlogin")
	public String getTenantLogin(Model model) {
		logger.info("Tenant login page accessed");
		model.addAttribute("tenantDto", new TenantDto());
		return "tenantLogin";
	}

	@PostMapping("/tenantlogin")
	public String tenantLogin(@ModelAttribute TenantDto tenantDto, Model model, HttpSession session) {
		logger.info("Tenant login attempt with email: {}", tenantDto.gettMail());

		TenantDto authenticatedTenant = tenantService.authenticateTenant(tenantDto.gettMail(), tenantDto.getPassword());

		if (authenticatedTenant == null) {
			logger.warn("Failed login attempt for email: {}", tenantDto.gettMail());
			model.addAttribute("error", "Invalid email or password. Please try again.");
			return "tenantLogin";
		}

		session.setAttribute("loggedInTenant", authenticatedTenant.gettMail());
		session.setAttribute("tenantUsername", authenticatedTenant.getUsername());

		logger.info("Tenant login successful: {}", authenticatedTenant.gettMail());
		return "redirect:/tenant/dashboard";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		String tenantEmail = (String) session.getAttribute("loggedInTenant");

		if (tenantEmail != null) {
			logger.info("Tenant logged out: {}", tenantEmail);
		} else {
			logger.info("Logout attempted, but no tenant was logged in");
		}

		session.invalidate();
		return "redirect:/tenantlogin";
	}
}
