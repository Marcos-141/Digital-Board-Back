package com.DigitalBoardMembers.DigitalBoard.service;

import com.DigitalBoardMembers.DigitalBoard.dto.request.TopicRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.TopicResponseDTO;

import java.util.List;

public interface TopicService {

    TopicResponseDTO createTopic(TopicRequestDTO request);

    TopicResponseDTO getTopicById(Long id);

    List<TopicResponseDTO> getAllTopics();

    TopicResponseDTO updateTopic(Long id, TopicRequestDTO request);

    void deleteTopic(Long id);
}