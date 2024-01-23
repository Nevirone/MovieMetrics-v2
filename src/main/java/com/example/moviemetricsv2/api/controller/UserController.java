package com.example.moviemetricsv2.api.controller;

import com.example.moviemetricsv2.api.dto.UserDto;
import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.model.User;
import com.example.moviemetricsv2.api.response.UserResponse;
import com.example.moviemetricsv2.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController extends BaseController implements ICrudController<User, UserDto, UserResponse> {
    private final UserService userService;

    @Override
    @PreAuthorize("hasAuthority('CREATE_USERS')")
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserDto userDto)
            throws DataConflictException {
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(userService.create(userDto)));
    }

    @Override
    @PreAuthorize("hasAuthority('DISPLAY_USERS')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAll().stream().map(UserResponse::new).toList());
    }

    @Override
    @PreAuthorize("hasAuthority('DISPLAY_USERS')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable Long id)
            throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(userService.get(id)));
    }

    @Override
    @PreAuthorize("hasAuthority('UPDATE_USERS')")
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserDto userDto)
            throws NotFoundException, DataConflictException {
        return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(userService.update(id, userDto)));
    }

    @Override
    @PreAuthorize("hasAuthority('DELETE_USERS')")
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> delete(@PathVariable Long id)
            throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(userService.delete(id)));
    }
}
