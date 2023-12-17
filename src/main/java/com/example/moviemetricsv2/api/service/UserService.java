package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.model.Role;
import com.example.moviemetricsv2.api.model.User;
import com.example.moviemetricsv2.api.repository.IRoleRepository;
import com.example.moviemetricsv2.api.repository.IUserRepository;
import com.example.moviemetricsv2.api.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
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
    public User create(UserDto userDto) {
        if (userRepository.existsByEmailIgnoreCase(userDto.getEmail()))
            throw DataConflictException.emailTaken(userDto.getEmail());

        return userRepository.save(
                new User(userDto, roleRepository, passwordEncoder)
        );
    }

    @Override
    public User get(Long id) throws NotFoundException {
        Optional<User> found = userRepository.findById(id);

        if (found.isEmpty())
            throw NotFoundException.userNotFoundById(id);

        return found.get();
    }

    public User getByEmail(String email) {
        Optional<User> found = userRepository.findByEmailIgnoreCase(email);

        if (found.isEmpty())
            throw NotFoundException.userNotFoundByEmail(email);

        return found.get();

    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(Long id, UserDto userDto) throws DataConflictException, NotFoundException {
        if (!userRepository.existsById(id))
            throw NotFoundException.userNotFoundById(id);

        if (userRepository.existsByEmailIgnoreCase(userDto.getEmail()))
            throw DataConflictException.emailTaken(userDto.getEmail());

        User user = new User(userDto, roleRepository, passwordEncoder);
        user.setId(id);

        return userRepository.save(user);
    }

    @Override
    public User delete(Long id) throws NotFoundException {
        Optional<User> found = userRepository.findById(id);

        if (found.isEmpty())
            throw NotFoundException.userNotFoundById(id);

        userRepository.deleteById(id);

        return found.get();
    }
}
