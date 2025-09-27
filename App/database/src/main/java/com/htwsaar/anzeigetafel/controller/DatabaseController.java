package com.htwsaar.anzeigetafel.controller;

import com.htwsaar.anzeigetafel.model.DisplayBoard;
import com.htwsaar.anzeigetafel.model.Message;
import com.htwsaar.anzeigetafel.model.User;
import com.htwsaar.anzeigetafel.service.DisplayBoardService;
import com.htwsaar.anzeigetafel.service.MessageService;
import com.htwsaar.anzeigetafel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DatabaseController {

    private DisplayBoardService displayBoardService;
    private UserService userService;
    private MessageService messageService;

    @Autowired
    public DatabaseController(DisplayBoardService displayBoardService, UserService userService, MessageService messageService) {
        this.displayBoardService = displayBoardService;
        this.userService = userService;
        this.messageService = messageService;
    }


    //*************** user functions  **************************\\

    public void createUser(User user) {
        userService.createUser(user);
    }

    public void deleteUserById(int id) {
        userService.deleteuserById(id);
    }

    public User getUserById(int id) {
        return userService.findUserById(id);
    }

    public User getUserByName(String name) {
        return userService.findUserByName(name);

    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    //**************** display board functions *********************\\



    public void createBoard( DisplayBoard board) {
        displayBoardService.crateBoard(board);
    }

    public void deleteBoardById( int id) {
        displayBoardService.deleteBoard(id);
    }

    public DisplayBoard getBoardByName(String name) {
        return displayBoardService.getBoard(name);
    }

    public List<DisplayBoard> getAllBoards() {
        return displayBoardService.getAllBoards();
    }

    //****************** Messages functions ********************************\\

    public void createMessage(Message message) {
        messageService.createMessage(message);
    }

    public void deleteMessageById( int id) {
        messageService.deleteMessage(id);
    }

    public void updateMessage( int id,  Message message) {
        messageService.updateMessage(id, message);
    }

    public Message getMessageById(int id) {
        return messageService.getMessageById(id);
    }

    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }
}
