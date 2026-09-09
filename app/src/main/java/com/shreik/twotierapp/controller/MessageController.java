package com.shreik.twotierapp.controller;

import com.shreik.twotierapp.model.Message;
import com.shreik.twotierapp.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        logger.info("POST /api/messages - received request from name: {}", message.getName());
        Message created = messageService.createMessage(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        logger.info("GET /api/messages - retrieving all messages");
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        logger.info("GET /api/messages/{} - retrieving message by id", id);
        return ResponseEntity.ok(messageService.getMessageById(id));
    }
}
