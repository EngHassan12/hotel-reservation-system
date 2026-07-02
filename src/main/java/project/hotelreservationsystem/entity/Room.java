package project.hotelreservationsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roomId;

    @Column(nullable = false, unique = true)
    private String roomNumber;

    @Column(nullable = false)
    private String roomType;

    @Column(nullable = false)
    private Double pricePerNight;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private String status; // AVAILABLE, OCCUPIED, MAINTENANCE
}