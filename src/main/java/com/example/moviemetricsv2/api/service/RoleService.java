package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.model.Permission;
import com.example.moviemetricsv2.api.model.Role;
import com.example.moviemetricsv2.api.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final IRoleRepository roleRepository;

    public Role findOrCreate(String name, List<Permission> permissions) {
        Optional<Role> role = roleRepository.findByNameIgnoreCase(name);

        return role.orElseGet(() -> roleRepository.save(
                Role.builder()
                        .name(name)
                        .permissions(permissions)
                        .build()
        ));
    }

    public void createIfNotFound(String name, List<Permission> permissions) {
        if (!roleRepository.existsByNameIgnoreCase(name))
            roleRepository.save(
                Role.builder()
                    .name(name)
                    .permissions(permissions)
                    .build()
            );
    }
}
