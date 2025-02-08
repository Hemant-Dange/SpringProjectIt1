package com.pgms.pgmanage.entity;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  
    private Tenant tenant; // A tenant can have multiple bookings

    @ManyToOne
    @JoinColumn(name = "room_no", nullable = false) // Many bookings can refer to one room
    private Room room;

    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private String requestStatus; // PENDING, APPROVED, REJECTED

    // Default Constructor
    public Booking() {}

    // Parameterized Constructor
    public Booking(Tenant tenant, Room room, LocalDate checkinDate, LocalDate checkoutDate, String requestStatus) {
        this.tenant = tenant;
        this.room = room;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        this.requestStatus = requestStatus;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDate checkinDate) {
        this.checkinDate = checkinDate;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDate checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public String toString() {
        return "Booking [id=" + id + ", tenant=" + tenant + ", room=" + room + 
               ", checkinDate=" + checkinDate + ", checkoutDate=" + checkoutDate + 
               ", requestStatus=" + requestStatus + "]";
    }
}
