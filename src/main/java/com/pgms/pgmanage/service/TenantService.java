package com.pgms.pgmanage.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pgms.pgmanage.dto.TenantDto;
import com.pgms.pgmanage.entity.Booking;
import com.pgms.pgmanage.entity.Room;
import com.pgms.pgmanage.entity.Tenant;
import com.pgms.pgmanage.repository.BookingRepository;
import com.pgms.pgmanage.repository.RoomRepository;
import com.pgms.pgmanage.repository.TenantRepository;

@Service
public class TenantService {

	private static final Logger logger = LoggerFactory.getLogger(TenantService.class);

	@Autowired
	private TenantRepository tenantRepo;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private RoomRepository roomRepository;

	public Tenant getTenantByEmail(String email) {
		logger.info("Fetching tenant with email: {}", email);
		Tenant tenant = tenantRepo.findByTMail(email);

		if (tenant == null) {
			logger.warn("No tenant found with email: {}", email);
		}
		return tenant;
	}

	public List<Room> getAvailableRooms() {
		logger.info("Fetching available rooms...");
		List<Room> rooms = roomRepository.findByStatus(false);

		if (rooms.isEmpty()) {
			logger.warn("No available rooms found.");
		} else {
			logger.info("Found {} available rooms.", rooms.size());
		}
		return rooms;
	}

	public Integer getAllocatedRoomNumber(Long tenantId) {
		logger.info("Fetching allocated room number for tenant ID: {}", tenantId);
		Booking approvedBooking = bookingRepository.findByTenantIdAndRequestStatus(tenantId, "APPROVED");

		if (approvedBooking != null) {
			logger.info("Tenant ID {} has been allocated Room No: {}", tenantId, approvedBooking.getRoom().getRoomNo());
			return approvedBooking.getRoom().getRoomNo();
		} else {
			logger.info("Tenant ID {} does not have an approved booking.", tenantId);
			return null;
		}
	}

	public String registerTenant(TenantDto tenantDto) {
		logger.info("Registering new tenant: {}", tenantDto.getUsername());

		if (tenantRepo.existsByTMail(tenantDto.gettMail())) {
			logger.warn("Registration failed: Email {} is already registered.", tenantDto.gettMail());
			return "Email is already registered!";
		}

		if (tenantRepo.existsByUsername(tenantDto.getUsername())) {
			logger.warn("Registration failed: Username {} is already taken.", tenantDto.getUsername());
			return "Username is already taken!";
		}

		if (tenantRepo.existsByPhNumber(tenantDto.getPhNumber())) {
			logger.warn("Registration failed: Phone number {} is already registered.", tenantDto.getPhNumber());
			return "Phone number is already registered!";
		}

		if (!tenantDto.getPassword().equals(tenantDto.getConPassword())) {
			logger.warn("Registration failed: Passwords do not match for user {}.", tenantDto.getUsername());
			return "Passwords do not match!";
		}

		Tenant tenant = new Tenant();
		tenant.setUsername(tenantDto.getUsername());
		tenant.settMail(tenantDto.gettMail());
		tenant.setPhNumber(tenantDto.getPhNumber());
		tenant.setPassword(tenantDto.getPassword());

		tenantRepo.save(tenant);
		logger.info("Registration successful for tenant: {}", tenantDto.getUsername());

		return "Registration Successful!";
	}

	public TenantDto authenticateTenant(String email, String password) {
		logger.info("Authenticating tenant with email: {}", email);
		Tenant tenant = tenantRepo.findByTMail(email);

		if (tenant != null && tenant.getPassword().equals(password)) {
			logger.info("Authentication successful for tenant: {}", tenant.getUsername());
			TenantDto tenantDto = new TenantDto();
			tenantDto.setUsername(tenant.getUsername());
			tenantDto.settMail(tenant.gettMail());
			tenantDto.setPhNumber(tenant.getPhNumber());
			return tenantDto;
		}

		logger.warn("Authentication failed for email: {}", email);
		return null;
	}

	public String requestBooking(String tenantEmail, int roomNo, String checkinDate, String checkoutDate) {
		logger.info("Processing booking request for tenant: {} | Room No: {} | Check-in: {} | Check-out: {}",
				tenantEmail, roomNo, checkinDate, checkoutDate);

		Tenant tenant = tenantRepo.findByTMail(tenantEmail);
		if (tenant == null) {
			logger.warn("Booking failed: Tenant not found with email {}", tenantEmail);
			return "Tenant not found!";
		}

		if (hasActiveBooking(tenant.getId())) {
			logger.warn("Booking failed: Tenant {} already has an active booking.", tenant.getUsername());
			return "You already have an active booking (Pending/Approved).";
		}

		Room room = roomRepository.findById(roomNo).orElse(null);
		if (room == null || room.isStatus()) {
			logger.warn("Booking failed: Room No {} is not available.", roomNo);
			return "Selected room is not available.";
		}

		Booking booking = new Booking();
		booking.setTenant(tenant);
		booking.setRoom(room);
		booking.setCheckinDate(LocalDate.parse(checkinDate));
		booking.setCheckoutDate(LocalDate.parse(checkoutDate));
		booking.setRequestStatus("PENDING");

		bookingRepository.save(booking);
		logger.info("Booking request sent successfully for Tenant: {} | Room No: {}", tenant.getUsername(), roomNo);

		return "Booking request sent successfully!";
	}

	public void rejectBooking(Long bookingId) {
		logger.info("Rejecting booking with ID: {}", bookingId);
		Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

		bookingOptional.ifPresent(booking -> {
			booking.setRequestStatus("REJECTED");
			bookingRepository.save(booking);
			logger.info("Booking ID {} has been rejected.", bookingId);
		});

		if (bookingOptional.isEmpty()) {
			logger.warn("Booking ID {} not found. Cannot reject.", bookingId);
		}
	}

	public boolean hasActiveBooking(Long tenantId) {
		boolean hasActive = bookingRepository.existsByTenantIdAndRequestStatusIn(tenantId,
				List.of("PENDING", "APPROVED"));
		logger.info("Checking active booking for Tenant ID {}: {}", tenantId, hasActive ? "YES" : "NO");
		return hasActive;
	}
}
