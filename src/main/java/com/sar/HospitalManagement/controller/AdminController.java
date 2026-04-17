package com.sar.HospitalManagement.controller;

import com.sar.HospitalManagement.dto.DoctorResponseDto;
import com.sar.HospitalManagement.dto.OnBoardDoctorRequestDto;
import com.sar.HospitalManagement.dto.PatientResponseDto;
import com.sar.HospitalManagement.entity.Patient;
import com.sar.HospitalManagement.service.DoctorService;
import com.sar.HospitalManagement.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private PatientService patientService;
    private DoctorService doctorService;

    @Autowired
    public AdminController(PatientService patientService, DoctorService doctorService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @GetMapping("/patients")
    public ResponseEntity<List<PatientResponseDto>> getAllPatients(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "3") int pageSize
    ){
        return ResponseEntity.ok(patientService.getAllPatients(pageNumber,pageSize));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/onBoardNewDoctor")
    public ResponseEntity<DoctorResponseDto> onBoardNewDoctor(@RequestBody OnBoardDoctorRequestDto onBoardDoctorRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.onBoardNewDoctor(onBoardDoctorRequestDto));
    }
}
