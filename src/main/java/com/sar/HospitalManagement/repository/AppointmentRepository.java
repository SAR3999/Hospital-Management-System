package com.sar.HospitalManagement.repository;

import com.sar.HospitalManagement.entity.Appointment;
import com.sar.HospitalManagement.entity.type.AppointmentStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctorIdAndAppointmentTime(Long doctorId, LocalDateTime appointmentTime);

    List<Appointment> findByDoctorIdAndAppointmentDateOrderByTokenNumberAsc(Long doctorId, LocalDate date);

    @Query("SELECT COALESCE(MAX(a.tokenNumber),0) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date")
    Integer findMaxToken(Long doctorId, LocalDate date);

    @Query("""
        SELECT a.tokenNumber
        FROM Appointment a
        WHERE a.doctor.id = :doctorId
        AND a.appointmentDate = :date
        AND a.status = 'ONGOING'
    """)
    Integer findCurrentServingToken(Long doctorId, LocalDate date);

    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);

    boolean existsByDoctorIdAndAppointmentDateAndStatus(
            Long doctorId,
            LocalDate date,
            AppointmentStatusType status
    );

    @Query("""
        SELECT COUNT(a)
        FROM Appointment a
        WHERE a.appointmentDate BETWEEN :startDate AND :endDate
        AND a.status = :status
    """)
    Long countByDateRangeAndStatus(
            LocalDate startDate,
            LocalDate endDate,
            AppointmentStatusType status
    );

    @Query("""
        SELECT COUNT(a)
        FROM Appointment a
        WHERE a.doctor.id = :doctorId
        AND a.status IN :statuses
        AND a.appointmentDate BETWEEN :startDate AND :endDate
    """)
    Long countAppointmentsForDoctor(
            Long doctorId,
            List<AppointmentStatusType> statuses,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusIn(
            Long doctorId,
            LocalDate date,
            List<AppointmentStatusType> statuses
    );

    @Query("""
        SELECT a
        FROM Appointment a
        WHERE a.doctor.id = :doctorId
        AND a.appointmentDate = :date
        AND a.status = 'SCHEDULED'
        ORDER BY a.tokenNumber ASC
    """)
    List<Appointment> findNextWaitingPatients(Long doctorId, LocalDate date);
}