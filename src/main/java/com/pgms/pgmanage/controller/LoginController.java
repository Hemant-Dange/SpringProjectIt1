package com.pgms.pgmanage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pgms.pgmanage.dto.TenantDto;
import com.pgms.pgmanage.service.TenantService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    @Autowired
    private TenantService tenantService;

    // ✅ Tenant Login Page
    @GetMapping("/tenantlogin")
    public String getTenantLogin(Model model) {
        model.addAttribute("tenantDto", new TenantDto());
        return "tenantLogin"; // Thymeleaf login page
    }

    // ✅ Process Tenant Login
    @PostMapping("/tenantlogin")
    public String tenantLogin(@ModelAttribute TenantDto tenantDto, Model model, HttpSession session) {
        // ✅ Authenticate Tenant
        TenantDto authenticatedTenant = tenantService.authenticateTenant(tenantDto.gettMail(), tenantDto.getPassword());

        if (authenticatedTenant == null) {
            model.addAttribute("error", "Invalid email or password. Please try again.");
            return "tenantLogin"; // Stay on login page if credentials are incorrect
        }

        // ✅ Store tenant information in session
        session.setAttribute("loggedInTenant", authenticatedTenant.gettMail());
        session.setAttribute("tenantUsername", authenticatedTenant.getUsername());

        return "redirect:/tenant/dashboard"; // Redirect to dashboard on successful login
    }

    // ✅ Tenant Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Clear session
        return "redirect:/tenantlogin"; // Redirect after logout
    }
}
