package com.DigitalBoardMembers.DigitalBoard.service.impl;

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
import com.DigitalBoardMembers.DigitalBoard.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMappers postMappers;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    @Override
    @Transactional
    public PostResponseDTO createPost(PostRequestDTO request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el ID: " + request.userId()));

        Topic topic = topicRepository.findById(request.topicId())
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con el ID: " + request.topicId()));

        Post post = postMappers.toEntity(request, topic, user);
        Post savedPost = postRepository.save(post);

        return postMappers.toResponseDTO(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponseDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publicación no encontrada con el ID: " + id));
        return postMappers.toResponseDTO(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDTO> getPostsByTopicId(Long topicId) {
        if (!topicRepository.existsById(topicId)) {
            throw new ResourceNotFoundException("Tema no encontrado con el ID: " + topicId);
        }

        return postRepository.findByPostTopicId(topicId)
                .stream()
                .map(postMappers::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public PostResponseDTO updatePost(Long id, PostRequestDTO request) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publicación no encontrada con el ID: " + id));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el ID: " + request.userId()));

        Topic topic = topicRepository.findById(request.topicId())
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con el ID: " + request.topicId()));

        existingPost.setTitle(request.title());
        existingPost.setDescription(request.description());
        existingPost.setImageUrl(request.image());
        existingPost.setPostUser(user);
        existingPost.setPostTopic(topic);
        existingPost.setUpdateAt(LocalDate.now());

        Post savedPost = postRepository.save(existingPost);
        return postMappers.toResponseDTO(savedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Publicación no encontrada con el ID: " + id);
        }
        postRepository.deleteById(id);
    }
}