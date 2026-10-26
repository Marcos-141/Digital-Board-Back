package com.DigitalBoardMembers.DigitalBoard.dto.response;

import java.time.LocalDate;

public record CommentResponseDTO(
        Long commentId,
        String content,
        String image,
        Long postId,
        Long userId,
        String userName,
        LocalDate createdAt,
        LocalDate updatedAt
) {
    public CommentResponseDTO(
            Long commentId,
            String content,
            String image,
            Long postId,
            Long userId,
            String userName
    ) {
        this(commentId, content, image, postId, userId, userName, null, null);
    }
}