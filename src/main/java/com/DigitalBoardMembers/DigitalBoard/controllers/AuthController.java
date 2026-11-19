package com.DigitalBoardMembers.DigitalBoard.controllers;

import com.DigitalBoardMembers.DigitalBoard.dto.request.LoginRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.request.TeacherRegistrationRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@RestController
@RequestMapping(ApiConstants.AUTH)
@RequiredArgsConstructor
public class AuthController {
    private static final String USER_ID = "authenticatedUserId";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMappers userMappers;
    private final UserService userService;
    private final SecurityContextRepository securityContextRepository;

    @Value("${app.auth.teacher-invitation-code:}")
    private String teacherInvitationCode;

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        User user = userRepository.findByEmail(request.email())
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.getPassword()))
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        HttpSession session = servletRequest.getSession(true);
        session.setAttribute(USER_ID, user.getId());
        var role = user.getRole() == null ? com.DigitalBoardMembers.DigitalBoard.entity.RoleName.STUDENT : user.getRole();
        var authentication = new UsernamePasswordAuthenticationToken(
                user.getId(), null, List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, servletRequest, servletResponse);
        return ResponseEntity.ok(userMappers.toResponseDTO(user));
    }

    @PostMapping("/register/teacher")
    public ResponseEntity<UserResponseDTO> registerTeacher(@Valid @RequestBody TeacherRegistrationRequestDTO request) {
        byte[] expected = teacherInvitationCode.getBytes(StandardCharsets.UTF_8);
        byte[] supplied = request.invitationCode().getBytes(StandardCharsets.UTF_8);
        if (expected.length == 0 || !MessageDigest.isEqual(expected, supplied)) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "Código de invitación inválido");
        }
        UserResponseDTO created = userService.createTeacher(request.toUserRequest());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
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
        SecurityContextHolder.clearContext();
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
}
