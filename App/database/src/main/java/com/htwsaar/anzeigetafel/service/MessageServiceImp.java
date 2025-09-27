package com.htwsaar.anzeigetafel.service;

import com.htwsaar.anzeigetafel.model.Message;
import com.htwsaar.anzeigetafel.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/**
 * MessageServiceImp
 */
@Service
public class MessageServiceImp implements MessageService {
    private MessageRepository messageRepository;

    @Autowired
    public MessageServiceImp(MessageRepository message) {
        this.messageRepository = message;
    }
    /**
     * Create a new message
     * @param message
     */
    @Override
    public void createMessage(Message message) {
        messageRepository.save(message);
    }
    /**
     * Delete a message
     * @param messageID
     */
    @Override
    public void deleteMessage(int messageID) {
        messageRepository.deleteById(messageID);
    }
    /**
     * Update a message
     * @param messageID
     * @param message
     */
    @Override
    public void updateMessage(int messageID, Message message) {
        Optional<Message> messageOptional = this.messageRepository.findById(messageID);
        if (messageOptional.isPresent()) {
            this.messageRepository.save(message);
        } else {
            throw new RuntimeException("Message not founded");
        }

    }
    /**
     * Get all messages
     * @return List<Message>
     */
    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }
    /**
     * Get a message by id
     * @param id
     * @return Message
     */

    @Override
    public Message getMessageById(int id) {
        if (messageRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Message not found");
        }else
            return messageRepository.findById(id).get();
    }
}

