package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class IRoleRepositoryTest {
    @Autowired
    IRoleRepository roleRepository;

    @Test
    @DisplayName("Find Role By Name: Successful")
    void testCanFindRoleByName() {
        // given
        String name = "Action";
        Role role = Role.builder().id(1L).name(name).build();

        roleRepository.save(role);

        // when
        Optional<Role> found = roleRepository.findByNameIgnoreCase(name);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getName()).isEqualTo(role.getName());
    }

    @Test
    @DisplayName("Find Role By Name Case Insensitive: Successful")
    void testCanFindRoleByNameCaseInsensitive() {
        // given
        String name = "AcTion";
        Role role = Role.builder().id(1L).name(name.toLowerCase()).build();

        roleRepository.save(role);

        // when
        Optional<Role> found = roleRepository.findByNameIgnoreCase(name);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getName()).isEqualTo(role.getName());
    }

    @Test
    @DisplayName("Find Role By Name: Not Found")
    void testCanFindByNameIfRoleDoesNotExist() {
        // given
        String name = "Action";

        // when
        Optional<Role> found = roleRepository.findByNameIgnoreCase(name);

        // then
        assertThat(found.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Check Name Exists: Successful")
    void testCanCheckNameExists() {
        // given
        String name = "Action";
        Role role = Role.builder().id(1L).name(name).build();

        roleRepository.save(role);

        // when
        // then
        assertThat(roleRepository.existsByNameIgnoreCase(name)).isTrue();
    }

    @Test
    @DisplayName("Check Name Exists Case Insensitive: Successful")
    void testCanCheckNameExistsCaseInsensitive() {
        // given
        String name = "AcTIon";
        Role role = Role.builder().id(1L).name(name.toLowerCase()).build();

        roleRepository.save(role);

        // when
        // then
        assertThat(roleRepository.existsByNameIgnoreCase(name)).isTrue();
    }

    @Test
    @DisplayName("Check Name Exists: Not Found")
    void testCanCheckNameExistsNotFound() {
        // given
        String name = "Action";

        // when
        // then
        assertThat(roleRepository.existsByNameIgnoreCase(name)).isFalse();
    }
}