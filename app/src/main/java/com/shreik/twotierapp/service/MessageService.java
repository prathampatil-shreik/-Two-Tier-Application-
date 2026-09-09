package com.shreik.twotierapp.service;

import com.shreik.twotierapp.exception.MessageNotFoundException;
import com.shreik.twotierapp.model.Message;
import com.shreik.twotierapp.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message createMessage(Message message) {
        logger.info("Creating new message from name: {}", message.getName());
        Message saved = messageRepository.save(message);
        logger.info("Message created successfully with id: {}", saved.getId());
        return saved;
    }

    public List<Message> getAllMessages() {
        logger.info("Retrieving all messages from database");
        List<Message> messages = messageRepository.findAll();
        logger.info("Retrieved {} messages", messages.size());
        return messages;
    }

    public Message getMessageById(Long id) {
        logger.info("Retrieving message with id: {}", id);
        return messageRepository.findById(id).orElseThrow(() -> {
            logger.error("ERROR: Message not found with id: {}", id);
            return new MessageNotFoundException("Message not found with id: " + id);
        });
    }
}
