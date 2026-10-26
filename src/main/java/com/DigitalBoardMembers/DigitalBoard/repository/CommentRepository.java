package com.DigitalBoardMembers.DigitalBoard.repository;

import com.DigitalBoardMembers.DigitalBoard.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByCommentPostId(Long postId);

    List<Comment> findByCommentUserId(Long userId);

    void deleteByCommentPostId(Long postId);

    void deleteByCommentUserId(Long userId);
}