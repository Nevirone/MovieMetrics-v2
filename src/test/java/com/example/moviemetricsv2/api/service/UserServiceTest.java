package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.dto.UserDto;
import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.model.ERole;
import com.example.moviemetricsv2.api.model.Role;
import com.example.moviemetricsv2.api.model.User;
import com.example.moviemetricsv2.api.repository.IRoleRepository;
import com.example.moviemetricsv2.api.repository.IUserRepository;
import com.example.moviemetricsv2.api.response.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class UserServiceTest {
    private AutoCloseable autoCloseable;

    @Mock
    private IUserRepository userRepository;
    @Mock
    private IRoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private User createUser() {
        return User.builder()
                .id(1L)
                .email("test@test.com")
                .password("TestPassword1")
                .role(
                        Role.builder()
                                .id(1L)
                                .name(ERole.User.getName())
                                .permissions(new ArrayList<>())
                                .build()
                )
                .build();
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .email("test@test.com")
                .password("TestPassword1")
                .isPasswordEncrypted(true)
                .roleId(1L)
                .build();
    }

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, roleRepository, passwordEncoder);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    @DisplayName("Create User: Successful")
    void canAddUser() {
        // given
        UserDto userDto = createUserDto();

        given(userRepository.existsByEmailIgnoreCase(userDto.getEmail()))
                .willReturn(false);

        given(roleRepository.existsById(userDto.getRoleId()))
                .willReturn(true);

        // when
        userService.create(userDto);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    @DisplayName("Create User: Email taken")
    void addingUserWillThrowWhenEmailIsTaken() {
        // given
        UserDto userDto = createUserDto();

        given(userRepository.existsByEmailIgnoreCase(userDto.getEmail()))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> userService.create(userDto))
                .isInstanceOf(DataConflictException.class)
                .hasMessageContaining(DataConflictException.emailTaken(userDto.getEmail()).getMessage());
    }

    @Test
    @DisplayName("Create User: Role not found")
    void addingUserWillThrowWhenRoleIsNotFound() {
        // given
        UserDto userDto = createUserDto();

        given(userRepository.existsByEmailIgnoreCase(userDto.getEmail()))
                .willReturn(false);

        given(roleRepository.findById(userDto.getRoleId()))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.create(userDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NotFoundException.roleNotFoundById(userDto.getRoleId()).getMessage());
    }

    @Test
    @DisplayName("Get User: Successful")
    void canGetUserById() {
        // given
        Long id = 2L;
        Optional<User> userOptional = Optional.of(
                createUser()
        );

        given(userRepository.findById(id))
                .willReturn(userOptional);

        // when
        User found = userService.get(id);

        // then
        assertThat(userOptional.get().getEmail()).isEqualTo(found.getEmail());
    }

    @Test
    @DisplayName("Get User: Not Found")
    void gettingUserByIdWillThrowWhenUserIsNotFound() {
        // given
        Long id = 2L;

        given(userRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.get(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NotFoundException.userNotFoundById(id).getMessage());
    }

    @Test
    @DisplayName("Get User By Email: Successful")
    void canGetUserByEmail() {
        // given
        Optional<User> userOptional = Optional.of(
                createUser()
        );

        given(userRepository.findByEmailIgnoreCase(userOptional.get().getEmail()))
                .willReturn(userOptional);

        // when
        User found = userService.getByEmail(userOptional.get().getEmail());

        // then
        assertThat(userOptional.get().getEmail()).isEqualTo(found.getEmail());
    }

    @Test
    @DisplayName("Get User By Email: Not Found")
    void gettingUserByEmailWillThrowWhenUserIsNotFound() {
        // given
        String email = "test@test.com";

        given(userRepository.findByEmailIgnoreCase(email))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.getByEmail(email))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NotFoundException.userNotFoundByEmail(email).getMessage());
    }

    @Test
    @DisplayName("Get All Users: Successful")
    void getAllUsers() {
        // when
        userService.getAll();

        // then
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Update User: Successful")
    void canUpdateUser() {
        // given
        Long id = 2L;
        UserDto userDto = createUserDto();

        given(userRepository.existsById(2L))
                .willReturn(true);

        given(userRepository.findByEmailIgnoreCase(userDto.getEmail()))
                .willReturn(Optional.empty());

        given(roleRepository.existsById(userDto.getRoleId())).willReturn(true);

        // when
        userService.update(id, userDto);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).existsById(id);
        verify(userRepository).findByEmailIgnoreCase(userDto.getEmail());

        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getId()).isEqualTo(id);
        assertThat(capturedUser.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    @DisplayName("Update User: Not Found")
    void updatingUserWillThrowWhenUserNotFound() {
        // given
        Long id = 2L;
        UserDto userDto = createUserDto();

        given(userRepository.findById(2L)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.update(id, userDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NotFoundException.userNotFoundById(id).getMessage());
    }

    @Test
    @DisplayName("Update User: Email taken")
    void updatingUserWillThrowWhenEmailIsTaken() {
        // given
        Long id = 2L;
        UserDto userDto = createUserDto();

        given(userRepository.existsById(2L))
                .willReturn(true);

        given(userRepository.findByEmailIgnoreCase(userDto.getEmail()))
                .willReturn(Optional.of(createUser()));

        // when
        // then
        assertThatThrownBy(() -> userService.update(id, userDto))
                .isInstanceOf(DataConflictException.class)
                .hasMessageContaining(DataConflictException.emailTaken(userDto.getEmail()).getMessage());
    }

    @Test
    @DisplayName("Update User: Role not found")
    void updatingUserWillThrowWhenRoleNotFound() {
        // given
        Long id = 2L;
        UserDto userDto = createUserDto();

        given(userRepository.existsById(2L))
                .willReturn(true);

        given(userRepository.existsByEmailIgnoreCase(userDto.getEmail()))
                .willReturn(false);

        given(roleRepository.findById(userDto.getRoleId()))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.update(id, userDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NotFoundException.roleNotFoundById(userDto.getRoleId()).getMessage());
    }

    @Test
    @DisplayName("Delete User: Successful")
    void canDeleteUser() {
        // given
        Long id = 2L;

        given(userRepository.findById(id))
                .willReturn(Optional.of(createUser()));

        // when
        userService.delete(id);

        // then
        verify(userRepository).deleteById(id);
    }

    @Test
    @DisplayName("Delete User: Not found")
    void deletingUserWillThrowWhenUserNotFound() {
        // given
        Long id = 2L;

        given(userRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.delete(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NotFoundException.userNotFoundById(id).getMessage());
    }
}