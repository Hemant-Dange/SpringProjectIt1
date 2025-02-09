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
import com.pgms.pgmanage.repository.BookingRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking approvedBooking;
    private final Long TENANT_ID = 1L;
    private final Long BOOKING_ID = 100L;

    @BeforeEach
    void setUp() {
        approvedBooking = new Booking();
        approvedBooking.setId(BOOKING_ID);
        approvedBooking.setRequestStatus("APPROVED");
    }

    // ✅ Test Active Booking Check
    @Test
    void testHasActiveBooking_WhenActiveExists() {
        when(bookingRepository.existsByTenantIdAndRequestStatusIn(TENANT_ID, List.of("PENDING", "APPROVED"))).thenReturn(true);

        boolean result = bookingService.hasActiveBooking(TENANT_ID);

        assertThat(result).isTrue();
        verify(bookingRepository, times(1)).existsByTenantIdAndRequestStatusIn(TENANT_ID, List.of("PENDING", "APPROVED"));
    }

    @Test
    void testHasActiveBooking_WhenNoActiveExists() {
        when(bookingRepository.existsByTenantIdAndRequestStatusIn(TENANT_ID, List.of("PENDING", "APPROVED"))).thenReturn(false);

        boolean result = bookingService.hasActiveBooking(TENANT_ID);

        assertThat(result).isFalse();
        verify(bookingRepository, times(1)).existsByTenantIdAndRequestStatusIn(TENANT_ID, List.of("PENDING", "APPROVED"));
    }

    // ✅ Test Get Approved Booking
    @Test
    void testGetApprovedBooking_WhenBookingExists() {
        when(bookingRepository.findByTenantIdAndRequestStatus(TENANT_ID, "APPROVED")).thenReturn(approvedBooking);

        Booking result = bookingService.getApprovedBooking(TENANT_ID);

        assertThat(result).isNotNull();
        assertThat(result.getRequestStatus()).isEqualTo("APPROVED");
        verify(bookingRepository, times(1)).findByTenantIdAndRequestStatus(TENANT_ID, "APPROVED");
    }

    @Test
    void testGetApprovedBooking_WhenNoBookingExists() {
        when(bookingRepository.findByTenantIdAndRequestStatus(TENANT_ID, "APPROVED")).thenReturn(null);

        Booking result = bookingService.getApprovedBooking(TENANT_ID);

        assertThat(result).isNull();
        verify(bookingRepository, times(1)).findByTenantIdAndRequestStatus(TENANT_ID, "APPROVED");
    }

    // ✅ Test Delete Booking
    @Test
    void testDeleteBooking_WhenBookingExists() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(approvedBooking));

        boolean result = bookingService.deleteBooking(BOOKING_ID);

        assertThat(result).isTrue();
        verify(bookingRepository, times(1)).findById(BOOKING_ID);
        verify(bookingRepository, times(1)).delete(approvedBooking);
    }

    @Test
    void testDeleteBooking_WhenBookingDoesNotExist() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        boolean result = bookingService.deleteBooking(BOOKING_ID);

        assertThat(result).isFalse();
        verify(bookingRepository, times(1)).findById(BOOKING_ID);
        verify(bookingRepository, never()).delete(any(Booking.class));
    }
}
