package project.hotelreservationsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.hotelreservationsystem.dto.ReservationDto;
import project.hotelreservationsystem.entity.Customer;
import project.hotelreservationsystem.entity.Reservation;
import project.hotelreservationsystem.entity.Room;
import project.hotelreservationsystem.repository.CustomerRepository;
import project.hotelreservationsystem.repository.ReservationRepository;
import project.hotelreservationsystem.repository.RoomRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;

    public Reservation createReservation(ReservationDto dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Reservation reservation = Reservation.builder()
                .customer(customer)
                .room(room)
                .checkInDate(dto.getCheckInDate())
                .checkOutDate(dto.getCheckOutDate())
                .numberOfGuests(dto.getNumberOfGuests())
                .reservationStatus("PENDING")
                .totalAmount(dto.getTotalAmount())
                .build();

        room.setStatus("OCCUPIED");
        roomRepository.save(room);

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Integer id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
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