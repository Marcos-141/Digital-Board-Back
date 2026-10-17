package com.DigitalBoardMembers.DigitalBoard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostRequestDTO(

        @NotBlank(message = "el post debe llevar un titulo")
        String title,

        @NotBlank(message = "el post debe tener una descripción")
        String description,

        // puede contener imágenes de forma opcional
        String image,

        @NotNull(message = "el post debe llevar el id del usuario")
        Long userId,

        @NotNull(message = "el post debe tener el id del tema")
        Long topicId

) {
}