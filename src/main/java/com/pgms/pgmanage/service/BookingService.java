package com.pgms.pgmanage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pgms.pgmanage.repository.BookingRepository;

@Service
public class BookingService {

	@Autowired
    private BookingRepository bookingRepository;

    public boolean hasActiveBooking(Long tenantId) {
        return bookingRepository.existsByTenantIdAndRequestStatusIn(
            tenantId, List.of("PENDING", "APPROVED")
        );
    }
	
}
