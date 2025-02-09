package com.pgms.pgmanage.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pgms.pgmanage.entity.Booking;
import com.pgms.pgmanage.entity.Manager;
import com.pgms.pgmanage.entity.Room;
import com.pgms.pgmanage.entity.Tenant;
import com.pgms.pgmanage.repository.BookingRepository;
import com.pgms.pgmanage.repository.ManagerRepository;
import com.pgms.pgmanage.repository.RoomRepository;
import com.pgms.pgmanage.repository.TenantRepository;

@Service
public class ManagerService {

	private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);

	@Autowired
	private ManagerRepository managerRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private TenantRepository tenantRepository;

	public Manager validateManager(String email, String password) {
		logger.info("Manager login attempt: {}", email);
		Manager manager = managerRepository.findByEmail(email);
		if (manager != null && manager.getPassword().equals(password)) {
			logger.info("Manager login successful: {}", email);
			return manager;
		}
		logger.warn("Manager login failed for email: {}", email);
		return null;
	}

	public List<Room> getAllRooms() {
		logger.info("Fetching all rooms for management.");
		return roomRepository.findAll();
	}

	public List<Tenant> getAllTenants() {
		logger.info("Fetching all tenants.");
		return tenantRepository.findAll();
	}

	public List<Booking> getPendingBookings() {
		logger.info("Fetching pending bookings.");
		return bookingRepository.findByRequestStatus("PENDING");
	}

	public List<Booking> getAllBookings() {
		logger.info("Fetching all bookings in descending order.");
		return bookingRepository.findAllByOrderByIdDesc();
	}

	public void approveBooking(Long bookingId) {
		logger.info("Attempting to approve booking with ID: {}", bookingId);
		Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
		if (optionalBooking.isPresent()) {
			Booking booking = optionalBooking.get();
			Tenant tenant = booking.getTenant();
			Room room = booking.getRoom();

			if (bookingRepository.findByTenantIdAndRequestStatus(tenant.getId(), "APPROVED") != null) {
				logger.warn("Tenant {} already has an approved booking!", tenant.getUsername());
				throw new IllegalStateException("Tenant already has an approved booking!");
			}

			booking.setRequestStatus("APPROVED");
			bookingRepository.save(booking);
			logger.info("Booking ID: {} approved for Tenant: {}", bookingId, tenant.getUsername());

			room.setStatus(true);
			roomRepository.save(room);
			logger.info("Room {} marked as occupied.", room.getRoomNo());
		} else {
			logger.warn("Booking ID: {} not found for approval.", bookingId);
		}
	}

	public void rejectBooking(Long bookingId) {
		logger.info("Attempting to reject booking with ID: {}", bookingId);
		Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
		if (optionalBooking.isPresent()) {
			Booking booking = optionalBooking.get();
			booking.setRequestStatus("REJECTED");
			bookingRepository.save(booking);
			logger.info("Booking ID: {} rejected.", bookingId);
		} else {
			logger.warn("Booking ID: {} not found for rejection.", bookingId);
		}
	}

	public void addRoom(int roomNo, boolean type) {
		logger.info("Attempting to add Room No: {}, Type: {}", roomNo, type ? "AC" : "Non-AC");

		if (roomNo < 1) {
			logger.warn("Invalid room number: {}", roomNo);
			throw new IllegalArgumentException("Room number must be a positive number!");
		}

		if (roomRepository.existsById(roomNo)) {
			logger.warn("Room No: {} already exists!", roomNo);
			throw new IllegalStateException("Room number " + roomNo + " already exists!");
		}

		roomRepository.save(new Room(roomNo, type, false)); 
		logger.info("Room No: {} successfully added.", roomNo);
	}

	@Transactional
	public void removeRoom(int roomNo) {
		logger.info("Attempting to remove Room No: {}", roomNo);
		Room room = roomRepository.findById(roomNo).orElse(null);
		if (room == null) {
			logger.warn("Room No: {} not found for deletion.", roomNo);
			throw new IllegalArgumentException("Room not found");
		}

		boolean isOccupied = bookingRepository.existsByRoomAndRequestStatus(room, "APPROVED");
		if (isOccupied) {
			logger.warn("Cannot delete Room No: {} as it is occupied.", roomNo);
			throw new IllegalStateException("Cannot delete room as it is occupied by a tenant.");
		}

		List<Booking> bookings = bookingRepository.findByRoom(room);
		bookingRepository.deleteAll(bookings);
		logger.info("Deleted all bookings associated with Room No: {}", roomNo);

		roomRepository.delete(room);
		logger.info("Room No: {} successfully removed.", roomNo);
	}
}
