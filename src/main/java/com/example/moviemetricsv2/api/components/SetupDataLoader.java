package com.example.moviemetricsv2.api.components;

import com.example.moviemetricsv2.api.model.*;
import com.example.moviemetricsv2.api.repository.IPermissionRepository;
import com.example.moviemetricsv2.api.repository.IRoleRepository;
import com.example.moviemetricsv2.api.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final Environment environment;

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;

    boolean alreadySetup = false;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) return;

        Map<EPermission, Permission> privileges = new HashMap<>();

        for (EPermission ePermission : EPermission.values()) {
            privileges.put(ePermission, createPermissionIfNotFound(ePermission.toString()));
        }

        Set<Permission> userPrivileges = new HashSet<>();

        createRoleIfNotFound(ERole.USER.toString(), userPrivileges);

        Set<Permission> moderatorPrivileges = new HashSet<>();

        moderatorPrivileges.add(privileges.get(EPermission.DISPLAY_USERS));

        createRoleIfNotFound(ERole.MODERATOR.toString(), moderatorPrivileges);

        Set<Permission> adminPrivileges = new HashSet<>(privileges.values());

        Role adminRole = createRoleIfNotFound(ERole.ADMIN.toString(), adminPrivileges);

        userRepository.save(
                User.builder()
                        .email(environment.getProperty("root.root_access"))
                        .password(environment.getProperty("root.root_password"))
                        .role(adminRole)
                        .build()
        );
    }

    Permission createPermissionIfNotFound(String name) {
        Optional<Permission> permission = permissionRepository.findByName(name);
        return permission.orElseGet(() -> permissionRepository.save(
                Permission.builder().name(name).build()
        ));
    }

    Role createRoleIfNotFound(String name, Set<Permission> permissions) {
        Optional<Role> role = roleRepository.findByName(name);
        return role.orElseGet(() -> roleRepository.save(
                Role.builder().name(name).permissions(permissions).build()
        ));
    }
}
