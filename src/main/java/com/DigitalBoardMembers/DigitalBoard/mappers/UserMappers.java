package com.DigitalBoardMembers.DigitalBoard.mappers;

import com.DigitalBoardMembers.DigitalBoard.dto.request.UserRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.UserResponseDTO;
import com.DigitalBoardMembers.DigitalBoard.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMappers {

    public User toEntity(UserRequestDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setName(dto.nombre());
        user.setEmail(dto.email());
        user.setPassword(dto.password());

        return user;
    }

    public UserResponseDTO toResponseDTO(User entity) {
        if (entity == null) return null;

        return new UserResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getRole(),
                entity.getCreatedAt()
        );
    }
}