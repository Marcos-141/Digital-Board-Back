package com.DigitalBoardMembers.DigitalBoard.service;

import com.DigitalBoardMembers.DigitalBoard.dto.request.UserRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.UserResponseDTO;
import com.DigitalBoardMembers.DigitalBoard.entity.RoleName;
import com.DigitalBoardMembers.DigitalBoard.entity.User;
import com.DigitalBoardMembers.DigitalBoard.exceptions.DuplicateResourceException;
import com.DigitalBoardMembers.DigitalBoard.exceptions.ResourceNotFoundException;
import com.DigitalBoardMembers.DigitalBoard.mappers.UserMappers;
import com.DigitalBoardMembers.DigitalBoard.repository.CommentRepository;
import com.DigitalBoardMembers.DigitalBoard.repository.UserRepository;
import com.DigitalBoardMembers.DigitalBoard.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private UserMappers userMappers = new UserMappers();

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Marcos");
        user.setEmail("marcos@test.com");
        user.setPassword("encodedPassword123");
        user.setRole(RoleName.STUDENT);

        userRequestDTO = new UserRequestDTO("Marcos", "marcos@test.com", "plainPassword123");
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByEmail("marcos@test.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponseDTO response = userService.createUser(userRequestDTO);

        assertNotNull(response);
        assertEquals(1L, response.userId());
        assertEquals("Marcos", response.nombre());
        assertEquals("marcos@test.com", response.email());
        assertEquals(RoleName.STUDENT, response.role());
        verify(passwordEncoder).encode("plainPassword123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsDuplicateResourceException() {
        when(userRepository.existsByEmail("marcos@test.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userRequestDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(1L, response.userId());
    }

    @Test
    void getUserById_NotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("marcos@test.com", 1L)).thenReturn(false);
        when(passwordEncoder.encode("plainPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.updateUser(1L, userRequestDTO);

        assertNotNull(response);
        assertEquals(RoleName.STUDENT, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_EmailTakenByAnother_ThrowsDuplicateResourceException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("marcos@test.com", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.updateUser(1L, userRequestDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(commentRepository).deleteByCommentUserId(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound_ThrowsResourceNotFoundException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(99L));
    }
}
