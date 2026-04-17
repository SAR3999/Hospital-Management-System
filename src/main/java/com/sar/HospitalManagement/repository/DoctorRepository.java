package com.sar.HospitalManagement.repository;

import com.sar.HospitalManagement.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Page<Doctor> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Doctor> findBySpecializationContainingIgnoreCase(String specialization, Pageable pageable);

    Page<Doctor> findByNameContainingIgnoreCaseAndSpecializationContainingIgnoreCase(
            String name,
            String specialization,
            Pageable pageable
    );
}