package com.pgms.pgmanage.entity;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // Tenant ID
    private int roomNo; // Room Number(make it unique)

    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private String requestStatus; // PENDING, APPROVED, REJECTED

    // ✅ Default Constructor
    public Booking() {}

    // ✅ Parameterized Constructor
    public Booking(Long userId, int roomNo, LocalDate checkinDate, LocalDate checkoutDate, String requestStatus) {
        this.userId = userId;
        this.roomNo = roomNo;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        this.requestStatus = requestStatus;
    }

    // ✅ Getters and Setters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
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
        return "Booking [id=" + id + ", userId=" + userId + ", roomNo=" + roomNo +
                ", checkinDate=" + checkinDate + ", checkoutDate=" + checkoutDate +
                ", requestStatus=" + requestStatus + "]";
    }
}
