package project.hotelreservationsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.hotelreservationsystem.dto.PaymentDto;
import project.hotelreservationsystem.entity.Payment;
import project.hotelreservationsystem.entity.Reservation;
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
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .paymentStatus(dto.getPaymentStatus() != null ? dto.getPaymentStatus() : "PENDING")
                .build();

        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
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