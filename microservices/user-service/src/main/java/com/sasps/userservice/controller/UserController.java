package com.sasps.userservice.controller;

import com.sasps.userservice.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers() {
        // Placeholder static sample user list. The real implementation should use a UserService and DB access.
        List<UserDto> users = Arrays.asList(
                new UserDto(1L, "admin", "admin@test.com", "Admin", "System"),
                new UserDto(2L, "andrei.ciobanu", "andrei.ciobanu@test.com", "Andrei", "Ciobanu")
        );
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        // Static example for the placeholder. Collab will implement the DB-backed service later.
        UserDto user = new UserDto(id, "placeholder", "placeholder@test.com", "First", "Last");
        return ResponseEntity.ok(user);
    }
}
