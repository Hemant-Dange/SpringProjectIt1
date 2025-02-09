package com.pgms.pgmanage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.pgms.pgmanage.dto.TenantDto;
import com.pgms.pgmanage.service.TenantService;

import jakarta.validation.Valid;

@Controller
public class RegisterController {

	private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

	@Autowired
	private TenantService tenantService;

	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		logger.info("Accessed registration page");
		model.addAttribute("tenantDto", new TenantDto());
		return "register";
	}

	@PostMapping("/register")
	public String signUp(@Valid @ModelAttribute("tenantDto") TenantDto tenantDto, BindingResult result, Model model) {
		logger.info("New registration attempt for email: {}", tenantDto.gettMail());

		if (result.hasErrors()) {
			logger.warn("Validation errors found during registration for email: {}", tenantDto.gettMail());
			return "register";
		}

		String registrationResult = tenantService.registerTenant(tenantDto);

		if (!registrationResult.equals("Registration Successful!")) {
			logger.warn("Registration failed for email: {} - Reason: {}", tenantDto.gettMail(), registrationResult);
			model.addAttribute("error", registrationResult);
			return "register";
		}

		logger.info("Registration successful for email: {}", tenantDto.gettMail());
		model.addAttribute("message", "Registration Successful!!");
		return "redirect:/tenantlogin";
	}
}
