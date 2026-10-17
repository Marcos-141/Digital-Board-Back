package com.DigitalBoardMembers.DigitalBoard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentRequestDTO(

        @NotBlank(message = "el comentario debe contener algo")
        String content,

        // puede contener imágenes de forma opcional
        String image,

        @NotNull(message = "el id del post debe ser obligatorio ")
        Long postId,

        @NotNull(message = "el id del usuario debe ser obligatorio")
        Long userId
) {
}