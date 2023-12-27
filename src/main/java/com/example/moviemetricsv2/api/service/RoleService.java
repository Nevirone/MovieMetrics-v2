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

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public Role findOrCreate(Long id, String name, List<Permission> permissions) {
        return roleRepository.findByNameIgnoreCase(name).orElseGet(() -> roleRepository.save(
                Role.builder()
                        .id(id)
                        .name(name)
                        .permissions(permissions)
                        .build()
        ));
    }

    public void createIfNotFound(Long id, String name, List<Permission> permissions) {
        if (!roleRepository.existsByNameIgnoreCase(name))
            roleRepository.save(
                    Role.builder()
                            .id(id)
                            .name(name)
                            .permissions(permissions)
                            .build()
            );
    }
}
