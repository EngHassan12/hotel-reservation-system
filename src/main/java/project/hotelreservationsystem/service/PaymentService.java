package project.hotelreservationsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import project.hotelreservationsystem.dto.PaymentDto;
import project.hotelreservationsystem.entity.Payment;
import project.hotelreservationsystem.entity.Reservation;
import project.hotelreservationsystem.exception.ResourceNotFoundException;
import project.hotelreservationsystem.repository.PaymentRepository;
import project.hotelreservationsystem.repository.ReservationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public Payment createPayment(PaymentDto dto) {
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isOwner = reservation.getCustomer().getEmail().equals(email);
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not allowed to pay for this reservation");
        }

        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .paymentStatus("COMPLETED")
                .build();

        Payment saved = paymentRepository.save(payment);

        reservation.setReservationStatus("CONFIRMED");
        reservationRepository.save(reservation);

        return saved;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    public List<Payment> getPaymentsByReservation(Integer reservationId) {
        return paymentRepository.findByReservation_ReservationId(reservationId);
    }

    public Payment updatePaymentStatus(Integer id, String status) {
        Payment payment = getPaymentById(id);
        payment.setPaymentStatus(status);
        return paymentRepository.save(payment);
    }

    public void deletePayment(Integer id) {
        Payment payment = getPaymentById(id);
        paymentRepository.delete(payment);
    }
}