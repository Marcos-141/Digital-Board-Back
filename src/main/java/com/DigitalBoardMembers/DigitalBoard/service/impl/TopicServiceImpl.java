package com.DigitalBoardMembers.DigitalBoard.service.impl;

import com.DigitalBoardMembers.DigitalBoard.dto.request.TopicRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.TopicResponseDTO;
import com.DigitalBoardMembers.DigitalBoard.entity.Topic;
import com.DigitalBoardMembers.DigitalBoard.exceptions.BadRequestException;
import com.DigitalBoardMembers.DigitalBoard.exceptions.DuplicateResourceException;
import com.DigitalBoardMembers.DigitalBoard.exceptions.ResourceNotFoundException;
import com.DigitalBoardMembers.DigitalBoard.mappers.TopicMappers;
import com.DigitalBoardMembers.DigitalBoard.repository.PostRepository;
import com.DigitalBoardMembers.DigitalBoard.repository.TopicRepository;
import com.DigitalBoardMembers.DigitalBoard.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final TopicMappers topicMappers;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public TopicResponseDTO createTopic(TopicRequestDTO request) {
        if (topicRepository.existsByTopicName(request.topicName())) {
            throw new DuplicateResourceException("Ya existe un tema con el nombre: " + request.topicName());
        }

        Topic topic = topicMappers.toEntity(request);
        Topic savedTopic = topicRepository.save(topic);
        return topicMappers.toResponseDTO(savedTopic);
    }

    @Override
    @Transactional(readOnly = true)
    public TopicResponseDTO getTopicById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con el ID: " + id));

        return topicMappers.toResponseDTO(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicResponseDTO> getAllTopics() {
        return topicRepository.findAll()
                .stream()
                .map(topicMappers::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public TopicResponseDTO updateTopic(Long id, TopicRequestDTO request) {
        Topic existingTopic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con el ID: " + id));

        if (topicRepository.existsByTopicNameAndIdNot(request.topicName(), id)) {
            throw new DuplicateResourceException("Ya existe otro tema con el nombre: " + request.topicName());
        }

        existingTopic.setTopicName(request.topicName());
        existingTopic.setTopicDescription(request.topicDescription());

        Topic savedTopic = topicRepository.save(existingTopic);
        return topicMappers.toResponseDTO(savedTopic);
    }

    @Override
    @Transactional
    public void deleteTopic(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tema no encontrado con el ID: " + id);
        }

        if (postRepository.existsByPostTopicId(id)) {
            throw new BadRequestException("No se puede eliminar el tema porque contiene publicaciones asociadas");
        }

        topicRepository.deleteById(id);
    }
}