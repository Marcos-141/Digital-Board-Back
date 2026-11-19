package com.DigitalBoardMembers.DigitalBoard;

import com.DigitalBoardMembers.DigitalBoard.entity.RoleName;
import com.DigitalBoardMembers.DigitalBoard.entity.User;
import com.DigitalBoardMembers.DigitalBoard.repository.TopicRepository;
import com.DigitalBoardMembers.DigitalBoard.repository.UserRepository;
import com.DigitalBoardMembers.DigitalBoard.service.UserService;
import com.DigitalBoardMembers.DigitalBoard.dto.request.UserRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "app.auth.teacher-invitation-code=test-invitation")
@AutoConfigureMockMvc
class AuthAndTopicAuthorizationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private TopicRepository topicRepository;
    @Autowired private UserService userService;
    @Autowired private ObjectMapper objectMapper;

    private final List<String> emails = new ArrayList<>();
    private final List<String> topicNames = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        topicNames.forEach(name -> topicRepository.findByTopicName(name).ifPresent(topicRepository::delete));
        emails.forEach(email -> userRepository.findByEmail(email).ifPresent(userRepository::delete));
    }

    @Test
    void existingStudentCanLoginButCannotCreateTopic() throws Exception {
        String email = uniqueEmail("student");
        userService.createUser(new UserRequestDTO("Estudiante", email, "password123"));
        User legacyStudent = userRepository.findByEmail(email).orElseThrow();
        legacyStudent.setRole(null);
        userRepository.save(legacyStudent);

        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"password123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        assertNotNull(session);
        String topic = uniqueTopic();
        mockMvc.perform(post("/api/v1/topics").session(session).contentType(MediaType.APPLICATION_JSON)
                        .content(topicJson(topic)))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/auth/logout").session(session))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/auth/me").session(session))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/v1/topics/1").session(session).contentType(MediaType.APPLICATION_JSON)
                        .content(topicJson(topic)))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/v1/topics/1").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void teacherRegistrationRejectsMissingOrInvalidInvitation() throws Exception {
        String email = uniqueEmail("invalid-teacher");
        String payload = teacherJson(email, "wrong-code");
        mockMvc.perform(post("/api/v1/auth/register/teacher").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isForbidden());
        assertTrue(userRepository.findByEmail(email).isEmpty());

        String missingCodeEmail = uniqueEmail("missing-code");
        mockMvc.perform(post("/api/v1/auth/register/teacher").contentType(MediaType.APPLICATION_JSON)
                        .content(teacherJson(missingCodeEmail, "")))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findByEmail(missingCodeEmail).isEmpty());
    }

    @Test
    void teacherCanRegisterLoginAndCreateTopic() throws Exception {
        String email = uniqueEmail("teacher");
        mockMvc.perform(post("/api/v1/auth/register/teacher").contentType(MediaType.APPLICATION_JSON)
                        .content(teacherJson(email, "test-invitation")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("TEACHER"));

        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"password123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("TEACHER"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        String topic = uniqueTopic();
        MvcResult created = mockMvc.perform(post("/api/v1/topics").session(session).contentType(MediaType.APPLICATION_JSON)
                        .content(topicJson(topic)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode createdBody = objectMapper.readTree(created.getResponse().getContentAsString());
        Long topicId = createdBody.get("id").asLong();
        topicNames.add(topic + "-updated");
        mockMvc.perform(put("/api/v1/topics/" + topicId).session(session).contentType(MediaType.APPLICATION_JSON)
                        .content(topicJson(topic + "-updated")))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/topics/" + topicId).session(session))
                .andExpect(status().isNoContent());
    }

    @Test
    void adminCanCreateTopic() throws Exception {
        String email = uniqueEmail("admin");
        userService.createUser(new UserRequestDTO("Admin", email, "password123"));
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setRole(RoleName.ADMIN);
        userRepository.save(user);

        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"password123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        String topic = uniqueTopic();
        mockMvc.perform(post("/api/v1/topics").session(session).contentType(MediaType.APPLICATION_JSON)
                        .content(topicJson(topic)))
                .andExpect(status().isCreated());
    }

    private String uniqueEmail(String prefix) {
        String email = prefix + "-" + UUID.randomUUID() + "@test.example";
        emails.add(email);
        return email;
    }

    private String uniqueTopic() {
        String topic = "test-topic-" + UUID.randomUUID();
        topicNames.add(topic);
        return topic;
    }

    private String teacherJson(String email, String code) {
        return """
                {"nombre":"Profesor","email":"%s","password":"password123","invitationCode":"%s"}
                """.formatted(email, code);
    }

    private String topicJson(String name) {
        return """
                {"topicName":"%s","topicDescription":"Descripción de prueba"}
                """.formatted(name);
    }
}
