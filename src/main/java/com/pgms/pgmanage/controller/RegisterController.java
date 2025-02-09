package com.pgms.pgmanage.controller;

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

	@Autowired
	private TenantService tenantService;

	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("tenantDto", new TenantDto());
		return "register";
	}

	@PostMapping("/register")
	public String signUp(@Valid @ModelAttribute("tenantDto") TenantDto tenantDto, BindingResult result, Model model) {

		// ✅ If validation fails, return to register page with error messages
		if (result.hasErrors()) {
			return "register"; // Reload the form with validation errors
		}

		// ✅ Validate Registration (Handled in Service Layer)
		String registrationResult = tenantService.registerTenant(tenantDto);

		// ✅ Handle Errors (If Registration Fails)
		if (!registrationResult.equals("Registration Successful!")) {
			model.addAttribute("error", registrationResult);
			return "register"; // Stay on the registration page if there's an error
		}

		// ✅ Successful Registration
		model.addAttribute("message", "Registration Successful!!");
		return "redirect:/tenantlogin"; // Redirect to login page on success
	}

}
