package com.DigitalBoardMembers.DigitalBoard.controllers;

import com.DigitalBoardMembers.DigitalBoard.dto.request.TopicRequestDTO;
import com.DigitalBoardMembers.DigitalBoard.dto.response.TopicResponseDTO;
import com.DigitalBoardMembers.DigitalBoard.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.TOPICS)
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<TopicResponseDTO> createTopic(@Valid @RequestBody TopicRequestDTO request) {
        TopicResponseDTO response = topicService.createTopic(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseDTO> getTopicById(@PathVariable Long id) {
        TopicResponseDTO response = topicService.getTopicById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TopicResponseDTO>> getAllTopics() {
        List<TopicResponseDTO> response = topicService.getAllTopics();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<TopicResponseDTO> updateTopic(@PathVariable Long id, @Valid @RequestBody TopicRequestDTO request) {
        TopicResponseDTO response = topicService.updateTopic(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }
}
