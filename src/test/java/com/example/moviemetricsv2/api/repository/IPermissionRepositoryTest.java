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
    @DisplayName("Get Permission By Name: Successful")
    void testCanFindByNameIfPermissionDoesExist() {
        // given
        String name = "TestPermission";
        Permission permission = Permission.builder().name(name).build();
        permissionRepository.save(permission);

        // when
        Optional<Permission> found = permissionRepository.findByName(name);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getName()).isEqualTo(permission.getName());
    }

    @Test
    @DisplayName("Get Permission By Name: Not Found")
    void testCanFindByNameIfPermissionDoesNotExist() {
        // given
        String name = "TestPermission";

        // when
        Optional<Permission> found = permissionRepository.findByName(name);

        // then
        assertThat(found.isEmpty()).isTrue();
    }
}