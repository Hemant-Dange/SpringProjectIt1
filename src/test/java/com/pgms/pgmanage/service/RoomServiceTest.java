package com.pgms.pgmanage.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pgms.pgmanage.entity.Room;
import com.pgms.pgmanage.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

	@Mock
	private RoomRepository roomRepository;

	@InjectMocks
	private RoomService roomService;

	private Room room1, room2;

	@BeforeEach
	void setUp() {
		room1 = new Room(101, true, false); // Vacant AC Room
		room2 = new Room(102, false, false); // Vacant Non-AC Room
	}

	// ✅ Test Fetching Available Rooms
	@Test
	void testGetAvailableRooms_Success() {
		when(roomRepository.findByStatus(false)).thenReturn(List.of(room1, room2));

		List<Room> availableRooms = roomService.getAvailableRooms();

		assertThat(availableRooms).hasSize(2);
		assertThat(availableRooms.get(0).getRoomNo()).isEqualTo(101);
		assertThat(availableRooms.get(1).getRoomNo()).isEqualTo(102);

		verify(roomRepository, times(1)).findByStatus(false);
	}

	@Test
	void testGetAvailableRooms_EmptyList() {
		when(roomRepository.findByStatus(false)).thenReturn(List.of());

		List<Room> availableRooms = roomService.getAvailableRooms();

		assertThat(availableRooms).isEmpty();
		verify(roomRepository, times(1)).findByStatus(false);
	}
}
