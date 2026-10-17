package com.DigitalBoardMembers.DigitalBoard.service;

import com.DigitalBoardMembers.DigitalBoard.dto.request.PostRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.PostResponseDTO;

import java.util.List;

public interface PostService {

    PostResponseDTO createPost(PostRequestDTO request);
    PostResponseDTO getPostById(Long id);
    List<PostResponseDTO> getPostsByTopicId(Long topicId);
    PostResponseDTO  updatePost(Long id, PostRequestDTO request);
    void deletePost(Long id);

}