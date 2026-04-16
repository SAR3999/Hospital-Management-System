package com.sar.HospitalManagement.service;

import com.sar.HospitalManagement.entity.Insurance;
import com.sar.HospitalManagement.entity.Patient;
import com.sar.HospitalManagement.repository.InsuranceRepository;
import com.sar.HospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InsuranceService {
    private InsuranceRepository insuranceRepository;
    private PatientRepository patientRepository;

    @Autowired
    public InsuranceService(InsuranceRepository insuranceRepository, PatientRepository patientRepository){
        this.insuranceRepository = insuranceRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public Patient assignInsuranceToPatient(Long patientId, Insurance insurance){
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(()->new EntityNotFoundException("Patient not found with ID : "+patientId));
        patient.setInsurance(insurance);

        // For maintaining by bi-direction consistency
        insurance.setPatient(patient);

        return patient;
    }

    @Transactional
    public Patient disaccociateInsuranceFromPatient(Long patientId){
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Patient not found with id: "+patientId)
                );
        patient.setInsurance(null);
        return patient;
    }
}
