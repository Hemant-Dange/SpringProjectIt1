package com.pgms.pgmanage.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pgms.pgmanage.dto.TenantDto;
import com.pgms.pgmanage.entity.Booking;
import com.pgms.pgmanage.entity.Room;
import com.pgms.pgmanage.entity.Tenant;
import com.pgms.pgmanage.repository.BookingRepository;
import com.pgms.pgmanage.repository.RoomRepository;
import com.pgms.pgmanage.repository.TenantRepository;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

	@Mock
	private TenantRepository tenantRepository;

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private RoomRepository roomRepository;

	@InjectMocks
	private TenantService tenantService;

	private Tenant tenant;
	private Room room;
	private Booking booking;

	@BeforeEach
	void setUp() {
		tenant = new Tenant();
		tenant.setId(1L);
		tenant.setUsername("JohnDoe");
		tenant.settMail("john@example.com");
		tenant.setPassword("securePass");

		room = new Room(101, true, false);

		booking = new Booking();
		booking.setId(1L);
		booking.setTenant(tenant);
		booking.setRoom(room);
		booking.setCheckinDate(LocalDate.now());
		booking.setCheckoutDate(LocalDate.now().plusDays(3));
		booking.setRequestStatus("PENDING");
	}

	// ✅ Test Fetching Tenant by Email
	@Test
	void testGetTenantByEmail_Success() {
		when(tenantRepository.findByTMail("john@example.com")).thenReturn(tenant);

		Tenant result = tenantService.getTenantByEmail("john@example.com");

		assertThat(result).isNotNull();
		assertThat(result.getUsername()).isEqualTo("JohnDoe");
		verify(tenantRepository, times(1)).findByTMail("john@example.com");
	}

	@Test
	void testGetTenantByEmail_NotFound() {
		when(tenantRepository.findByTMail("unknown@example.com")).thenReturn(null);

		Tenant result = tenantService.getTenantByEmail("unknown@example.com");

		assertThat(result).isNull();
		verify(tenantRepository, times(1)).findByTMail("unknown@example.com");
	}

	// ✅ Test Fetching Available Rooms
	@Test
	void testGetAvailableRooms() {
		when(roomRepository.findByStatus(false)).thenReturn(List.of(room));

		List<Room> rooms = tenantService.getAvailableRooms();

		assertThat(rooms).hasSize(1);
		assertThat(rooms.get(0).getRoomNo()).isEqualTo(101);
		verify(roomRepository, times(1)).findByStatus(false);
	}

	@Test
	void testGetAvailableRooms_EmptyList() {
		when(roomRepository.findByStatus(false)).thenReturn(List.of());

		List<Room> rooms = tenantService.getAvailableRooms();

		assertThat(rooms).isEmpty();
		verify(roomRepository, times(1)).findByStatus(false);
	}

	// ✅ Test Allocated Room Fetching
	@Test
	void testGetAllocatedRoomNumber_Success() {
		when(bookingRepository.findByTenantIdAndRequestStatus(1L, "APPROVED")).thenReturn(booking);

		Integer allocatedRoom = tenantService.getAllocatedRoomNumber(1L);

		assertThat(allocatedRoom).isEqualTo(101);
		verify(bookingRepository, times(1)).findByTenantIdAndRequestStatus(1L, "APPROVED");
	}

	@Test
	void testGetAllocatedRoomNumber_NoBooking() {
		when(bookingRepository.findByTenantIdAndRequestStatus(1L, "APPROVED")).thenReturn(null);

		Integer allocatedRoom = tenantService.getAllocatedRoomNumber(1L);

		assertThat(allocatedRoom).isNull();
		verify(bookingRepository, times(1)).findByTenantIdAndRequestStatus(1L, "APPROVED");
	}

	@Test
	void testAuthenticateTenant_Success() {
		when(tenantRepository.findByTMail("john@example.com")).thenReturn(tenant);

		TenantDto result = tenantService.authenticateTenant("john@example.com", "securePass");

		assertThat(result).isNotNull();
		assertThat(result.getUsername()).isEqualTo("JohnDoe");
	}

	@Test
	void testAuthenticateTenant_Failure() {
		when(tenantRepository.findByTMail("john@example.com")).thenReturn(null);

		TenantDto result = tenantService.authenticateTenant("john@example.com", "securePass");

		assertThat(result).isNull();
	}

	// ✅ Test Request Booking
	@Test
	void testRequestBooking_Success() {
		when(tenantRepository.findByTMail("john@example.com")).thenReturn(tenant);
		when(bookingRepository.existsByTenantIdAndRequestStatusIn(1L, List.of("PENDING", "APPROVED")))
				.thenReturn(false);
		when(roomRepository.findById(101)).thenReturn(Optional.of(room));

		String result = tenantService.requestBooking("john@example.com", 101, "2025-02-10", "2025-02-15");

		assertThat(result).isEqualTo("Booking request sent successfully!");
		verify(bookingRepository, times(1)).save(any(Booking.class));
	}

	@Test
	void testRequestBooking_TenantNotFound() {
		when(tenantRepository.findByTMail("unknown@example.com")).thenReturn(null);

		String result = tenantService.requestBooking("unknown@example.com", 101, "2025-02-10", "2025-02-15");

		assertThat(result).isEqualTo("Tenant not found!");
		verify(bookingRepository, never()).save(any(Booking.class));
	}

	@Test
	void testRequestBooking_AlreadyHasBooking() {
		when(tenantRepository.findByTMail("john@example.com")).thenReturn(tenant);
		when(bookingRepository.existsByTenantIdAndRequestStatusIn(1L, List.of("PENDING", "APPROVED"))).thenReturn(true);

		String result = tenantService.requestBooking("john@example.com", 101, "2025-02-10", "2025-02-15");

		assertThat(result).isEqualTo("You already have an active booking (Pending/Approved).");
		verify(bookingRepository, never()).save(any(Booking.class));
	}
}
