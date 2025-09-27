package com.htwsaar.anzeigetafel.client;

import com.htwsaar.anzeigetafel.model.Message;
import com.htwsaar.anzeigetafel.server.Server;
import com.htwsaar.anzeigetafel.model.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ClientCoordinator extends User {
    private int clientID;
    private Server server;
    private Queue<Message> messageQueue;
    private List<Message> displayedMessages;;


    public ClientCoordinator(String username, String password) {
        super(username, password,  Role.ClientCoordinator);

        this.messageQueue = new LinkedList<>();
        this.displayedMessages = new ArrayList<>();
    }

    public int getCoordinatorID() {
        return clientID;
    }

    public void sendMessage(String content) {
        Message message = new Message(clientID, content);
        messageQueue.add(message);

    }

    public void modifyMessage(int messageID, String newContent) {
        // Client RMI
        //server.modifyMessage(coordinatorID, messageID, newContent);
    }

    public void deleteMessage(int messageID) {
        // Client RMI
        //server.popMessage(clientID, messageID);
    }

    @Override
    public void notifyUser() {

    }

    public void displayMessages() {
        // Client RMI
        //displayedMessages = server.getMessages(clientID);

        System.out.println("Angezeigte Nachrichten für Coordinator " + clientID + ":");
        for (Message message : displayedMessages) {
            System.out.println(message.getContent());
        }
    }

    public void receiveNotification() {
        System.out.println("Benachrichtigung erhalten: Neue Nachrichten für Coordinator " + clientID);
    }

    public void publishMessage(String content) {
        //Message message = new Message(clientID,"", content);
        //server.pushMessage(message);
    }

    @Override
    public String toString() {
        return "ClientCoordinator{" +
                "coordinatorID=" + clientID +
                ", server=" + server +
                ", messageQueue=" + messageQueue +
                ", displayedMessages=" + displayedMessages +
                '}';
    }
}
