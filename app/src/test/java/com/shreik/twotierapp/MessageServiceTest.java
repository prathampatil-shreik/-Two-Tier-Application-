package com.shreik.twotierapp;

import com.shreik.twotierapp.exception.MessageNotFoundException;
import com.shreik.twotierapp.model.Message;
import com.shreik.twotierapp.repository.MessageRepository;
import com.shreik.twotierapp.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    private Message sampleMessage;

    @BeforeEach
    void setUp() {
        sampleMessage = new Message();
        sampleMessage.setId(1L);
        sampleMessage.setName("Prathamesh");
        sampleMessage.setMessage("Hello from the two-tier application");
        sampleMessage.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createMessage_savesAndReturnsMessage() {
        when(messageRepository.save(any(Message.class))).thenReturn(sampleMessage);

        Message result = messageService.createMessage(sampleMessage);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Prathamesh");
    }

    @Test
    void getAllMessages_returnsListOfMessages() {
        when(messageRepository.findAll()).thenReturn(List.of(sampleMessage));

        List<Message> result = messageService.getAllMessages();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Prathamesh");
    }

    @Test
    void getMessageById_returnsMessage_whenFound() {
        when(messageRepository.findById(1L)).thenReturn(Optional.of(sampleMessage));

        Message result = messageService.getMessageById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getMessageById_throwsException_whenNotFound() {
        when(messageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.getMessageById(99L))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessageContaining("99");
    }
}
