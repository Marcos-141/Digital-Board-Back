package com.DigitalBoardMembers.DigitalBoard.dto.response;

public record CommentResponseDTO(

        Long commentId,
        String content,
        String image,
        Long postId,
        Long userId,
        String userName
) {
}