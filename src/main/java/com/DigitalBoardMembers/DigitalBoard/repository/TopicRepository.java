package com.DigitalBoardMembers.DigitalBoard.repository;

import com.DigitalBoardMembers.DigitalBoard.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    boolean existsByTopicName(String topicName);

    boolean existsByTopicNameAndIdNot(String topicName, Long id);

    Optional<Topic> findByTopicName(String topicName);
}