package com.works.repositories;

import com.works.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByEmailEqualsIgnoreCase(String email);

    Optional<Admin> findByVerificationCodeEquals(String verificationCode);


}