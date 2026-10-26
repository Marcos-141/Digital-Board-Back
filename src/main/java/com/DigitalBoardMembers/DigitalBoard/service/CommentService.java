package com.DigitalBoardMembers.DigitalBoard.service;

import com.DigitalBoardMembers.DigitalBoard.dto.request.CommentRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.CommentResponseDTO;

import java.util.List;

public interface CommentService {

    CommentResponseDTO createComment(CommentRequestDTO request);

    CommentResponseDTO getCommentById(Long id);

    List<CommentResponseDTO> getCommentsByPostId(Long postId);

    CommentResponseDTO updateComment(Long id, CommentRequestDTO request);

    void deleteComment(Long id);
}