package project.hotelreservationsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import project.hotelreservationsystem.dto.ReservationDto;
import project.hotelreservationsystem.entity.Reservation;
import project.hotelreservationsystem.entity.Room;
import project.hotelreservationsystem.entity.User;
import project.hotelreservationsystem.exception.ResourceNotFoundException;
import project.hotelreservationsystem.repository.ReservationRepository;
import project.hotelreservationsystem.repository.RoomRepository;
import project.hotelreservationsystem.repository.UserRepository;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public Reservation createReservation(ReservationDto dto) {
        User customer = getCurrentUser();

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
        User customer = getCurrentUser();
        return reservationRepository.findByCustomer_UserId(customer.getUserId());
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