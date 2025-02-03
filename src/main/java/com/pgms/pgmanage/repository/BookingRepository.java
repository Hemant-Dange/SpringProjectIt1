package com.pgms.pgmanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pgms.pgmanage.entity.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ✅ Get all bookings with a specific status (PENDING, APPROVED, REJECTED)
    List<Booking> findByRequestStatus(String status);

    // ✅ Get a tenant's booking (Since each tenant can have only one booking)
    Booking findByTenantId(Long tenantId);  

    // ✅ Get a tenant's booking with a specific status (e.g., "APPROVED")
    Booking findByTenantIdAndRequestStatus(Long tenantId, String requestStatus);
}
