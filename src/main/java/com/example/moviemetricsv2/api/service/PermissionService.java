package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.model.Permission;
import com.example.moviemetricsv2.api.repository.IPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final IPermissionRepository permissionRepository;

    public Permission findOrCreate(String name) {
        // todo shorten
        Optional<Permission> permission = permissionRepository.findByNameIgnoreCase(name);

        return permission.orElseGet(() -> permissionRepository.save(
                Permission.builder()
                        .name(name)
                        .build()
        ));
    }

    public void createIfNotFound(String name) {
        if (!permissionRepository.existsByNameIgnoreCase(name))
            permissionRepository.save(
                    Permission.builder()
                            .name(name)
                            .build()
            );
    }
}
