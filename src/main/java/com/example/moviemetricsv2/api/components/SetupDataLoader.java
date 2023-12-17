package com.example.moviemetricsv2.api.components;

import com.example.moviemetricsv2.api.model.*;
import com.example.moviemetricsv2.api.repository.IUserRepository;
import com.example.moviemetricsv2.api.service.MovieClassificationService;
import com.example.moviemetricsv2.api.service.PermissionService;
import com.example.moviemetricsv2.api.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final Environment environment;

    private final IUserRepository userRepository;
    private final PasswordEncoder encoder;
    private final PermissionService permissionService;
    private final RoleService roleService;
    private final MovieClassificationService movieClassificationService;

    boolean alreadySetup = false;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) return;

        Map<EPermission, Permission> privileges = new HashMap<>();

        for (EMovieClassification eMovieClassification : EMovieClassification.values()) {
            movieClassificationService.createIfNotFound(eMovieClassification.getName());
        }

        for (EPermission ePermission : EPermission.values()) {
            privileges.put(ePermission, permissionService.findOrCreate(ePermission.getName()));
        }

        List<Permission> userPrivileges = new ArrayList<>();

        userPrivileges.add(privileges.get(EPermission.DisplayMovies));

        roleService.createIfNotFound(ERole.User.getName(), userPrivileges);

        List<Permission> moderatorPrivileges = new ArrayList<>();

        moderatorPrivileges.add(privileges.get(EPermission.DisplayMovies));
        moderatorPrivileges.add(privileges.get(EPermission.CreateMovies));
        moderatorPrivileges.add(privileges.get(EPermission.UpdateUsers));
        moderatorPrivileges.add(privileges.get(EPermission.DeleteMovies));

        moderatorPrivileges.add(privileges.get(EPermission.DisplayUsers));

        roleService.createIfNotFound(ERole.Moderator.getName(), moderatorPrivileges);

        Role adminRole = roleService.findOrCreate(ERole.Admin.getName(), privileges.values().stream().toList());

        userRepository.save(
                User.builder()
                        .email(environment.getProperty("root.root_access"))
                        .password(encoder.encode(environment.getProperty("root.root_password")))
                        .role(adminRole)
                        .build()
        );
    }
}
