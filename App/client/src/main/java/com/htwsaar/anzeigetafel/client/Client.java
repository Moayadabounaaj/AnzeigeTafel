package com.htwsaar.anzeigetafel.client;

import com.htwsaar.anzeigetafel.model.Message;
import com.htwsaar.anzeigetafel.server.Server;
import com.htwsaar.anzeigetafel.model.User;

import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

public class Client extends User {
    private int clientID;
    private Server server;
    private Queue<Message> messageQueue;
    private List<Message> displayedMessages;

    public Client(String username, String password) {
        super(username, password, Role.Client);
        this.messageQueue = new LinkedList<Message>();

        if (clientID < 0) {
            throw new ClientExceptions("ClientID darf nicht negativ sein.");
        }
        this.clientID = clientID;

        this.messageQueue = new LinkedList<Message>();
    }

    public int getClientID() {
        return clientID;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }


    @Override
    public void sendMessage(String content) {
        //client RMI
        Message message = new Message(clientID, content);
        messageQueue.add(message);
        //server.pushMessage(message);
    }

    @Override
    public void modifyMessage(int messageID, String newContent) {
        //client RMI
        //server.modifyMessage(clientID, messageID, newContent);
    }

    @Override
    public void deleteMessage(int messageID) {
        //client RMI
        //server.popMessage(clientID, messageID);
    }

    @Override
    public void notifyUser() {

    }

    @Override
    public void displayMessages() {
        // Client RMI
        //displayedMessages = server.getMessages(clientID);
        System.out.println("Angezeigte Nachrichten für Client " + clientID + ":");
        for (Message message : displayedMessages) {
            System.out.println(message.getContent());
        }
    }


}
