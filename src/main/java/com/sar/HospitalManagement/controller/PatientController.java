package com.sar.HospitalManagement.controller;

import com.sar.HospitalManagement.dto.AppointmentResponseDto;
import com.sar.HospitalManagement.dto.CreateAppointmentRequestDto;
import com.sar.HospitalManagement.dto.PatientResponseDto;
import com.sar.HospitalManagement.service.AppointmentService;
import com.sar.HospitalManagement.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponseDto> createNewAppointment(@RequestBody CreateAppointmentRequestDto createAppointmentRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createNewAppointment(createAppointmentRequestDto));
    }

//    @GetMapping("/profile")
//    private ResponseEntity<PatientResponseDto> getPatientProfile() {
//        Long patientId = 4L;
//        return ResponseEntity.ok(patientService.getPatientById(patientId));
//    }

    @GetMapping("/queue/{doctorId}")
    public ResponseEntity<List<AppointmentResponseDto>> getQueue(@PathVariable Long doctorId){
        return ResponseEntity.ok(appointmentService.getQueue(doctorId));
    }

    @GetMapping("/position/{appointmentId}")
    public ResponseEntity<Integer> getPosition(@PathVariable Long appointmentId){
        return ResponseEntity.ok(appointmentService.getMyPosition(appointmentId));
    }
}
