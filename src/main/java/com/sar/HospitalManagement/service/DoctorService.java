package com.sar.HospitalManagement.service;

import com.sar.HospitalManagement.dto.DoctorResponseDto;
import com.sar.HospitalManagement.dto.OnBoardDoctorRequestDto;
import com.sar.HospitalManagement.entity.Doctor;
import com.sar.HospitalManagement.entity.User;
import com.sar.HospitalManagement.entity.type.RoleType;
import com.sar.HospitalManagement.repository.DoctorRepository;
import com.sar.HospitalManagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    // ✅ PUBLIC (no restriction needed if controller is public)
    public Page<DoctorResponseDto> getAllDoctors(int page, int size){

        Pageable pageable = PageRequest.of(page, size);

        return doctorRepository.findAll(pageable)
                .map(doctor -> modelMapper.map(doctor, DoctorResponseDto.class));
    }

    // 🔐 ONLY ADMIN CAN ADD DOCTOR
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public DoctorResponseDto onBoardNewDoctor(OnBoardDoctorRequestDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(doctorRepository.existsById(dto.getUserId())) {
            throw new IllegalArgumentException("Already a doctor");
        }

        Doctor doctor = Doctor.builder()
                .name(dto.getName())
                .specialization(dto.getSpecialization())
                .user(user)
                .build();

        // ✅ Assign DOCTOR role
        user.getRoles().add(RoleType.DOCTOR);

        return modelMapper.map(doctorRepository.save(doctor), DoctorResponseDto.class);
    }

    // 🔍 PUBLIC SEARCH (no restriction needed)
    public Page<DoctorResponseDto> searchDoctors(
            String name,
            String specialization,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        if (name != null && specialization != null) {
            return doctorRepository
                    .findByNameContainingIgnoreCaseAndSpecializationContainingIgnoreCase(
                            name, specialization, pageable
                    )
                    .map(d -> modelMapper.map(d, DoctorResponseDto.class));

        } else if (name != null) {
            return doctorRepository
                    .findByNameContainingIgnoreCase(name, pageable)
                    .map(d -> modelMapper.map(d, DoctorResponseDto.class));

        } else if (specialization != null) {
            return doctorRepository
                    .findBySpecializationContainingIgnoreCase(specialization, pageable)
                    .map(d -> modelMapper.map(d, DoctorResponseDto.class));
        }

        return doctorRepository.findAll(pageable)
                .map(d -> modelMapper.map(d, DoctorResponseDto.class));
    }
}