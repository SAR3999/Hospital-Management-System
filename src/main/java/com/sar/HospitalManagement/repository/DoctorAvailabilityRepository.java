package com.sar.HospitalManagement.repository;

import com.sar.HospitalManagement.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    // ❌ Check if doctor is unavailable on a specific date
    boolean existsByDoctorIdAndUnavailableDate(Long doctorId, LocalDate date);


    // 📅 Get all unavailable dates of a doctor
    List<DoctorAvailability> findByDoctorIdOrderByUnavailableDateAsc(Long doctorId);


    // ❌ Remove unavailable date (doctor becomes available again)
    void deleteByDoctorIdAndUnavailableDate(Long doctorId, LocalDate date);


    // 🔍 Get unavailable dates in range (optional for analytics)
    List<DoctorAvailability> findByDoctorIdAndUnavailableDateBetween(
            Long doctorId,
            LocalDate startDate,
            LocalDate endDate
    );
}