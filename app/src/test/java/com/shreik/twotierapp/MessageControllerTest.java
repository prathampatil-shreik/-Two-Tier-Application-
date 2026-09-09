package com.shreik.twotierapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shreik.twotierapp.model.Message;
import com.shreik.twotierapp.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
    }

    @Test
    void postMessage_createsAndReturns201() throws Exception {
        Message msg = new Message();
        msg.setName("Prathamesh");
        msg.setMessage("Hello from the two-tier application");

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(msg)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Prathamesh"))
                .andExpect(jsonPath("$.message").value("Hello from the two-tier application"));
    }

    @Test
    void getMessages_returnsAllMessages() throws Exception {
        Message msg = new Message();
        msg.setName("Shreik");
        msg.setMessage("Test message");
        messageRepository.save(msg);

        mockMvc.perform(get("/api/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Shreik"));
    }

    @Test
    void getMessageById_returnsMessage_whenExists() throws Exception {
        Message msg = new Message();
        msg.setName("Test User");
        msg.setMessage("Test message");
        Message saved = messageRepository.save(msg);

        mockMvc.perform(get("/api/messages/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getMessageById_returns404_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/messages/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void healthEndpoint_returnsUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
