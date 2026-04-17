package com.sar.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_doctor_unavailable_date",
                        columnNames = {"doctor_id", "unavailableDate"}
                )
        }
)
public class DoctorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate unavailableDate;

    @ManyToOne
    @JoinColumn(
            name = "doctor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_availability_doctor")
    )
    private Doctor doctor;
}
