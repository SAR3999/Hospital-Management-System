package com.sar.HospitalManagement.dto;

import com.sar.HospitalManagement.entity.type.BloodGroupType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloodGroupCountResponseEntity {
    private BloodGroupType bloodGroup;
    private Long count;
}
