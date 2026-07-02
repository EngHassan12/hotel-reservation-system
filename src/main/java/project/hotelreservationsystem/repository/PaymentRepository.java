package project.hotelreservationsystem.repository;

import project.hotelreservationsystem.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByReservation_ReservationId(Integer reservationId);
    List<Payment> findByPaymentStatus(String status);
}