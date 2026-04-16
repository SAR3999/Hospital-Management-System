package com.sar.HospitalManagement.repository;

import com.sar.HospitalManagement.entity.User;
import com.sar.HospitalManagement.entity.type.AuthProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByProviderIdAndProviderType(String providerId, AuthProviderType providerType);
}