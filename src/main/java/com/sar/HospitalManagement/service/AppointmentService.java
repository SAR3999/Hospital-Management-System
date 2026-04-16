package com.sar.HospitalManagement.service;

import com.sar.HospitalManagement.entity.Appointment;
import com.sar.HospitalManagement.entity.Doctor;
import com.sar.HospitalManagement.entity.Patient;
import com.sar.HospitalManagement.repository.AppointmentRepository;
import com.sar.HospitalManagement.repository.DoctorRepository;
import com.sar.HospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentService {
    private AppointmentRepository appointmentRepository;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public Appointment createNewAppointment(Appointment appointment, Long patientId, Long doctorId){
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new EntityNotFoundException("Doctor with doctor id is not stored in our system"));
        Patient patient = patientRepository.findById(patientId).orElseThrow( () -> new EntityNotFoundException("Patient with patient id is not stord in our system") );

        if(appointment.getId() != null )
            throw new IllegalArgumentException("Appointment should not have appointment id");

        if(appointment.getAppointmentTime().isBefore(LocalDateTime.now()))
            throw new RuntimeException("We can't schedule appointment in the past");

        if(appointmentRepository.existsByDoctorIdAndAppointmentTime(doctor.getId(), appointment.getAppointmentTime()))
            throw new RuntimeException("Doctor already have an appointment on this time");

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        // for maintaining bi-directional mapping which is used to consist the memory (not required for db)
        patient.getAppointments().add(appointment);
        doctor.getAppointments().add(appointment);

        return appointmentRepository.save(appointment);
    }
}
