package com.sar.HospitalManagement.entity;

import com.sar.HospitalManagement.entity.type.AppointmentStatusType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_doctor_time" ,
                        columnNames = {"appointmentTime","doctor_id"}
                )
        }
)
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate appointmentDate;

    @Column(nullable = false)
    private Integer tokenNumber;

    private LocalDateTime appointmentTime;

    @Column(length = 200)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatusType status;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "patient_id",
            foreignKey = @ForeignKey(name = "fk_patient_id")
    )
    private Patient patient;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "doctor_id",
            foreignKey = @ForeignKey(name = "fk_doctor_id")
    )
    private Doctor doctor;
}
