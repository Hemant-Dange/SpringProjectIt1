package com.pgms.pgmanage.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pgms.pgmanage.entity.Booking;
import com.pgms.pgmanage.repository.BookingRepository;

@Service
public class BookingService {

	private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

	@Autowired
	private BookingRepository bookingRepository;

	public boolean hasActiveBooking(Long tenantId) {
		logger.info("Checking if tenant ID {} has an active booking...", tenantId);
		boolean exists = bookingRepository.existsByTenantIdAndRequestStatusIn(tenantId, List.of("PENDING", "APPROVED"));

		if (exists) {
			logger.info("Tenant ID {} has an active booking.", tenantId);
		} else {
			logger.info("Tenant ID {} has no active bookings.", tenantId);
		}
		return exists;
	}

	public Booking getApprovedBooking(Long tenantId) {
		logger.info("Fetching approved booking for tenant ID {}...", tenantId);
		Booking approvedBooking = bookingRepository.findByTenantIdAndRequestStatus(tenantId, "APPROVED");

		if (approvedBooking != null) {
			logger.info("Approved booking found for tenant ID {}: Booking ID {}", tenantId, approvedBooking.getId());
		} else {
			logger.info("No approved booking found for tenant ID {}.", tenantId);
		}

		return approvedBooking;
	}

	public boolean deleteBooking(Long bookingId) {
		logger.info("Attempting to delete booking ID {}...", bookingId);
		Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

		if (bookingOptional.isPresent()) {
			bookingRepository.delete(bookingOptional.get());
			logger.info("Successfully deleted booking ID {}.", bookingId);
			return true;
		}

		logger.warn("Booking ID {} not found. Cannot delete.", bookingId);
		return false;
	}
}
