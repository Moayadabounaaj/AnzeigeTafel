package com.htwsaar.anzeigetafel.server;

import com.htwsaar.anzeigetafel.model.DisplayBoard;
import com.htwsaar.anzeigetafel.model.Message;
import com.htwsaar.anzeigetafel.model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IServerRMI extends Remote {

    Integer addUser(User user) throws RemoteException;

    Integer login(User user) throws RemoteException;
    Integer singup(User user) throws RemoteException;

    Long getLastChangeTime(String boardName) throws RemoteException;

    Boolean removeUser(Integer userID) throws RemoteException;

    // return boardID
    Boolean addBoard(DisplayBoard board) throws RemoteException;
    Boolean removeBoard(Integer boardID) throws RemoteException;
    DisplayBoard getDisplayboard(String boardName) throws RemoteException;

    User getUserByID(Integer userID) throws RemoteException;

    User getUserByUsername(String username) throws RemoteException;

    List<User> getUserByRole(User.Role role) throws RemoteException;

    Boolean modifyMessage(Integer messageID, Integer userId,String newContent) throws RemoteException;

    Boolean deleteMessage(Integer messageID, Integer userId) throws RemoteException;

    Boolean forwardMessage(Integer messageID, Integer userId) throws RemoteException;
    List<Message> getForwardMessages() throws RemoteException;

    Boolean displayMessage(Message message) throws RemoteException;

    void receiveNotification() throws RemoteException;

    Message sendMessage(Integer userID, String content, DisplayBoard displayBoard) throws RemoteException;

    List<Message> getAllMessages(DisplayBoard displayBoard) throws RemoteException;
}
