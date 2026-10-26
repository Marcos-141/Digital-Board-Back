package com.DigitalBoardMembers.DigitalBoard.service.impl;

import com.DigitalBoardMembers.DigitalBoard.dto.request.UserRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.UserResponseDTO;
import com.DigitalBoardMembers.DigitalBoard.entity.RoleName;
import com.DigitalBoardMembers.DigitalBoard.entity.User;
import com.DigitalBoardMembers.DigitalBoard.exceptions.DuplicateResourceException;
import com.DigitalBoardMembers.DigitalBoard.exceptions.ResourceNotFoundException;
import com.DigitalBoardMembers.DigitalBoard.mappers.UserMappers;
import com.DigitalBoardMembers.DigitalBoard.repository.CommentRepository;
import com.DigitalBoardMembers.DigitalBoard.repository.UserRepository;
import com.DigitalBoardMembers.DigitalBoard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMappers userMappers;
    private final PasswordEncoder passwordEncoder;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("El email ya se encuentra registrado: " + request.email());
        }

        User user = userMappers.toEntity(request);
        if (user.getRole() == null) {
            user.setRole(RoleName.STUDENT);
        }
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);
        return userMappers.toResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el id: " + id));

        return userMappers.toResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMappers::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el id: " + id));

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("El email ya se encuentra registrado por otro usuario: " + request.email());
        }

        existingUser.setName(request.nombre());
        existingUser.setEmail(request.email());

        if (request.password() != null && !request.password().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(request.password()));
        }

        User savedUser = userRepository.save(existingUser);
        return userMappers.toResponseDTO(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("El usuario con el id " + id + " no existe, por lo que no se puede eliminar");
        }

        commentRepository.deleteByCommentUserId(id);
        userRepository.deleteById(id);
    }
}