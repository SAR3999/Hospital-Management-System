package com.sar.HospitalManagement.service;

import com.sar.HospitalManagement.entity.Doctor;
import com.sar.HospitalManagement.entity.DoctorAvailability;
import com.sar.HospitalManagement.entity.User;
import com.sar.HospitalManagement.entity.type.RoleType;
import com.sar.HospitalManagement.repository.DoctorAvailabilityRepository;
import com.sar.HospitalManagement.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityService {

    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    @PreAuthorize("hasRole('ADMIN') OR hasRole('DOCTOR')")
    public void markUnavailable(Long doctorId, LocalDate date){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(user.getRoles().contains(RoleType.DOCTOR) && !user.getId().equals(doctorId)){
            throw new RuntimeException("You can only update your own availability");
        }

        if(doctorAvailabilityRepository.existsByDoctorIdAndUnavailableDate(doctorId, date)){
            throw new RuntimeException("Already marked unavailable");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DoctorAvailability availability = DoctorAvailability.builder()
                .doctor(doctor)
                .unavailableDate(date)
                .build();

        doctorAvailabilityRepository.save(availability);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') OR hasRole('DOCTOR')")
    public void removeUnavailable(Long doctorId, LocalDate date){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Ownership check
        if(user.getRoles().contains(RoleType.DOCTOR) && !user.getId().equals(doctorId)){
            throw new RuntimeException("You can only update your own availability");
        }

        doctorAvailabilityRepository.deleteByDoctorIdAndUnavailableDate(doctorId, date);
    }
}