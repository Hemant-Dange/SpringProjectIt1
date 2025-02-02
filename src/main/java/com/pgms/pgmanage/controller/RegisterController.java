package com.pgms.pgmanage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.pgms.pgmanage.dto.TenantDto;

import com.pgms.pgmanage.service.TenantService;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
public class RegisterController {
	
	@Autowired
	private TenantService tenantService;

	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("tenantDto",new TenantDto());//what is this
		return "register";
	}
	
	@PostMapping("/register")
	public String signUp(@ModelAttribute TenantDto tenantDto, Model model) {
		
		if (!tenantDto.getConPassword().equals(tenantDto.getPassword())) {
			model.addAttribute("error", "Passwords do not match");
			return "register";
		}
		
		boolean tenantNotRegistered = tenantService.registerTenant(tenantDto);
		
		
		if(!tenantNotRegistered) {
			model.addAttribute("error", "User already exists");
			return "register";
		}
		
		model.addAttribute("message", "Registration Successful!!");
		return "redirect:/tenantlogin";
	}
	
	
}
