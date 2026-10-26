package com.DigitalBoardMembers.DigitalBoard.mappers;

import com.DigitalBoardMembers.DigitalBoard.dto.request.PostRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.PostResponseDTO;
import com.DigitalBoardMembers.DigitalBoard.entity.Post;
import com.DigitalBoardMembers.DigitalBoard.entity.Topic;
import com.DigitalBoardMembers.DigitalBoard.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PostMappers {

    public Post toEntity(PostRequestDTO dto, Topic topic, User user) {
        if (dto == null) return null;

        Post post = new Post();
        post.setTitle(dto.title());
        post.setDescription(dto.description());
        post.setImageUrl(dto.image());

        post.setPostUser(user);
        post.setPostTopic(topic);

        return post;
    }

    public PostResponseDTO toResponseDTO(Post post) {
        if (post == null) return null;

        return new PostResponseDTO(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getImageUrl(),
                post.getPostUser() != null ? post.getPostUser().getId() : null,
                post.getPostUser() != null ? post.getPostUser().getName() : null,
                post.getPostTopic() != null ? post.getPostTopic().getId() : null,
                post.getPostTopic() != null ? post.getPostTopic().getTopicName() : null,
                post.getCreatedAt(),
                post.getUpdateAt()
        );
    }
}