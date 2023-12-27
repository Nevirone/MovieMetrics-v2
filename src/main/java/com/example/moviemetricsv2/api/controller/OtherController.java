package com.example.moviemetricsv2.api.controller;

import com.example.moviemetricsv2.api.model.Genre;
import com.example.moviemetricsv2.api.model.MovieClassification;
import com.example.moviemetricsv2.api.model.Permission;
import com.example.moviemetricsv2.api.model.Role;
import com.example.moviemetricsv2.api.service.GenreService;
import com.example.moviemetricsv2.api.service.MovieClassificationService;
import com.example.moviemetricsv2.api.service.PermissionService;
import com.example.moviemetricsv2.api.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OtherController extends BaseController {
    private final MovieClassificationService movieClassificationService;
    private final GenreService genreService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    @PreAuthorize("hasAuthority('DISPLAY_MOVIES')")
    @GetMapping("/classifications")
    public ResponseEntity<List<MovieClassification>> getAllClassifications() {
        return ResponseEntity.status(HttpStatus.OK).body(movieClassificationService.getAll());
    }

    @PreAuthorize("hasAuthority('DISPLAY_MOVIES')")
    @GetMapping("/genres")
    public ResponseEntity<List<Genre>> getAllGenres() {
        return ResponseEntity.status(HttpStatus.OK).body(genreService.getAll());
    }

    @PreAuthorize("hasAuthority('CREATE_USERS')")
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.getAll());
    }

    @PreAuthorize("hasAuthority('CREATE_USERS')")
    @GetMapping("/permissions")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.status(HttpStatus.OK).body(permissionService.getAll());
    }
}
