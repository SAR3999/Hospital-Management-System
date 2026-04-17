package com.sar.HospitalManagement.service;

import com.sar.HospitalManagement.dto.AppointmentResponseDto;
import com.sar.HospitalManagement.dto.CreateAppointmentRequestDto;
import com.sar.HospitalManagement.entity.Appointment;
import com.sar.HospitalManagement.entity.Doctor;
import com.sar.HospitalManagement.entity.Patient;
import com.sar.HospitalManagement.entity.User;
import com.sar.HospitalManagement.entity.type.AppointmentStatusType;
import com.sar.HospitalManagement.repository.AppointmentRepository;
import com.sar.HospitalManagement.repository.DoctorAvailabilityRepository;
import com.sar.HospitalManagement.repository.DoctorRepository;
import com.sar.HospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Transactional
    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentResponseDto createNewAppointment(CreateAppointmentRequestDto dto){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        LocalDate today = LocalDate.now();
        Long doctorId = doctor.getId();
        if(doctorAvailabilityRepository.existsByDoctorIdAndUnavailableDate(doctorId, today)){
            throw new RuntimeException("Doctor is not available on this date");
        }

        Integer lastToken = appointmentRepository
                .findMaxToken(doctor.getId(), today);

        int newToken = lastToken + 1;

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDate(today)
                .tokenNumber(newToken)
                .status(AppointmentStatusType.SCHEDULED)
                .reason(dto.getReason())
                .build();

        return modelMapper.map(appointmentRepository.save(appointment), AppointmentResponseDto.class);
    }

    @PreAuthorize("""
    hasRole('ADMIN') OR
    (hasRole('DOCTOR') AND #doctorId == authentication.principal.id) OR
    hasRole('PATIENT')
    """)
    public List<AppointmentResponseDto> getQueue(Long doctorId){

        List<Appointment> list = appointmentRepository
                .findByDoctorIdAndAppointmentDateOrderByTokenNumberAsc(
                        doctorId, LocalDate.now()
                );

        return list.stream()
                .map(a -> modelMapper.map(a, AppointmentResponseDto.class))
                .toList();
    }

    @PreAuthorize("hasRole('PATIENT')")
    public Integer getMyPosition(Long appointmentId){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));

        if(!appointment.getPatient().getId().equals(user.getId())){
            throw new RuntimeException("Unauthorized access");
        }

        List<Appointment> queue = appointmentRepository
                .findByDoctorIdAndAppointmentDateOrderByTokenNumberAsc(
                        appointment.getDoctor().getId(),
                        LocalDate.now()
                );

        for(int i=0;i<queue.size();i++){
            if(queue.get(i).getId().equals(appointmentId)){
                return i+1;
            }
        }

        return -1;
    }

    @Transactional
    @PreAuthorize("hasRole('DOCTOR')")
    public AppointmentResponseDto updateStatus(Long appointmentId, AppointmentStatusType status){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if(!appointment.getDoctor().getId().equals(user.getId())){
            throw new RuntimeException("You can only manage your own appointments");
        }

        appointment.setStatus(status);

        return modelMapper.map(appointment, AppointmentResponseDto.class);
    }

//    @Transactional
//    @PreAuthorize("hasAuthority('appointment:write') or #doctorId == authentication.principal.id")
//    public Appointment reAssignAppointmentToAnotherDoctor(Long appointmentId, Long doctorId) {
//        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
//        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
//
//        appointment.setDoctor(doctor); // this will automatically call the update, because it is dirty
//
//        doctor.getAppointments().add(appointment); // just for bidirectional consistency
//
//        return appointment;
//    }

    @PreAuthorize("hasRole('ADMIN') OR (hasRole('DOCTOR') AND #doctorId == authentication.principal.id)")
    public List<AppointmentResponseDto> getAllAppointmentsOfDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();

        return doctor.getAppointments()
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentResponseDto.class))
                .collect(Collectors.toList());
    }
}
