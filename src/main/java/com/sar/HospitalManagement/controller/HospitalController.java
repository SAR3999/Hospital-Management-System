package com.sar.HospitalManagement.controller;

import com.sar.HospitalManagement.dto.DoctorResponseDto;
import com.sar.HospitalManagement.service.AdminAnalyticsService;
import com.sar.HospitalManagement.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class HospitalController {

    private final DoctorService doctorService;
    private final AdminAnalyticsService analyticsService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/doctors")
    public ResponseEntity<Page<DoctorResponseDto>> getAllDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(
                doctorService.getAllDoctors(page, size)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/doctors/search")
    public ResponseEntity<Page<DoctorResponseDto>> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(
                doctorService.searchDoctors(name, specialization, page, size)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/analytics/today")
    public ResponseEntity<Map<String, Long>> getTodayStats(){

        Map<String, Long> response = Map.of(
                "completed", analyticsService.getTodayCompleted(),
                "cancelled", analyticsService.getTodayCancelled()
        );

        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/analytics/last-7-days")
    public ResponseEntity<Long> getLast7DaysCompleted(){
        return ResponseEntity.ok(analyticsService.getLast7DaysCompleted());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/analytics/last-month")
    public ResponseEntity<Long> getLastMonthCompleted(){
        return ResponseEntity.ok(analyticsService.getLastMonthCompleted());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/analytics/last-6-months")
    public ResponseEntity<Long> getLast6MonthsCompleted(){
        return ResponseEntity.ok(analyticsService.getLast6MonthsCompleted());
    }

    // everything in one api
    @GetMapping("/analytics/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(){

        LocalDate today = LocalDate.now();

        Map<String, Object> response = Map.of(
                "todayCompleted", analyticsService.getTodayCompleted(),
                "todayCancelled", analyticsService.getTodayCancelled(),
                "last7DaysCompleted", analyticsService.getLast7DaysCompleted(),
                "lastMonthCompleted", analyticsService.getLastMonthCompleted()
        );

        return ResponseEntity.ok(response);
    }
}
