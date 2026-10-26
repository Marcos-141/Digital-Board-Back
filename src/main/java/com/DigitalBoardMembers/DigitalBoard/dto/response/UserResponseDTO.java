package com.DigitalBoardMembers.DigitalBoard.dto.response;

import com.DigitalBoardMembers.DigitalBoard.entity.RoleName;

import java.time.LocalDate;

public record UserResponseDTO(
        Long userId,
        String nombre,
        String email,
        RoleName role,
        LocalDate createdAt
) {
    public UserResponseDTO(Long userId, String nombre, String email) {
        this(userId, nombre, email, null, null);
    }
}