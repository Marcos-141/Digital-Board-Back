package com.DigitalBoardMembers.DigitalBoard.mappers;

import com.DigitalBoardMembers.DigitalBoard.dto.request.TopicRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.TopicResponseDTO;
import com.DigitalBoardMembers.DigitalBoard.entity.Topic;
import org.springframework.stereotype.Component;

@Component

public class TopicMappers {

    public Topic toEntity(TopicRequestDTO dto){
        if (dto == null) return null;

        Topic topic = new Topic();
        topic.setTopicName(dto.topicName());
        topic.setTopicDescription(dto.topicDescription());

        return topic;
    }

    public TopicResponseDTO toResponseDTO(Topic entity) {
        if (entity == null) return null;

        return new TopicResponseDTO(
                entity.getId(),
                entity.getTopicName(),
                entity.getTopicDescription()
        );
    }
}