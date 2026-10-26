package com.DigitalBoardMembers.DigitalBoard.service;

import com.DigitalBoardMembers.DigitalBoard.dto.request.PostRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.PostResponseDTO;
import com.DigitalBoardMembers.DigitalBoard.entity.Post;
import com.DigitalBoardMembers.DigitalBoard.entity.Topic;
import com.DigitalBoardMembers.DigitalBoard.entity.User;
import com.DigitalBoardMembers.DigitalBoard.exceptions.ResourceNotFoundException;
import com.DigitalBoardMembers.DigitalBoard.mappers.PostMappers;
import com.DigitalBoardMembers.DigitalBoard.repository.PostRepository;
import com.DigitalBoardMembers.DigitalBoard.repository.TopicRepository;
import com.DigitalBoardMembers.DigitalBoard.repository.UserRepository;
import com.DigitalBoardMembers.DigitalBoard.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopicRepository topicRepository;

    @Spy
    private PostMappers postMappers = new PostMappers();

    @InjectMocks
    private PostServiceImpl postService;

    private User user;
    private Topic topic;
    private Post post;
    private PostRequestDTO postRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(5L);
        user.setName("Juan");

        topic = new Topic();
        topic.setId(10L);
        topic.setTopicName("Spring Boot 4");

        post = new Post();
        post.setId(100L);
        post.setTitle("Titulo");
        post.setDescription("Descripcion");
        post.setPostUser(user);
        post.setPostTopic(topic);

        postRequestDTO = new PostRequestDTO("Titulo Actualizado", "Nueva descripcion", "img.png", 5L, 10L);
    }

    @Test
    void createPost_Success() {
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(topicRepository.findById(10L)).thenReturn(Optional.of(topic));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        });

        PostResponseDTO response = postService.createPost(postRequestDTO);

        assertNotNull(response);
        assertEquals(100L, response.postId());
        assertEquals("Titulo Actualizado", response.title());
        assertEquals(5L, response.userId());
        assertEquals(10L, response.topicId());
    }

    @Test
    void updatePost_UsesRequestUserAndTopicIds_Success() {
        // This test ensures the previously existing logic bug (using postId instead of userId and topicId) is fixed!
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(topicRepository.findById(10L)).thenReturn(Optional.of(topic));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponseDTO response = postService.updatePost(100L, postRequestDTO);

        assertNotNull(response);
        assertEquals("Titulo Actualizado", response.title());
        verify(userRepository).findById(5L);
        verify(topicRepository).findById(10L);
        verify(userRepository, never()).findById(100L);
        verify(topicRepository, never()).findById(100L);
    }

    @Test
    void getPostsByTopicId_TopicNotFound_ThrowsResourceNotFoundException() {
        when(topicRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> postService.getPostsByTopicId(999L));
    }

    @Test
    void deletePost_Success() {
        when(postRepository.existsById(100L)).thenReturn(true);

        postService.deletePost(100L);

        verify(postRepository).deleteById(100L);
    }
}
