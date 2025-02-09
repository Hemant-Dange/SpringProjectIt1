package com.pgms.pgmanage.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pgms.pgmanage.entity.Booking;
import com.pgms.pgmanage.entity.Manager;
import com.pgms.pgmanage.entity.Room;
import com.pgms.pgmanage.entity.Tenant;
import com.pgms.pgmanage.repository.BookingRepository;
import com.pgms.pgmanage.repository.ManagerRepository;
import com.pgms.pgmanage.repository.RoomRepository;
import com.pgms.pgmanage.repository.TenantRepository;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

	@Mock
	private ManagerRepository managerRepository;

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private RoomRepository roomRepository;

	@Mock
	private TenantRepository tenantRepository;

	@InjectMocks
	private ManagerService managerService;

	private Manager manager;
	private Tenant tenant;
	private Room room;
	private Booking booking;

	@BeforeEach
	void setUp() {
		manager = new Manager();
		manager.setEmail("admin@pgms.com");
		manager.setPassword("securepass");

		tenant = new Tenant();
		tenant.setId(1L);
		tenant.setUsername("JohnDoe");

		room = new Room(101, true, false);

		booking = new Booking();
		booking.setId(1L);
		booking.setTenant(tenant);
		booking.setRoom(room);
		booking.setRequestStatus("PENDING");
	}

	// ✅ Test Manager Login Validation
	@Test
	void testValidateManager_Success() {
		when(managerRepository.findByEmail("admin@pgms.com")).thenReturn(manager);

		Manager result = managerService.validateManager("admin@pgms.com", "securepass");

		assertThat(result).isNotNull();
		assertThat(result.getEmail()).isEqualTo("admin@pgms.com");
		verify(managerRepository, times(1)).findByEmail("admin@pgms.com");
	}

	@Test
	void testValidateManager_Failure() {
		when(managerRepository.findByEmail("wrong@pgms.com")).thenReturn(null);

		Manager result = managerService.validateManager("wrong@pgms.com", "securepass");

		assertThat(result).isNull();
		verify(managerRepository, times(1)).findByEmail("wrong@pgms.com");
	}

	// ✅ Test Fetching All Rooms
	@Test
	void testGetAllRooms() {
		when(roomRepository.findAll()).thenReturn(List.of(room));

		List<Room> rooms = managerService.getAllRooms();

		assertThat(rooms).hasSize(1);
		assertThat(rooms.get(0).getRoomNo()).isEqualTo(101);
		verify(roomRepository, times(1)).findAll();
	}

	// ✅ Test Fetching Pending Bookings
	@Test
	void testGetPendingBookings() {
		when(bookingRepository.findByRequestStatus("PENDING")).thenReturn(List.of(booking));

		List<Booking> pendingBookings = managerService.getPendingBookings();

		assertThat(pendingBookings).hasSize(1);
		assertThat(pendingBookings.get(0).getRequestStatus()).isEqualTo("PENDING");
		verify(bookingRepository, times(1)).findByRequestStatus("PENDING");
	}

	// ✅ Test Approving Booking
	@Test
	void testApproveBooking_Success() {
		when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
		when(bookingRepository.findByTenantIdAndRequestStatus(tenant.getId(), "APPROVED")).thenReturn(null);

		managerService.approveBooking(1L);

		assertThat(booking.getRequestStatus()).isEqualTo("APPROVED");
		verify(bookingRepository, times(1)).save(booking);
		verify(roomRepository, times(1)).save(room);
	}

	@Test
	void testApproveBooking_Fails_TenantAlreadyHasBooking() {
		when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
		when(bookingRepository.findByTenantIdAndRequestStatus(tenant.getId(), "APPROVED")).thenReturn(booking);

		assertThatThrownBy(() -> managerService.approveBooking(1L)).isInstanceOf(IllegalStateException.class)
				.hasMessage("Tenant already has an approved booking!");

		verify(bookingRepository, never()).save(any(Booking.class));
		verify(roomRepository, never()).save(any(Room.class));
	}

	// ✅ Test Rejecting Booking
	@Test
	void testRejectBooking_Success() {
		when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

		managerService.rejectBooking(1L);

		assertThat(booking.getRequestStatus()).isEqualTo("REJECTED");
		verify(bookingRepository, times(1)).save(booking);
	}

	// ✅ Test Adding a Room
	@Test
	void testAddRoom_Success() {
		when(roomRepository.existsById(101)).thenReturn(false);

		managerService.addRoom(101, true);

		verify(roomRepository, times(1)).save(any(Room.class));
	}

	@Test
	void testAddRoom_Fails_RoomAlreadyExists() {
		when(roomRepository.existsById(101)).thenReturn(true);

		assertThatThrownBy(() -> managerService.addRoom(101, true)).isInstanceOf(IllegalStateException.class)
				.hasMessage("Room number 101 already exists!");

		verify(roomRepository, never()).save(any(Room.class));
	}

	// ✅ Test Removing a Room
	@Test
	void testRemoveRoom_Success() {
		when(roomRepository.findById(101)).thenReturn(Optional.of(room));
		when(bookingRepository.existsByRoomAndRequestStatus(room, "APPROVED")).thenReturn(false);

		managerService.removeRoom(101);

		verify(roomRepository, times(1)).delete(room);
	}

	@Test
	void testRemoveRoom_Fails_RoomOccupied() {
		when(roomRepository.findById(101)).thenReturn(Optional.of(room));
		when(bookingRepository.existsByRoomAndRequestStatus(room, "APPROVED")).thenReturn(true);

		assertThatThrownBy(() -> managerService.removeRoom(101)).isInstanceOf(IllegalStateException.class)
				.hasMessage("Cannot delete room as it is occupied by a tenant.");

		verify(roomRepository, never()).delete(any(Room.class));
	}
}
