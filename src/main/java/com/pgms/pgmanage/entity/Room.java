package com.pgms.pgmanage.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    private int roomNo; // Primary Key, starts from 101

    private boolean type; // true = AC, false = Non-AC
    private boolean status; // true = Occupied, false = Vacant

    // ✅ One-to-Many Relationship: A Room can have multiple Bookings
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    // ✅ Default Constructor
    public Room() {}

    // ✅ Parameterized Constructor
    public Room(int roomNo, boolean type, boolean status) {
        this.roomNo = roomNo;
        this.type = type;
        this.status = status;
    }

    // ✅ Getters & Setters
    public int getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public String toString() {
        return "Room [roomNo=" + roomNo + ", type=" + (type ? "AC" : "Non-AC") + 
               ", status=" + (status ? "Occupied" : "Vacant") + "]";
    }
}
