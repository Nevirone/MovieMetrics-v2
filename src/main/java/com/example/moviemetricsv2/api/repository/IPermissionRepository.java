package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
