package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.dto.UserDto;
import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.model.Role;
import com.example.moviemetricsv2.api.model.User;
import com.example.moviemetricsv2.api.repository.IRoleRepository;
import com.example.moviemetricsv2.api.repository.IUserRepository;
import com.example.moviemetricsv2.api.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IObjectService<User, UserDto, UserResponse> {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(UserDto userDto) {
        if (userRepository.existsByEmailIgnoreCase(userDto.getEmail()))
            throw DataConflictException.emailTaken(userDto.getEmail());

        if (!roleRepository.existsById(userDto.getRoleId()))
            throw NotFoundException.roleNotFoundById(userDto.getRoleId());

        User created = userRepository.save(
                User.builder()
                        .email(userDto.getEmail())
                        .password(
                                userDto.getIsPasswordEncrypted() ?
                                        userDto.getPassword() :
                                        passwordEncoder.encode(userDto.getPassword())
                        )
                        .role(roleRepository.getReferenceById(userDto.getRoleId()))
                        .build()
        );
        return new UserResponse(created);
    }

    @Override
    public UserResponse get(Long id) throws NotFoundException {
        User found = userRepository.findById(id)
                .orElseThrow(() -> NotFoundException.userNotFoundById(id));

        return new UserResponse(found);
    }

    public UserResponse getByEmail(String email) {
        User found = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> NotFoundException.userNotFoundByEmail(email));

        return new UserResponse(found);

    }

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(UserResponse::new).toList();
    }

    @Override
    public UserResponse update(Long id, UserDto userDto) throws DataConflictException, NotFoundException {
        if (!userRepository.existsById(id))
            throw NotFoundException.userNotFoundById(id);

        Optional<User> found = userRepository.findByEmailIgnoreCase(userDto.getEmail());

        if (found.isPresent() && !found.get().getId().equals(id))
            throw DataConflictException.emailTaken(userDto.getEmail());

        if (!roleRepository.existsById(userDto.getRoleId()))
            throw NotFoundException.roleNotFoundById(userDto.getRoleId());

        User updated = userRepository.save(
                User.builder()
                        .id(id)
                        .email(userDto.getEmail())
                        .password(
                                userDto.getIsPasswordEncrypted() ?
                                        userDto.getPassword() :
                                        passwordEncoder.encode(userDto.getPassword())
                        )
                        .role(roleRepository.getReferenceById(userDto.getRoleId()))
                        .build()
        );
        return new UserResponse(updated);
    }

    @Override
    public UserResponse delete(Long id) throws NotFoundException {
        User found = userRepository.findById(id)
                .orElseThrow(() -> NotFoundException.userNotFoundById(id));

        userRepository.deleteById(id);

        return new UserResponse(found);
    }
}
