package com.sar.HospitalManagement.entity;

import com.sar.HospitalManagement.entity.type.BloodGroupType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
/*Lombok*/
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name="patient",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_patient_email", columnNames = {"email"}),
                @UniqueConstraint(name = "unique_patient_name_with_birthdate", columnNames = {"name","birthDate"})
        },
        indexes = {
                @Index(name = "idx_patient_birthdate", columnList = "birthDate")    /*Makes the searching faster*/
                /*
                * For Multiple
                * @Index(name = "idx_patient_name_birthdate", columnList = "name, birthDate")
                * */
        }
)
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String gender;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BloodGroupType bloodGroup;

    @OneToOne
    @MapsId
    private User user;

//    @ToString.Exclude
    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.LAZY) /*CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE*/
    @JoinColumn(
            name = "insurance_id",
            /*
            * Here we are just giving name to the constraint.
            * If we don't use it, it will create foreign key,
            * but it gives a constraint name like that "FKkivfb7y0wti16f30erw48cjfi", which is hard to find or dubbing/testing
            */
            foreignKey = @ForeignKey(name = "fk_patient_insurance")
    )  // JoinColumn is owning side
    private Insurance insurance;

    @ToString.Exclude
    @OneToMany(mappedBy = "patient", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();

/* Priority this if we used both
    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
*/
}
