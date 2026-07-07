package project.hotelreservationsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import project.hotelreservationsystem.dto.ReservationDto;
import project.hotelreservationsystem.entity.Customer;
import project.hotelreservationsystem.entity.Reservation;
import project.hotelreservationsystem.entity.Room;
import project.hotelreservationsystem.exception.ResourceNotFoundException;
import project.hotelreservationsystem.repository.CustomerRepository;
import project.hotelreservationsystem.repository.ReservationRepository;
import project.hotelreservationsystem.repository.RoomRepository;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;

    // Wuxuu ka helayaa Customer-ka la xiriira user-ka hadda login-ka ah (isla email-ka)
    private Customer getCurrentCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for this account"));
    }

    public Reservation createReservation(ReservationDto dto) {
        Customer customer = getCurrentCustomer();

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (!"AVAILABLE".equals(room.getStatus())) {
            throw new IllegalStateException("Room is not available for booking");
        }

        long nights = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        if (nights <= 0) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        double totalAmount = room.getPricePerNight() * nights;

        Reservation reservation = Reservation.builder()
                .customer(customer)
                .room(room)
                .checkInDate(dto.getCheckInDate())
                .checkOutDate(dto.getCheckOutDate())
                .numberOfGuests(dto.getNumberOfGuests())
                .reservationStatus("PENDING")
                .totalAmount(totalAmount)
                .build();

        room.setStatus("OCCUPIED");
        roomRepository.save(room);

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getMyReservations() {
        Customer customer = getCurrentCustomer();
        return reservationRepository.findByCustomer_CustomerId(customer.getCustomerId());
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Integer id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
    }

    public Reservation updateStatus(Integer id, String status) {
        Reservation reservation = getReservationById(id);
        reservation.setReservationStatus(status);

        if (status.equals("CHECKED_OUT") || status.equals("CANCELLED")) {
            reservation.getRoom().setStatus("AVAILABLE");
            roomRepository.save(reservation.getRoom());
        }

        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Integer id) {
        Reservation reservation = getReservationById(id);
        reservationRepository.delete(reservation);
    }
}