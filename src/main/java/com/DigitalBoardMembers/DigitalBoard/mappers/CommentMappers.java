package com.DigitalBoardMembers.DigitalBoard.mappers;

import com.DigitalBoardMembers.DigitalBoard.dto.request.CommentRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.CommentResponseDTO;
import com.DigitalBoardMembers.DigitalBoard.entity.Comment;
import com.DigitalBoardMembers.DigitalBoard.entity.Post;
import com.DigitalBoardMembers.DigitalBoard.entity.User;
import org.springframework.stereotype.Component;

@Component

public class CommentMappers{

    public Comment toEntity(CommentRequestDTO dto, Post post, User user){
        if (dto == null) return null;

        Comment comment = new Comment();
        comment.setContent(dto.content());
        comment.setImage(dto.image());

        comment.setCommentUser(user);
        comment.setCommentPost(post);

        return comment;
    }

    public CommentResponseDTO toResponseDTO(Comment entity){
        if (entity == null) return null;

        return new CommentResponseDTO(
                entity.getId(),
                entity.getContent(),
                entity.getImage(),
                entity.getCommentPost() != null ? entity.getCommentPost().getId() : null,
                entity.getCommentUser() != null ? entity.getCommentUser().getId() : null,
                entity.getCommentUser() != null ? entity.getCommentUser().getName() : null

        );

    }
}