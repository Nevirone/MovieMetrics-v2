package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.Permission;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class IPermissionRepositoryTest {
    @Autowired
    IPermissionRepository permissionRepository;

    @Test
    @DisplayName("Find Permission By Name: Successful")
    void testCanFindPermissionByName() {
        // given
        String name = "Delete_User";
        Permission permission = Permission.builder().id(1L).name(name).build();

        permissionRepository.save(permission);

        // when
        Optional<Permission> found = permissionRepository.findByNameIgnoreCase(name);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getName()).isEqualTo(permission.getName());
    }

    @Test
    @DisplayName("Find Permission By Name Case Insensitive: Successful")
    void testCanFindPermissionByNameCaseInsensitive() {
        // given
        String name = "DeLEte_USer";
        Permission permission = Permission.builder().id(1L).name(name.toLowerCase()).build();

        permissionRepository.save(permission);

        // when
        Optional<Permission> found = permissionRepository.findByNameIgnoreCase(name);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getName()).isEqualTo(permission.getName());
    }

    @Test
    @DisplayName("Find Permission By Name: Not Found")
    void testCanFindByNameIfPermissionDoesNotExist() {
        // given
        String name = "Delete_User";

        // when
        Optional<Permission> found = permissionRepository.findByNameIgnoreCase(name);

        // then
        assertThat(found.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Check Name Exists: Successful")
    void testCanCheckNameExists() {
        // given
        String name = "Delete_User";
        Permission permission = Permission.builder().id(1L).name(name).build();

        permissionRepository.save(permission);

        // when
        // then
        assertThat(permissionRepository.existsByNameIgnoreCase(name)).isTrue();
    }

    @Test
    @DisplayName("Check Name Exists Case Insensitive: Successful")
    void testCanCheckNameExistsCaseInsensitive() {
        // given
        String name = "DeLEte_USer";
        Permission permission = Permission.builder().id(1L).name(name.toLowerCase()).build();

        permissionRepository.save(permission);

        // when
        // then
        assertThat(permissionRepository.existsByNameIgnoreCase(name)).isTrue();
    }

    @Test
    @DisplayName("Check Name Exists: Not Found")
    void testCanCheckNameExistsNotFound() {
        // given
        String name = "Delete_User";

        // when
        // then
        assertThat(permissionRepository.existsByNameIgnoreCase(name)).isFalse();
    }
}