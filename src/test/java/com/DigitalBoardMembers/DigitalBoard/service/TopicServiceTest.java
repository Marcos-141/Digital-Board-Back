package com.DigitalBoardMembers.DigitalBoard.service;

import com.DigitalBoardMembers.DigitalBoard.dto.request.TopicRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.TopicResponseDTO;
import com.DigitalBoardMembers.DigitalBoard.entity.Topic;
import com.DigitalBoardMembers.DigitalBoard.exceptions.BadRequestException;
import com.DigitalBoardMembers.DigitalBoard.exceptions.DuplicateResourceException;
import com.DigitalBoardMembers.DigitalBoard.exceptions.ResourceNotFoundException;
import com.DigitalBoardMembers.DigitalBoard.mappers.TopicMappers;
import com.DigitalBoardMembers.DigitalBoard.repository.PostRepository;
import com.DigitalBoardMembers.DigitalBoard.repository.TopicRepository;
import com.DigitalBoardMembers.DigitalBoard.service.impl.TopicServiceImpl;
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
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private PostRepository postRepository;

    @Spy
    private TopicMappers topicMappers = new TopicMappers();

    @InjectMocks
    private TopicServiceImpl topicService;

    private Topic topic;
    private TopicRequestDTO topicRequestDTO;

    @BeforeEach
    void setUp() {
        topic = new Topic();
        topic.setId(1L);
        topic.setTopicName("Programacion");
        topic.setTopicDescription("Curso de programacion");

        topicRequestDTO = new TopicRequestDTO("Programacion", "Curso de programacion");
    }

    @Test
    void createTopic_Success() {
        when(topicRepository.existsByTopicName("Programacion")).thenReturn(false);
        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> {
            Topic t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        TopicResponseDTO response = topicService.createTopic(topicRequestDTO);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Programacion", response.topicName());
    }

    @Test
    void createTopic_DuplicateName_ThrowsDuplicateResourceException() {
        when(topicRepository.existsByTopicName("Programacion")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> topicService.createTopic(topicRequestDTO));
        verify(topicRepository, never()).save(any());
    }

    @Test
    void deleteTopic_WithExistingPosts_ThrowsBadRequestException() {
        when(topicRepository.existsById(1L)).thenReturn(true);
        when(postRepository.existsByPostTopicId(1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> topicService.deleteTopic(1L));
        verify(topicRepository, never()).deleteById(any());
    }

    @Test
    void deleteTopic_WithoutPosts_Success() {
        when(topicRepository.existsById(1L)).thenReturn(true);
        when(postRepository.existsByPostTopicId(1L)).thenReturn(false);

        topicService.deleteTopic(1L);

        verify(topicRepository).deleteById(1L);
    }
}
