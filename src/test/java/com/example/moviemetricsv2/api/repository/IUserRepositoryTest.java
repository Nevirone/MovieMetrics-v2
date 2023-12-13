package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.Role;
import com.example.moviemetricsv2.api.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class IUserRepositoryTest {

    @Autowired
    IUserRepository userRepository;
    @Autowired
    IRoleRepository roleRepository;

    @Test
    @DisplayName("Get User By Email: Successful")
    void testCanFindByEmailIfUserDoesExist() {
        // given
        String email = "test@test.com";
        Role role = roleRepository.save(
                Role.builder()
                        .name("TestRole")
                        .permissions(new HashSet<>())
                        .build()
        );
        User user = User.builder().email(email).password("test").role(role).build();
        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findByEmail(email);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Get User By Email: Not Found")
    void testCanFindByEmailIfUserDoesNotExist() {
        // given
        String email = "test@test.com";

        // when
        Optional<User> found = userRepository.findByEmail(email);

        // then
        assertThat(found.isEmpty()).isTrue();
    }
}