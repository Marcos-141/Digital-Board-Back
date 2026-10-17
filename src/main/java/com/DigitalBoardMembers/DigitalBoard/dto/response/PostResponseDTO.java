package com.DigitalBoardMembers.DigitalBoard.dto.response;

public record PostResponseDTO(

        Long postId,
        String title,
        String description,
        String image,
        Long userId,
        String userName,
        Long topicId,
        String topicName
) {
}