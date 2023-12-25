package com.example.moviemetricsv2.api.controller;

import com.example.moviemetricsv2.api.model.MovieClassification;
import com.example.moviemetricsv2.api.model.User;
import com.example.moviemetricsv2.api.service.MovieClassificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/classifications")
@RequiredArgsConstructor
public class ClassificationController {
    private final MovieClassificationService movieClassificationService;
    @PreAuthorize("hasAuthority('DISPLAY_MOVIES')")
    @GetMapping
    public ResponseEntity<List<MovieClassification>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(movieClassificationService.getAll());
    }
}
