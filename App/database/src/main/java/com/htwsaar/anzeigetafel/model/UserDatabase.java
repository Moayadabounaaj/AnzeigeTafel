package com.htwsaar.anzeigetafel.model;

import com.htwsaar.anzeigetafel.model.DisplayBoard;
import com.htwsaar.anzeigetafel.model.Message;
import com.htwsaar.anzeigetafel.model.User;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserDatabase {

    // just for testing prototype
    private HashMap<Integer, User> testMapUsers;
    private HashMap<Integer, DisplayBoard> testMapDisplayBoard;
    private HashMap<Integer, Message> testMessage;
    private int userGeneretedIDs;
    private int displayBoardGeneretedIDs;
    private int messageGeneretedIDs;

    public UserDatabase() {
        // delete later
        testMapUsers = new HashMap<>();
        testMapDisplayBoard = new HashMap<>();
        testMessage = new HashMap<>();

        userGeneretedIDs = 0;
        displayBoardGeneretedIDs = 0;
    }

    /**
     * Add user to the database
     * @param user
     * @return userID and increment the userID
     */
    public int addUser(User user) {
        testMapUsers.put(userGeneretedIDs, user);
        user.setUserID(userGeneretedIDs);

        return userGeneretedIDs++;
    }

    /**
     * remove user from the database
     * @param userId
     */
    public void removeUser(int userId) {
        testMapUsers.remove(userId);
    }

    /**
     * add display board to the database
     * @param board
     */
    public void addBoard(DisplayBoard board) {
        board.setBoardID(displayBoardGeneretedIDs);
        testMapDisplayBoard.put(displayBoardGeneretedIDs++, board);
    }


    public DisplayBoard getDisplayBoardByName(String boardName)
    {
        int id = getDisplayBoardId(boardName);
        return testMapDisplayBoard.get(id); // will return null if not present
    }

    /**
     * get all display boards by name
     * @return list of display boards
     */
    public int getDisplayBoardId(String boardName)
    {
        Iterator<DisplayBoard> iterator = testMapDisplayBoard.values().iterator();

        while (iterator.hasNext()) {
            DisplayBoard board = iterator.next();
            if (board.getBoardName().equals(boardName))
                return board.getBoardID();
        }

        return -1;
    }


    /**
     * remove display board from the database
     * @param boardID
     */
    public void removeBoard(int boardID) {
        testMapDisplayBoard.remove(boardID);
    }

    /**
     * get user by id
     * @return list of display boards
     */
    public User getUserByID(int userId) {
        return testMapUsers.get(userId);
    }

    /**
     * get user by username
     * @return user
     */
    public User getUserByUsername(String userName) {
        Iterator<User> iterator = testMapUsers.values().iterator();

        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getUsername().equals(userName))
                return user;
        }

        return null;
    }

    /**
     * add message to the database
     * @param userid
     * @param msg
     * @param board
     * @return message
     */
    public Message addMessage(int userid, String msg, DisplayBoard board) {
        // should assert msg and board
        //Message message = new Message(userid, getUserByID(userid).getUsername(),msg);
        Message message = new Message(userid, msg);
        message.setMessageID(messageGeneretedIDs);
        message.setDisplayBoardID(board.getBoardID());

        testMessage.put(messageGeneretedIDs++, message);
        return message;
    }

    /**
     * get message by id
     * @return message
     */
    public Message getMessage(int messageID)
    {
        return testMessage.get(messageID);
    }

    /**
     * get all messages
     * @return list of messages
     */
    public List<Message> getAllMessages(int displayBoardId)
    {
        return testMessage.values()
                .parallelStream()
                .filter(message ->  message.getDisplayBoardID() == displayBoardId)
                .collect(Collectors.toList());
    }

    /**
     * update message
     * @param message
     * @return if message is updated
     */
    public boolean updateMessage(Message message)
    {
        testMessage.put(message.getMessageID(), message);
        return true;
    }

    /**
     * delete message
     * @param messageID
     * @return if message is deleted
     */
    public boolean deleteMessage(int messageID)
    {
        Message message = testMessage.remove(messageID);
        return message != null;
    }

    /**
     * get all users by role
     * @return list of users
     */
    public List<User> getUserByRole(User.Role role) {
        Predicate<User> filter = user -> user.getRole() == role;

        return testMapUsers.values()
                .parallelStream()
                .filter(filter)
                .collect(Collectors.toList());

    }




}
