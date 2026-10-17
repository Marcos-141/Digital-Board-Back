package com.DigitalBoardMembers.DigitalBoard.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank(message = "el nombre es obligatorio")
        String nombre,

        @Email(message = "el email debe ser valido")
        @NotNull(message = "el email debe ser obligatorio")
        String email,

        @NotBlank
        @Size(min = 8, message = "la contraseña debe tener minimo 8 caracteres")
        String password

) {
}