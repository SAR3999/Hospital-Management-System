package com.sar.HospitalManagement.service;

import com.sar.HospitalManagement.dto.DoctorResponseDto;
import com.sar.HospitalManagement.dto.OnBoardDoctorRequestDto;
import com.sar.HospitalManagement.entity.Doctor;
import com.sar.HospitalManagement.entity.User;
import com.sar.HospitalManagement.repository.DoctorRepository;
import com.sar.HospitalManagement.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    private DoctorRepository doctorRepository;
    private ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, ModelMapper modelMapper,
                         UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    public List<DoctorResponseDto> getAllDoctors(){
        return doctorRepository.findAll()
                .stream()
                .map(doctor -> modelMapper.map(doctor, DoctorResponseDto.class))
                .collect(Collectors.toList());
    }

//    public DoctorResponseDto onBoardNewDoctor(OnBoardDoctorRequestDto onBoardDoctorRequestDto){
//        User user = userRepository.findById(onBoardDoctorRequestDto.getUserId()).orElseThrow();
//
//        if(doctorRepository.existsById(onBoardDoctorRequestDto.getUserId())){
//            throw new IllegalArgumentException("Doctor already registered.");
//        }
//
//        Doctor doctor = Doctor.builder()
//                .name(onBoardDoctorRequestDto.getName())
//                .specialization(onBoardDoctorRequestDto.getSpecialization())
//                .occupation(onBoardDoctorRequestDto.getOccupation)
//    }
}
