
        package com.simran.job_management.service;

import com.simran.job_management.entity.User;
import com.simran.job_management.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {

        if (user.getName() == null
                || user.getName().trim().isEmpty()
                || user.getEmail() == null
                || user.getEmail().trim().isEmpty()
                || user.getPassword() == null
                || user.getPassword().length() < 8
                || user.getRole() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Name, email, role and a password of at least 8 characters are required"
            );
        }

        String email = user.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }
}
