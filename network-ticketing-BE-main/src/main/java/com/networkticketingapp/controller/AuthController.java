package com.networkticketingapp.controller;

import com.networkticketingapp.dto.*;

import com.networkticketingapp.entity.Role;
import com.networkticketingapp.entity.User;
import com.networkticketingapp.repository.UserRepository;
import com.networkticketingapp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successfully");
    }

    @PutMapping("/reset-email")
    public ResponseEntity<?> resetEmail(
            @RequestBody ResetEmailRequest request) {

        User user = userRepository.findByEmail(request.getOldEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(request.getNewEmail());
        userRepository.save(user);

        return ResponseEntity.ok("Email updated successfully");
    }



    // 🔹 REGISTER
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .build();

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    // 🔹 LOGIN
    // 🔹 LOGIN
    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // ✅ SAFETY CHECK (THIS FIXES 400)
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Email or password missing");
        }

        return userRepository.findByEmail(request.getEmail())
                .map(user -> {

                    if (!passwordEncoder.matches(
                            request.getPassword(),
                            user.getPassword())) {

                        return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body("Invalid credentials");
                    }

                    String token = jwtUtil.generateToken(user.getEmail());

                    LoginResponse response = new LoginResponse(
                            token,
                            user.getRole().name(),
                            user.getId(),
                            user.getEmail()
                    );

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() ->
                        ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body("User not found")
                );
    }

}
