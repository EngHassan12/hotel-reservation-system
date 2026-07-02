package project.hotelreservationsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private Double amount;

    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private String paymentMethod; // CASH, CARD, ONLINE

    @Column(nullable = false)
    private String paymentStatus; // PENDING, COMPLETED, REFUNDED

    @PrePersist
    public void prePersist() {
        this.paymentDate = LocalDateTime.now();
    }
}