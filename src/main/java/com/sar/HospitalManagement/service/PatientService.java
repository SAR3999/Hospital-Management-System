package com.sar.HospitalManagement.service;

import com.sar.HospitalManagement.dto.PatientResponseDto;
import com.sar.HospitalManagement.entity.Patient;
import com.sar.HospitalManagement.entity.User;
import com.sar.HospitalManagement.entity.type.RoleType;
import com.sar.HospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {
    private PatientRepository patientRepository;
    private ModelMapper modelMapper;

    @Autowired
    public PatientService(PatientRepository patientRepository, ModelMapper modelMapper){
        this.patientRepository = patientRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public PatientResponseDto getPatientById(Long patientId){
        Patient patient = patientRepository.findById(patientId).orElseThrow(
                ()-> new EntityNotFoundException("Patient not Found with id: "+patientId)
        );
        return modelMapper.map(patient, PatientResponseDto.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<PatientResponseDto> getAllPatients(int page, int size){

        return patientRepository.findAll(PageRequest.of(page, size))
                .map(patient -> modelMapper.map(patient, PatientResponseDto.class));
    }
//    public Patient savePatient(Patient patient){
//        return patientRepository.save(patient);
//    }

    @PreAuthorize("hasRole('PATIENT')")
    public PatientResponseDto getMyProfile(){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        return modelMapper.map(patient, PatientResponseDto.class);
    }
}
