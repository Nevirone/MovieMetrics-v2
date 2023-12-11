package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.model.Role;
import com.example.moviemetricsv2.api.model.User;
import com.example.moviemetricsv2.api.repository.IRoleRepository;
import com.example.moviemetricsv2.api.repository.IUserRepository;
import com.example.moviemetricsv2.api.request.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IObjectService<User, UserDto>{
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User create(UserDto userDto) throws DataConflictException, NotFoundException {
        Optional<User> taken = userRepository.findByEmail(userDto.getEmail());

        if (taken.isPresent()) throw DataConflictException.emailTaken(userDto.getEmail());

        Optional<Role> role = roleRepository.findByName(userDto.getRole());

        if (role.isEmpty()) throw NotFoundException.roleNotFound(userDto.getRole());

        return userRepository.save(
                User.builder()
                .email(userDto.getEmail())
                .password(userDto.getIsPasswordEncrypted()?
                        userDto.getPassword() :
                        passwordEncoder.encode(userDto.getPassword())
                )
                .role(role.get())
                .build()
        );
    }

    @Override
    public User get(Long id) {
    Optional<User> found = userRepository.findById(id);

        if (found.isEmpty()) throw NotFoundException.userNotFoundById(id);

        return found.get();
    }

    public User getByEmail(String email) {
        Optional<User> found = userRepository.findByEmail(email);

        if (found.isEmpty()) throw NotFoundException.userNotFoundByEmail(email);

        return found.get();

    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(Long id, UserDto userDto) {
        Optional<User> found = userRepository.findById(id);

        if (found.isEmpty()) throw NotFoundException.userNotFoundById(id);

        Optional<User> taken = userRepository.findByEmail(userDto.getEmail());

        if (taken.isPresent()) throw DataConflictException.emailTaken(userDto.getEmail());

        Optional<Role> role = roleRepository.findByName(userDto.getRole());

        if (role.isEmpty()) throw NotFoundException.roleNotFound(userDto.getRole());

        return userRepository.save(
                User.builder()
                        .id(id)
                        .email(userDto.getEmail())
                        .password(userDto.getIsPasswordEncrypted()?
                                userDto.getPassword() :
                                passwordEncoder.encode(userDto.getPassword())
                        )
                        .role(role.get())
                        .build()
        );
    }

    @Override
    public User delete(Long id) {
        Optional<User> found = userRepository.findById(id);

        if (found.isEmpty()) throw NotFoundException.userNotFoundById(id);

        userRepository.deleteById(id);

        return found.get();
    }
}
