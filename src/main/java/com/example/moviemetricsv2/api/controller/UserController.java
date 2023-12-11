package com.example.moviemetricsv2.api.controller;

import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.model.User;
import com.example.moviemetricsv2.api.request.UserDto;
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
public class UserController extends BaseController implements ICrudController<User, UserDto> {
    private final UserService userService;

    @PreAuthorize("hasAuthority('CREATE_USERS')")
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody UserDto userDto)
            throws DataConflictException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userDto));
    }

    @PreAuthorize("hasAuthority('DISPLAY_USERS')")
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAll());
    }

    @PreAuthorize("hasAuthority('DISPLAY_USERS')")
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Long id)
            throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.get(id));
    }

    @PreAuthorize("hasAuthority('UPDATE_USERS')")
    @PatchMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody UserDto userDto)
            throws NotFoundException, DataConflictException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(id, userDto));
    }

    @PreAuthorize("hasAuthority('DELETE_USERS')")
    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable Long id)
            throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.delete(id));
    }
}
