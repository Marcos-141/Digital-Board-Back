package com.DigitalBoardMembers.DigitalBoard.service.impl;

import com.DigitalBoardMembers.DigitalBoard.dto.request.TopicRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.TopicResponseDTO;
import com.DigitalBoardMembers.DigitalBoard.entity.Topic;
import com.DigitalBoardMembers.DigitalBoard.mappers.TopicMappers;
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

    @Override
    public TopicResponseDTO createTopic(TopicRequestDTO request) {

        Topic topic = topicMappers.toEntity(request);
        Topic savedTopic = topicRepository.save(topic);
        return topicMappers.toResponseDTO(savedTopic);

    }

    @Override
    @Transactional(readOnly = true)
    public TopicResponseDTO getTopicById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado con el ID: " + id));

        return topicMappers.toResponseDTO(topic);
    }

    @Override
    public List<TopicResponseDTO> getAllTopics() {
        return topicRepository.findAll()
                .stream()
                .map(topicMappers::toResponseDTO)
                .toList();
    }

    @Override
    public void deleteTopic(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new RuntimeException("Tema no encontrado con el id: " + id);
        }
        topicRepository.deleteById(id);
    }
}