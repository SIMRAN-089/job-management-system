package com.simran.job_management.controller;

import com.simran.job_management.dto.UserDTO;
import com.simran.job_management.dto.UserRequestDTO;
import com.simran.job_management.entity.User;
import com.simran.job_management.mapper.UserMapper;
import com.simran.job_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream().map(UserMapper::toDTO).collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id, Principal principal) {
        User user = userService.getUserById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        boolean isRecruiter = user.getRole() != null && principal != null
                && org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RECRUITER"));
        if (!isRecruiter && (principal == null || !user.getEmail().equalsIgnoreCase(principal.getName()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own profile");
        }
        return UserMapper.toDTO(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        return UserMapper.toDTO(userService.saveUser(user));
    }
}
