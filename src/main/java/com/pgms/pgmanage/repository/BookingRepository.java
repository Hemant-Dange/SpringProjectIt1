package com.pgms.pgmanage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pgms.pgmanage.entity.Booking;
import com.pgms.pgmanage.entity.Room;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByRequestStatus(String status);

    Booking findByTenantId(Long tenantId);  

    Booking findByTenantIdAndRequestStatus(Long tenantId, String requestStatus);
    
    boolean existsByTenantIdAndRequestStatusIn(Long tenantId, List<String> statuses);
    
    List<Booking> findByRoom(Room room);

    boolean existsByRoomAndRequestStatus(Room room, String requestStatus);
    
    List<Booking> findAllByOrderByIdDesc();
}
