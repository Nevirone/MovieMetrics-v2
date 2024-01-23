package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.model.Permission;
import com.example.moviemetricsv2.api.repository.IPermissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class PermissionServiceTest {
    private AutoCloseable autoCloseable;
    @Mock
    private IPermissionRepository permissionRepository;

    private PermissionService permissionService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        permissionService = new PermissionService(permissionRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    @DisplayName("Find Or Create: Found")
    void canFindOrCreateWhenFound() {
        // given
        String name = "DISPLAY_USERS";
        given(permissionRepository.findByNameIgnoreCase(name))
                .willReturn(Optional.of(Permission.builder().name(name).build()));

        // when
        permissionService.findOrCreate(1L, name);

        // then
        verify(permissionRepository).findByNameIgnoreCase(name);
    }

    @Test
    @DisplayName("Find Or Create: Created")
    void canFindOrCreateWhenNotFound() {
        // given
        String name = "DISPLAY_USERS";
        given(permissionRepository.findByNameIgnoreCase(name))
                .willReturn(Optional.empty());

        // when
        permissionService.findOrCreate(1L, name);

        // then
        ArgumentCaptor<Permission> permissionArgumentCaptor = ArgumentCaptor.forClass(Permission.class);

        verify(permissionRepository).findByNameIgnoreCase(name);
        verify(permissionRepository).save(permissionArgumentCaptor.capture());

        Permission capturedPermission = permissionArgumentCaptor.getValue();

        assertThat(capturedPermission.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Create If Not Found: Found")
    void canCreateIfNotFoundWhenFound() {
        // given
        String name = "DISPLAY_USERS";
        given(permissionRepository.existsByNameIgnoreCase(name))
                .willReturn(true);

        // when
        permissionService.createIfNotFound(1L, name);

        // then
        verify(permissionRepository).existsByNameIgnoreCase(name);
    }

    @Test
    @DisplayName("Create If Not Found: Not Found")
    void canCreateIfNotFoundWhenNotFound() {
        // given
        String name = "DISPLAY_USERS";
        given(permissionRepository.existsByNameIgnoreCase(name))
                .willReturn(false);

        // when
        permissionService.createIfNotFound(1L, name);

        // then
        ArgumentCaptor<Permission> permissionArgumentCaptor = ArgumentCaptor.forClass(Permission.class);

        verify(permissionRepository).existsByNameIgnoreCase(name);
        verify(permissionRepository).save(permissionArgumentCaptor.capture());

        Permission capturedPermission = permissionArgumentCaptor.getValue();

        assertThat(capturedPermission.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Get All Permissions: Successful")
    void getAllPermissions() {
        // given
        given(permissionRepository.findAll())
                .willReturn(new ArrayList<>());

        // when
        permissionService.getAll();

        // then
        verify(permissionRepository).findAll();
    }
}