package com.DigitalBoardMembers.DigitalBoard.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TopicRequestDTO(

        @NotBlank(message = "el tema debe tener nombre ")
        String topicName,

        @NotBlank(message = "el tema debe tener una descripción")
        String topicDescription

) {
}