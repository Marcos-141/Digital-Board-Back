package com.DigitalBoardMembers.DigitalBoard.service;

import com.DigitalBoardMembers.DigitalBoard.dto.request.UserRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.UserResponseDTO;

import java.util.List;

public interface UserService {
        UserResponseDTO createUser (UserRequestDTO request);
        UserResponseDTO getUserById(Long id);
        List<UserResponseDTO> getAllUsers();
        UserResponseDTO updateUser(Long id, UserRequestDTO request);
        void deleteUser(Long id);

}