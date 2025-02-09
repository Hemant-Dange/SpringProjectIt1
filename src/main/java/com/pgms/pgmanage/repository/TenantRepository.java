package com.pgms.pgmanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pgms.pgmanage.entity.Tenant;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    // ✅ Find tenant by email
    Tenant findByTMail(String tMail);  

    // ✅ Find tenant by username
//    Tenant findByUsername(String username);

    // ✅ Find tenant by phone number
//    Tenant findByPhNumber(Long phNumber);

    // ✅ Check if email exists (for validation during registration)
    boolean existsByTMail(String tMail);

    // ✅ Check if username exists (for validation during registration)
    boolean existsByUsername(String username);

    // ✅ Check if phone number exists (for validation during registration)
    boolean existsByPhNumber(Long phNumber);
}
