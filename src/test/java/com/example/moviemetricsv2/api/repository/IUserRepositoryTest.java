package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class IUserRepositoryTest {

    @Autowired
    IUserRepository userRepository;

    @Test
    @DisplayName("Get User By Email: Successful")
    void itShouldCheckIfUserExistsByEmail() {
        // given
        String email = "test@test.com";
        User user = User.builder().email(email).password("test").build();
        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findByEmail(email);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Get User By Email: Not Found")
    void itShouldCheckIfUserDoesNotExistByEmail() {
        // given
        String email = "test@test.com";

        // when
        Optional<User> found = userRepository.findByEmail(email);

        // then
        assertThat(found.isEmpty()).isTrue();
    }
}