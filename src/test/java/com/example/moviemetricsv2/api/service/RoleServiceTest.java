package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.model.Role;
import com.example.moviemetricsv2.api.repository.IRoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class RoleServiceTest {
    private AutoCloseable autoCloseable;
    @Mock
    private IRoleRepository roleRepository;

    private RoleService roleService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        roleService = new RoleService(roleRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    @DisplayName("Find Or Create: Found")
    void canFindOrCreateWhenFound() {
        // given
        String name = "USER";
        given(roleRepository.findByNameIgnoreCase(name))
                .willReturn(Optional.of(Role.builder().name(name).build()));

        // when
        roleService.findOrCreate(name, List.of());

        // then
        verify(roleRepository).findByNameIgnoreCase(name);
    }

    @Test
    @DisplayName("Find Or Create: Created")
    void canFindOrCreateWhenNotFound() {
        // given
        String name = "USER";
        given(roleRepository.findByNameIgnoreCase(name))
                .willReturn(Optional.empty());

        // when
        roleService.findOrCreate(name, List.of());

        // then
        ArgumentCaptor<Role> roleArgumentCaptor = ArgumentCaptor.forClass(Role.class);

        verify(roleRepository).findByNameIgnoreCase(name);
        verify(roleRepository).save(roleArgumentCaptor.capture());

        Role capturedRole = roleArgumentCaptor.getValue();

        assertThat(capturedRole.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Create If Not Found: Found")
    void canCreateIfNotFoundWhenFound() {
        // given
        String name = "USER";
        given(roleRepository.existsByNameIgnoreCase(name))
                .willReturn(true);

        // when
        roleService.createIfNotFound(name, List.of());

        // then
        verify(roleRepository).existsByNameIgnoreCase(name);
    }

    @Test
    @DisplayName("Create If Not Found: Not Found")
    void canCreateIfNotFoundWhenNotFound() {
        // given
        String name = "USER";
        given(roleRepository.existsByNameIgnoreCase(name))
                .willReturn(false);

        // when
        roleService.createIfNotFound(name, List.of());

        // then
        ArgumentCaptor<Role> roleArgumentCaptor = ArgumentCaptor.forClass(Role.class);

        verify(roleRepository).existsByNameIgnoreCase(name);
        verify(roleRepository).save(roleArgumentCaptor.capture());

        Role capturedRole = roleArgumentCaptor.getValue();

        assertThat(capturedRole.getName()).isEqualTo(name);
    }
}