package com.DigitalBoardMembers.DigitalBoard.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank(message = "el nombre es obligatorio")
        String nombre,

        @NotBlank(message = "el email es obligatorio")
        @Email(message = "el email debe ser valido")
        String email,

        @NotBlank(message = "la contraseña es obligatoria")
        @Size(min = 8, message = "la contraseña debe tener minimo 8 caracteres")
        String password
) {
}