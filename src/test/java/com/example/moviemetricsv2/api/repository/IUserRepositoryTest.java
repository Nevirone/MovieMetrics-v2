package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.Role;
import com.example.moviemetricsv2.api.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class IUserRepositoryTest {

    @Autowired
    IUserRepository userRepository;
    @Autowired
    IRoleRepository roleRepository;

    @Test
    @DisplayName("Find User By Email: Successful")
    void testCanFindUserByEmail() {
        // given
        String email = "test@test.com";
        Role role = roleRepository.save(
                Role.builder()
                        .id(1L)
                        .name("TestRole")
                        .permissions(new ArrayList<>())
                        .build()
        );
        User user = User.builder().email(email).password("test").role(role).build();
        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findByEmailIgnoreCase(email);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Find User By Email Case Insensitive: Successful")
    void testCanFindUserByEmailCaseInsensitive() {
        // given
        String email = "tEsT@Test.com";
        Role role = roleRepository.save(
                Role.builder()
                        .id(1L)
                        .name("TestRole")
                        .permissions(new ArrayList<>())
                        .build()
        );
        User user = User.builder().email(email.toLowerCase()).password("test").role(role).build();
        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findByEmailIgnoreCase(email);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Find User By Email: Not Found")
    void testCanFindByEmailIfUserDoesNotExist() {
        // given
        String email = "test@test.com";

        // when
        Optional<User> found = userRepository.findByEmailIgnoreCase(email);

        // then
        assertThat(found.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Check Email Exists: Successful")
    void testCanCheckEmailExists() {
        // given
        String email = "test@test.com";
        Role role = roleRepository.save(
                Role.builder()
                        .id(1L)
                        .name("TestRole")
                        .permissions(new ArrayList<>())
                        .build()
        );
        User user = User.builder().email(email).password("test").role(role).build();
        userRepository.save(user);

        // when
        // then
        assertThat(userRepository.existsByEmailIgnoreCase(email)).isTrue();
    }

    @Test
    @DisplayName("Check Email Exists Case Insensitive: Successful")
    void testCanCheckEmailExistsCaseInsensitive() {
        // given
        String email = "TesT@TEsT.com";
        Role role = roleRepository.save(
                Role.builder()
                        .id(1L)
                        .name("TestRole")
                        .permissions(new ArrayList<>())
                        .build()
        );
        User user = User.builder().email(email.toLowerCase()).password("test").role(role).build();
        userRepository.save(user);

        // when
        // then
        assertThat(userRepository.existsByEmailIgnoreCase(email)).isTrue();
    }

    @Test
    @DisplayName("Check Email Exists: Not Found")
    void testCanCheckEmailExistsNotFound() {
        // given
        String email = "test@test.com";

        // when
        // then
        assertThat(userRepository.existsByEmailIgnoreCase(email)).isFalse();
    }
}