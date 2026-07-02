package project.hotelreservationsystem.dto;

import lombok.Data;

@Data
public class PaymentDto {
    private Integer reservationId;
    private Double amount;
    private String paymentMethod;
    private String paymentStatus;
}