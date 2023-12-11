package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IPermissionRepository extends JpaRepository<Permission, Long> {

    @Query("SELECT p FROM Permission p WHERE p.name = :name")
    Optional<Permission> findByName(@Param("name") String name);
}
