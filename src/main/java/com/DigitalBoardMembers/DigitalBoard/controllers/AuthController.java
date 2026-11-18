package com.DigitalBoardMembers.DigitalBoard.controllers;

import com.DigitalBoardMembers.DigitalBoard.dto.request.LoginRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.UserResponseDTO;
import com.DigitalBoardMembers.DigitalBoard.entity.User;
import com.DigitalBoardMembers.DigitalBoard.exceptions.ResourceNotFoundException;
import com.DigitalBoardMembers.DigitalBoard.mappers.UserMappers;
import com.DigitalBoardMembers.DigitalBoard.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.AUTH)
@RequiredArgsConstructor
public class AuthController {
    private static final String USER_ID = "authenticatedUserId";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMappers userMappers;

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO request, HttpSession session) {
        User user = userRepository.findByEmail(request.email())
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.getPassword()))
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        session.setAttribute(USER_ID, user.getId());
        return ResponseEntity.ok(userMappers.toResponseDTO(user));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(HttpSession session) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión no autenticada");
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el id: " + userId));
        return ResponseEntity.ok(userMappers.toResponseDTO(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
}
