package com.DigitalBoardMembers.DigitalBoard.repository;

import com.DigitalBoardMembers.DigitalBoard.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface TopicRepository extends JpaRepository<Topic, Long> {


}