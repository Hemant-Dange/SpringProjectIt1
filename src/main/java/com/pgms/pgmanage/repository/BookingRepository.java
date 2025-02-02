package com.pgms.pgmanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pgms.pgmanage.entity.Booking;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRequestStatus(String status); // Get bookings by status
    List<Booking> findByUserId(Long userId); // Get bookings for a specific tenant
    Booking findByUserIdAndRequestStatus(Long userId, String requestStatus);
}
