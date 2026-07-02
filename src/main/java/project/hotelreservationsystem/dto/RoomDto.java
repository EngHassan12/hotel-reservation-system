package project.hotelreservationsystem.dto;

import lombok.Data;

@Data
public class RoomDto {
    private String roomNumber;
    private String roomType;
    private Double pricePerNight;
    private Integer capacity;
    private String status;
}