package com.DigitalBoardMembers.DigitalBoard.service;

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
import com.DigitalBoardMembers.DigitalBoard.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Spy
    private CommentMappers commentMappers = new CommentMappers();

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Post post;
    private Comment comment;
    private CommentRequestDTO commentRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(3L);
        user.setName("Carlos");

        post = new Post();
        post.setId(20L);

        comment = new Comment();
        comment.setId(50L);
        comment.setContent("Buen post");
        comment.setCommentUser(user);
        comment.setCommentPost(post);

        commentRequestDTO = new CommentRequestDTO("Comentario actualizado", "img.jpg", 20L, 3L);
    }

    @Test
    void createComment_Success() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(postRepository.findById(20L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            c.setId(50L);
            return c;
        });

        CommentResponseDTO response = commentService.createComment(commentRequestDTO);

        assertNotNull(response);
        assertEquals(50L, response.commentId());
        assertEquals("Comentario actualizado", response.content());
        assertEquals(3L, response.userId());
        assertEquals(20L, response.postId());
    }

    @Test
    void updateComment_UsesRequestUserAndPostIds_Success() {
        // This test ensures the previously existing logic bug (using commentId instead of userId and postId) is fixed!
        when(commentRepository.findById(50L)).thenReturn(Optional.of(comment));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(postRepository.findById(20L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponseDTO response = commentService.updateComment(50L, commentRequestDTO);

        assertNotNull(response);
        assertEquals("Comentario actualizado", response.content());
        verify(userRepository).findById(3L);
        verify(postRepository).findById(20L);
        verify(userRepository, never()).findById(50L);
        verify(postRepository, never()).findById(50L);
    }

    @Test
    void getCommentsByPostId_PostNotFound_ThrowsResourceNotFoundException() {
        when(postRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentsByPostId(999L));
    }
}
