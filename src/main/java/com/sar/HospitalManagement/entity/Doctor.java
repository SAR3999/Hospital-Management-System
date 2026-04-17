package com.sar.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
    @Builder makes more readable while creating an object as follows
    Appointment appointment = Appointment.builder()
        .appointmentTime(LocalDateTime.now())
        .reason("Fever")
        .status(AppointmentStatusType.ONGOING)
        .build();
*/
@Builder
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String specialization;

    @OneToOne
    @MapsId
    private User user;

//    @Column(nullable = false)
//    private String address;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ToString.Exclude
    @OneToMany(mappedBy = "doctor", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();

    @ToString.Exclude
    @ManyToMany(mappedBy = "doctors")
    private List<Department> departments = new ArrayList<>();
}
