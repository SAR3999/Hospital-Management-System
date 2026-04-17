package com.sar.HospitalManagement.controller;

import com.sar.HospitalManagement.dto.AppointmentResponseDto;
import com.sar.HospitalManagement.entity.User;
import com.sar.HospitalManagement.entity.type.AppointmentStatusType;
import com.sar.HospitalManagement.service.AppointmentService;
import com.sar.HospitalManagement.service.DoctorAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {
    private final AppointmentService appointmentService;
    private final DoctorAvailabilityService doctorAvailabilityService;
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointmentsOfDoctor() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(appointmentService.getAllAppointmentsOfDoctor(user.getId()));
    }

    @GetMapping("/queue/{doctorId}")
    public ResponseEntity<List<AppointmentResponseDto>> getQueue(@PathVariable Long doctorId){
        return ResponseEntity.ok(appointmentService.getQueue(doctorId));
    }

    @PatchMapping("/appointment/{id}/status")
    public ResponseEntity<AppointmentResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatusType status
    ){
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/unavailable")
    public ResponseEntity<String> markUnavailable(@RequestParam LocalDate date){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        doctorAvailabilityService.markUnavailable(user.getId(), date);

        return ResponseEntity.ok("Marked unavailable");
    }
}
