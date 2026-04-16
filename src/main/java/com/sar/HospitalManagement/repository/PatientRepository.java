package com.sar.HospitalManagement.repository;

import com.sar.HospitalManagement.dto.BloodGroupCountResponseEntity;
import com.sar.HospitalManagement.entity.Patient;
import com.sar.HospitalManagement.entity.type.BloodGroupType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


/*
* Uses SimpleJpaRepository (class) has an implementation of JpaRepository(interface) methods
* entityManager is responsible for all handling in SimpleJpaRepository class.
* entityManager is entity of Hibernate (follows Hibernate Life cycle).
* */
@Repository
public interface PatientRepository extends JpaRepository<Patient,Long> {
    /*Query Methods*/
    List<Patient> findByNameContaining(String str);
    List<Patient> findAllByOrderByEmailDesc();

    /*JPQL*/
    // 1. Positional (1 mapped with positional of argument in method)
    @Query("SELECT p FROM Patient p WHERE p.bloodGroup=?1")
    List<Patient> findByBloodGroup(BloodGroupType bloodGroup);
    //List<Patient> findByBloodGroup(@Param("bloodGroup") BloodGroupType bloodGroup);

    // 2. Named (:variableName) mapped with @Param("variableName)
    @Query("SELECT p FROM Patient p WHERE p.birthDate > :birthDate ORDER BY birthDate DESC")
    List<Patient> findByBirthDateAfterDate(@Param("birthDate") LocalDate birthDate);
    /*
            *** Projection ***
          @Query("SELECT p.name, p.email, p.birthDate FROM Patient p WHERE p.birthDate > :birthDate")
          for this query we need to create dto class of patient which includes field like name,email and birthdate for object mapping
          *
          * example is below
    */

//    @Query("SELECT p.bloodGroup, COUNT(p) FROM Patient p GROUP BY p.bloodGroup")
//    List<Object[]> countEachBloodGroupType();
    @Query("SELECT new com.sar.HospitalManagement.dto.BloodGroupCountResponseEntity(p.bloodGroup, COUNT(p)) FROM Patient p GROUP BY p.bloodGroup")
    List<BloodGroupCountResponseEntity> countEachBloodGroupType();

    /* Native queries are directly passed (raw query we don't need to convert it)  */
//    @Query(value = "SELECT * FROM PATIENT",nativeQuery = true)
//    List<Patient> findAllPatients();
    /*Paging*/
    @Query(value = "SELECT * FROM PATIENT",nativeQuery = true)
    Page<Patient> findAllPatients(Pageable pageable);

    /* Update Query*/
    @Transactional
    @Modifying
    @Query("UPDATE Patient p SET p.name = :name, p.email = :email WHERE p.id = :id")
    int updateNameAndEmailById(@Param("name") String name, @Param("email") String email, @Param("id") Long id);

    /*Solving n+1 queries problem by custom query*/
    @Query("SELECT p from Patient p LEFT JOIN FETCH p.insurance")
    List<Patient> findAllPatientWithInsurance();
}