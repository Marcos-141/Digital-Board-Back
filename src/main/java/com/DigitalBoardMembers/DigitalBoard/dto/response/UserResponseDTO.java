package com.DigitalBoardMembers.DigitalBoard.dto.response;

import java.time.LocalDate;

public record UserResponseDTO(

        Long userId,
        String nombre,
        String email

) {
}