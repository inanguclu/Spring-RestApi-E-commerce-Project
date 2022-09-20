package com.works.springecommerceproject.repositories;

import com.works.springecommerceproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEMailEqualsIgnoreCase(String eMail);
}