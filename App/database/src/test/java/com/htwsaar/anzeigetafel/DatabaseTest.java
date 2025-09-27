package com.htwsaar.anzeigetafel;

import com.htwsaar.anzeigetafel.controller.DatabaseController;
import com.htwsaar.anzeigetafel.service.DisplayBoardService;
import com.htwsaar.anzeigetafel.service.MessageService;
import com.htwsaar.anzeigetafel.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import com.htwsaar.anzeigetafel.model.User;
import com.htwsaar.anzeigetafel.model.DisplayBoard;
import com.htwsaar.anzeigetafel.model.Message;
import com.htwsaar.anzeigetafel.repository.DisplayBoardRepository;



import org.junit.runner.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = AnzeigeTafelApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@ExtendWith(MockitoExtension.class)
class DatabaseTest {

    @Mock
    private UserService userService;

    @Mock
    private DisplayBoardService displayBoardService;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private DatabaseController databaseController;

    //*************** User functions tests  **************************\\

    @Test
    public void testCreateUser() {
        User user = new User();
        databaseController.createUser(user);
        verify(userService).createUser(user);
    }

    @Test
    public void testDeleteUserById() {
        int id = 1;
        databaseController.deleteUserById(id);
        verify(userService).deleteuserById(id);
    }

    @Test
    public void testGetUserById() {
        int id = 1;
        User expectedUser = new User();
        when(userService.findUserById(id)).thenReturn(expectedUser);
        User actualUser = databaseController.getUserById(id);
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    public void testGetUserByName() {
        String name = "testUser";
        User expectedUser = new User();
        when(userService.findUserByName(name)).thenReturn(expectedUser);
        User actualUser = databaseController.getUserByName(name);
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    public void testGetAllUsers() {
        List<User> expectedUsers = new ArrayList<>();
        when(userService.getAllUsers()).thenReturn(expectedUsers);
        List<User> actualUsers = databaseController.getAllUsers();
        assertThat(actualUsers).isEqualTo(expectedUsers);
    }

    //**************** display board functions tests *********************\\

    @Test
    public void testCreateBoard() {
        DisplayBoard board = new DisplayBoard();
        databaseController.createBoard(board);
        verify(displayBoardService).crateBoard(board);
    }

    @Test
    public void testDeleteBoardById() {
        int id = 1;
        databaseController.deleteBoardById(id);
        verify(displayBoardService).deleteBoard(id);
    }

    @Test
    public void testGetBoardByName() {
        String name = "testBoard";
        DisplayBoard expectedBoard = new DisplayBoard();
        when(displayBoardService.getBoard(name)).thenReturn(expectedBoard);
        DisplayBoard actualBoard = databaseController.getBoardByName(name);
        assertThat(actualBoard).isEqualTo(expectedBoard);
    }

    @Test
    public void testGetAllBoards() {
        List<DisplayBoard> expectedBoards = new ArrayList<>();
        when(displayBoardService.getAllBoards()).thenReturn(expectedBoards);
        List<DisplayBoard> actualBoards = databaseController.getAllBoards();
        assertThat(actualBoards).isEqualTo(expectedBoards);
    }

    //****************** Messages functions tests ********************************\\

    @Test
    public void testCreateMessage() {
        Message message = new Message();
        databaseController.createMessage(message);
        verify(messageService).createMessage(message);
    }

    @Test
    public void testDeleteMessageById() {
        int id = 1;
        databaseController.deleteMessageById(id);
        verify(messageService).deleteMessage(id);
    }

    @Test
    public void testUpdateMessage() {
        int id = 1;
        Message message = new Message();
        databaseController.updateMessage(id, message);
        verify(messageService).updateMessage(id, message);
    }

    @Test
    public void testGetMessageById() {
        int id = 1;
        Message expectedMessage = new Message();
        when(messageService.getMessageById(id)).thenReturn(expectedMessage);
        Message actualMessage = databaseController.getMessageById(id);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void testGetAllMessages() {
        List<Message> expectedMessages = new ArrayList<>();
        when(messageService.getAllMessages()).thenReturn(expectedMessages);
        List<Message> actualMessages = databaseController.getAllMessages();
        assertThat(actualMessages).isEqualTo(expectedMessages);
    }
}
