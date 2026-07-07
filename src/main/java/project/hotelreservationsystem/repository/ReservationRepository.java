package project.hotelreservationsystem.repository;

import project.hotelreservationsystem.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByCustomer_CustomerId(Integer customerId);
    List<Reservation> findByRoom_RoomId(Integer roomId);
    List<Reservation> findByReservationStatus(String status);
}