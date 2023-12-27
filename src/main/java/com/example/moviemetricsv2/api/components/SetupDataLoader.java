package com.example.moviemetricsv2.api.components;

import com.example.moviemetricsv2.api.model.*;
import com.example.moviemetricsv2.api.repository.IUserRepository;
import com.example.moviemetricsv2.api.service.GenreService;
import com.example.moviemetricsv2.api.service.MovieClassificationService;
import com.example.moviemetricsv2.api.service.PermissionService;
import com.example.moviemetricsv2.api.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final Environment environment;

    private final IUserRepository userRepository;
    private final PasswordEncoder encoder;
    private final PermissionService permissionService;
    private final GenreService genreService;
    private final RoleService roleService;
    private final MovieClassificationService movieClassificationService;

    boolean alreadySetup = false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) return;

        Map<EPermission, Permission> privileges = new HashMap<>();

        for (int i = 0; i < EMovieClassification.values().length; i++) {
            movieClassificationService.createIfNotFound(
                    (long) (i + 1),
                    EMovieClassification.values()[i].getName(),
                    EMovieClassification.values()[i].getBrief()
            );
        }

        for (int i = 0; i < EPermission.values().length; i ++) {
            privileges.put(EPermission.values()[i],
                    permissionService.findOrCreate(
                            (long) (i+1),
                            EPermission.values()[i].getName()
                    ));
        }

        for (int i = 0; i < EGenre.values().length; i++) {
            genreService.createIfNotFound(
                    (long) (i + 1),
                    EGenre.values()[i].getName()
            );
        }

        List<Permission> userPrivileges = new ArrayList<>();

        userPrivileges.add(privileges.get(EPermission.DisplayMovies));
        userPrivileges.add(privileges.get(EPermission.DisplayReviews));
        userPrivileges.add(privileges.get(EPermission.CreateReviews));
        userPrivileges.add(privileges.get(EPermission.UpdateOwnReviews));
        userPrivileges.add(privileges.get(EPermission.DeleteOwnReviews));

        roleService.createIfNotFound(1L, ERole.User.getName(), userPrivileges);

        List<Permission> moderatorPrivileges = new ArrayList<>();

        moderatorPrivileges.add(privileges.get(EPermission.DisplayMovies));
        moderatorPrivileges.add(privileges.get(EPermission.CreateMovies));
        moderatorPrivileges.add(privileges.get(EPermission.UpdateMovies));
        moderatorPrivileges.add(privileges.get(EPermission.DeleteMovies));

        moderatorPrivileges.add(privileges.get(EPermission.DisplayReviews));
        moderatorPrivileges.add(privileges.get(EPermission.CreateReviews));
        moderatorPrivileges.add(privileges.get(EPermission.UpdateReviews));
        moderatorPrivileges.add(privileges.get(EPermission.DeleteReviews));
        
        userPrivileges.add(privileges.get(EPermission.UpdateOwnReviews));
        userPrivileges.add(privileges.get(EPermission.DeleteOwnReviews));

        moderatorPrivileges.add(privileges.get(EPermission.DisplayUsers));

        roleService.createIfNotFound(2L, ERole.Moderator.getName(), moderatorPrivileges);

        Role adminRole = roleService.findOrCreate(3L, ERole.Admin.getName(), privileges.values().stream().toList());

        if (!userRepository.existsByEmailIgnoreCase(environment.getProperty("root.root_access"))) {
            userRepository.save(
                    User.builder()
                            .email(environment.getProperty("root.root_access"))
                            .password(encoder.encode(environment.getProperty("root.root_password")))
                            .role(adminRole)
                            .build()
            );
        }
    }
}
