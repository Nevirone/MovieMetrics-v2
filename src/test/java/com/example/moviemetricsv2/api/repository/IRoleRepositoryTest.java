package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class IRoleRepositoryTest {

    @Autowired
    IRoleRepository roleRepository;

    @Test
    @DisplayName("Get Role By Name: Successful")
    void testCanFindByNameIfRoleDoesExist() {
        // given
        String name = "TestRole";
        Role role = Role.builder().name(name).permissions(new HashSet<>()).build();
        roleRepository.save(role);

        // when
        Optional<Role> found = roleRepository.findByName(name);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getName()).isEqualTo(role.getName());
    }

    @Test
    @DisplayName("Get Role By Name: Not Found")
    void testCanFindByNameIfRoleDoesNotExist() {
        // given
        String name = "TestRole";

        // when
        Optional<Role> found = roleRepository.findByName(name);

        // then
        assertThat(found.isEmpty()).isTrue();
    }
}