package com.sar.HospitalManagement.service;

import com.sar.HospitalManagement.entity.type.AppointmentStatusType;
import com.sar.HospitalManagement.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsService {

    private final AppointmentRepository appointmentRepository;

    public Long getTodayCompleted(){
        LocalDate today = LocalDate.now();

        return appointmentRepository.countByDateRangeAndStatus(
                today,
                today,
                AppointmentStatusType.COMPLETED
        );
    }

    public Long getTodayCancelled(){
        LocalDate today = LocalDate.now();

        return appointmentRepository.countByDateRangeAndStatus(
                today,
                today,
                AppointmentStatusType.CANCELLED
        );
    }

    public Long getLast7DaysCompleted(){
        LocalDate today = LocalDate.now();

        return appointmentRepository.countByDateRangeAndStatus(
                today.minusDays(7),
                today,
                AppointmentStatusType.COMPLETED
        );
    }

    public Long getLastMonthCompleted(){
        LocalDate today = LocalDate.now();

        return appointmentRepository.countByDateRangeAndStatus(
                today.minusMonths(1),
                today,
                AppointmentStatusType.COMPLETED
        );
    }

    public Long getLast6MonthsCompleted(){
        LocalDate today = LocalDate.now();

        return appointmentRepository.countByDateRangeAndStatus(
                today.minusMonths(6),
                today,
                AppointmentStatusType.COMPLETED
        );
    }
}
