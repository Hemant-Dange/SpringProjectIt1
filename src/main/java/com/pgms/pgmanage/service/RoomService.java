package com.pgms.pgmanage.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.pgms.pgmanage.entity.Room;
import com.pgms.pgmanage.repository.RoomRepository;

@Service
public class RoomService {

	private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

	@Autowired
	private RoomRepository roomRepository;

	public List<Room> getAvailableRooms() {
		logger.info("Fetching all available (vacant) rooms...");
		List<Room> availableRooms = roomRepository.findByStatus(false);

		if (availableRooms.isEmpty()) {
			logger.warn("No available rooms found!");
		} else {
			logger.info("Found {} available rooms.", availableRooms.size());
		}

		return availableRooms;
	}
}
