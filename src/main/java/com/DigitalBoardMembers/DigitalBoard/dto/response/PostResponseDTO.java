package com.DigitalBoardMembers.DigitalBoard.dto.response;

import java.time.LocalDate;

public record PostResponseDTO(
        Long postId,
        String title,
        String description,
        String image,
        Long userId,
        String userName,
        Long topicId,
        String topicName,
        LocalDate createdAt,
        LocalDate updateAt
) {
    public PostResponseDTO(
            Long postId,
            String title,
            String description,
            String image,
            Long userId,
            String userName,
            Long topicId,
            String topicName
    ) {
        this(postId, title, description, image, userId, userName, topicId, topicName, null, null);
    }
}