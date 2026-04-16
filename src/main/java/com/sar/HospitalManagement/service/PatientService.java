package com.sar.HospitalManagement.service;

import com.sar.HospitalManagement.dto.PatientResponseDto;
import com.sar.HospitalManagement.entity.Patient;
import com.sar.HospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
    public PatientResponseDto getPatientById(Long patientId){
        Patient patient = patientRepository.findById(patientId).orElseThrow(
                ()-> new EntityNotFoundException("Patient not Found with id: "+patientId)
        );
        return modelMapper.map(patient, PatientResponseDto.class);
    }

    public List<PatientResponseDto> getAllPatients(Integer pageNumber, Integer pageSize){
        return patientRepository.findAllPatients(PageRequest.of(pageNumber,pageSize))
                .stream()
                .map(patient -> modelMapper.map(patient, PatientResponseDto.class))
                .collect(Collectors.toList());
    }

//    public Patient savePatient(Patient patient){
//        return patientRepository.save(patient);
//    }
}
