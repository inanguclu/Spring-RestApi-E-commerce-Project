package com.works.ecommerce.repositories;

import com.works.ecommerce.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}