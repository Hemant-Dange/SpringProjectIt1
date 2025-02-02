package com.pgms.pgmanage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pgms.pgmanage.dto.TenantDto;
import com.pgms.pgmanage.entity.Tenant;
import com.pgms.pgmanage.repository.TenantRepository;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepo;

    // ✅ Tenant Registration (Without Password Hashing)
    public boolean registerTenant(TenantDto tenantDto) {
        // Check if email already exists
        if (tenantRepo.findBytMail(tenantDto.gettMail()) != null) {
            return false; // Email is already registered
        }

        // Create a new Tenant entity
        Tenant tenant = new Tenant();
        tenant.setUsername(tenantDto.getUsername());
        tenant.settMail(tenantDto.gettMail());
        tenant.setPhNumber(tenantDto.getPhNumber());

        // Storing passwords as plain text (⚠️ Security risk, consider hashing in the future)
        tenant.setPassword(tenantDto.getPassword());
        tenant.setConPassword(tenantDto.getConPassword());

        // Save to database
        tenantRepo.save(tenant);

        return true; // Registration successful
    }

    // ✅ Tenant Login (Simple Email & Password Comparison)
    public TenantDto authenticateTenant(String email, String password) {
        // Find tenant by email
        Tenant tenant = tenantRepo.findBytMail(email);

        // Check if tenant exists and password matches
        if (tenant != null && tenant.getPassword().equals(password)) {
            // ✅ Create a new TenantDto object with only necessary fields
            TenantDto tenantDto = new TenantDto();
            tenantDto.setUsername(tenant.getUsername());
            tenantDto.settMail(tenant.gettMail());
            tenantDto.setPhNumber(tenant.getPhNumber());
            return tenantDto; // Returning only required fields
        }

        return null; // Authentication failed
    }
    
    public boolean authenticateTenant(TenantDto tenantDto) {
        Tenant tenant = tenantRepo.findBytMail(tenantDto.gettMail());

        return tenant != null && tenant.getPassword().equals(tenantDto.getPassword());
    }
}
