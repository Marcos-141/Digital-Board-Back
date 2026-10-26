package com.DigitalBoardMembers.DigitalBoard.service.impl;

import com.DigitalBoardMembers.DigitalBoard.dto.request.CommentRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.CommentResponseDTO;
import com.DigitalBoardMembers.DigitalBoard.entity.Comment;
import com.DigitalBoardMembers.DigitalBoard.entity.Post;
import com.DigitalBoardMembers.DigitalBoard.entity.User;
import com.DigitalBoardMembers.DigitalBoard.exceptions.ResourceNotFoundException;
import com.DigitalBoardMembers.DigitalBoard.mappers.CommentMappers;
import com.DigitalBoardMembers.DigitalBoard.repository.CommentRepository;
import com.DigitalBoardMembers.DigitalBoard.repository.PostRepository;
import com.DigitalBoardMembers.DigitalBoard.repository.UserRepository;
import com.DigitalBoardMembers.DigitalBoard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMappers commentMappers;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public CommentResponseDTO createComment(CommentRequestDTO request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el ID: " + request.userId()));

        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new ResourceNotFoundException("Publicación no encontrada con el ID: " + request.postId()));

        Comment comment = commentMappers.toEntity(request, post, user);
        Comment savedComment = commentRepository.save(comment);

        return commentMappers.toResponseDTO(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponseDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado con el ID: " + id));
        return commentMappers.toResponseDTO(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Publicación no encontrada con el ID: " + postId);
        }

        return commentRepository.findByCommentPostId(postId)
                .stream()
                .map(commentMappers::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public CommentResponseDTO updateComment(Long id, CommentRequestDTO request) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado con el ID: " + id));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el ID: " + request.userId()));

        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new ResourceNotFoundException("Publicación no encontrada con el ID: " + request.postId()));

        existingComment.setContent(request.content());
        existingComment.setImage(request.image());
        existingComment.setCommentUser(user);
        existingComment.setCommentPost(post);
        existingComment.setUpdatedAt(LocalDate.now());

        Comment updatedComment = commentRepository.save(existingComment);
        return commentMappers.toResponseDTO(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comentario no encontrado con el ID: " + id);
        }

        commentRepository.deleteById(id);
    }
}