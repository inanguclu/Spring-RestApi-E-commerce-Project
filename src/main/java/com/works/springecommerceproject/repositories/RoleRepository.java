package com.works.springecommerceproject.repositories;

import com.works.springecommerceproject.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}