package project.hotelreservationsystem.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationDto {
    private Integer customerId;
    private Integer roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private String reservationStatus;
    private Double totalAmount;
}