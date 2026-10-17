package com.DigitalBoardMembers.DigitalBoard.service.impl;

import com.DigitalBoardMembers.DigitalBoard.dto.request.CommentRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.CommentResponseDTO;
import com.DigitalBoardMembers.DigitalBoard.entity.Comment;
import com.DigitalBoardMembers.DigitalBoard.entity.Post;
import com.DigitalBoardMembers.DigitalBoard.entity.User;
import com.DigitalBoardMembers.DigitalBoard.mappers.CommentMappers;
import com.DigitalBoardMembers.DigitalBoard.repository.CommentRepository;
import com.DigitalBoardMembers.DigitalBoard.repository.PostRepository;
import com.DigitalBoardMembers.DigitalBoard.repository.UserRepository;
import com.DigitalBoardMembers.DigitalBoard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMappers commentMappers;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public CommentResponseDTO createComment(CommentRequestDTO request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new RuntimeException("Post no encontrado"));


        Comment comment = commentMappers.toEntity(request, post, user);
        Comment savedComment = commentRepository.save(comment);

        return commentMappers.toResponseDTO(savedComment);

    }

    @Override
    public CommentResponseDTO getCommentById(Long id) {
        Comment  comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        return commentMappers.toResponseDTO(comment);
    }

    @Override
    public List<CommentResponseDTO> getCommentsByTopicId(Long postId) {
        return commentRepository.findByCommentPostId(postId)
                .stream()
                .map(commentMappers::toResponseDTO)
                .toList();
    }

    @Override
    public CommentResponseDTO updateComment(Long id, CommentRequestDTO request) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con el ID: " + id));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado "));

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        Comment updateComment = commentMappers.toEntity(request, post, user);
        updateComment.setId(existingComment.getId());

        Comment updatedComment = commentRepository.save(updateComment);
        return commentMappers.toResponseDTO(updatedComment);
    }

    @Override
    public void deleteComment(Long id){
        if(!commentRepository.existsById(id)){
            throw new RuntimeException("Commentario no encontrado");
        }

        commentRepository.deleteById(id);
    }
}