package com.pgms.pgmanage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import com.pgms.pgmanage.entity.Room;
import com.pgms.pgmanage.repository.RoomRepository;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus(false); // false = vacant
    }
}

