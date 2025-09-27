package com.htwsaar.anzeigetafel.client.controller;

import com.htwsaar.anzeigetafel.model.DisplayBoard;
import com.htwsaar.anzeigetafel.model.Message;
import com.htwsaar.anzeigetafel.model.User;
import com.htwsaar.anzeigetafel.client.ClientRMI;
import com.htwsaar.anzeigetafel.server.controller.Layer;
import com.htwsaar.anzeigetafel.server.util.ServerLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ConsoleDialog implements Layer {

    private ClientRMI clientRMI;
    private Scanner input;
    private DisplayBoard displayBoard;
    private Integer userId;


    //-------------------Const fields--------------------------------//
    private static final int ADD_MESSAGE = 1;
    private static final int DELETE_MESSAGE = 2;
    private static final int MODIFY_MESSAGE = 3;
    private static final int DISPLAY_MESSAGE = 4;
    private static final int RECEIVE_NOTIFICATION = 5;
    private static final int VIEW_MESSAGES = 6;
    private static final int FORWARD_MESSAGES = 7;
    private static final int VIEW_FORWARD_MESSAGES = 8;
    private static final int END = 0;

    @Override
    public void onStart(String[] args) {
        input = new Scanner(System.in);

        try {
            simpleLogin();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private int readInteger() {
        System.out.print("--> ");
        int in = input.nextInt();
        input.nextLine();
        return in;
    }

    private float readFloat() {
        System.out.print("--> ");
        float in = input.nextFloat();
        input.nextLine();
        return in;
    }

    private String readString() {
        System.out.print("--> ");
        return input.nextLine();
    }

    private void printInputInformation() {
        System.out.println("Add Message: " + ADD_MESSAGE);
        System.out.println("Delete Message: " + DELETE_MESSAGE);
        System.out.println("Modify Message: " + MODIFY_MESSAGE);
        System.out.println("Display Message: " + DISPLAY_MESSAGE);
        System.out.println("Receive Notification: " + RECEIVE_NOTIFICATION);
        System.out.println("View Messages: " + VIEW_MESSAGES);
        System.out.println("Forward Messages: " + FORWARD_MESSAGES);
        System.out.println("View Forward Messages: " + VIEW_FORWARD_MESSAGES);
        System.out.println("Shutdown: " + END);
    }

    private void handleInput(int inputParam) {
        switch (inputParam) {
            case ADD_MESSAGE:
                System.out.println("Thread: " + Thread.currentThread().toString());
                addMessage();
                break;
            case DELETE_MESSAGE:
                deleteMessage();
                break;
            case MODIFY_MESSAGE:
                modifyMessage();
                break;
            case DISPLAY_MESSAGE:
                displayMessage();
                break;
            case RECEIVE_NOTIFICATION:
                receiveNotification();
                break;
            case VIEW_MESSAGES:
                viewMessages();
                break;
            case FORWARD_MESSAGES:
                forwardMessage();
                break;
            case VIEW_FORWARD_MESSAGES:
                viewForwardMessages();
                break;
            case END:
                onShutdown();
                break;
            default:
                System.out.println("Invalid choice. Please choose a valid option.");
        }
    }



    private static String getLocalIpAddress() throws Exception {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            // Exclude loopback and inactive interfaces
            if (iface.isLoopback() || !iface.isUp()) {
                continue;
            }

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address.isSiteLocalAddress()) {
                    return address.getHostAddress();
                }
            }
        }
        throw new RuntimeException("No suitable network interface found.");
    }


    @Override
    public void onRun() {
        int currentInput = -1;
        while (currentInput != END) {
            printInputInformation();
            currentInput = readInteger();
            handleInput(currentInput);
        }
    }

    @Override
    public void onShutdown() {
        System.out.println("Shutting down...");
        input.close();
    }

    // just very simple for now, improve this later
    public void simpleLogin() throws RemoteException {
        boolean connectToServer = false;
        boolean loggedIn = false;

        while (!connectToServer) {
            System.out.print("Enter display board name: ");
            String displayboardName = readString();

            System.out.print("Enter server IP: ");
            String hostIP = readString();

            this.clientRMI = new ClientRMI();
            this.displayBoard = clientRMI.connectToServer(displayboardName, hostIP);

            if (displayBoard == null)
                System.out.println("Failed to connect to display board. Check display board name and server IP.");
            else
                connectToServer = true;
        }

        while (!loggedIn) {
            System.out.print("Login: 1\nSignup: 2\nEnter your choice: ");
            int loginOrSignUp = readInteger();

            switch (loginOrSignUp) {
                case 1:
                    if (loginUser()) {
                        System.out.println("Login successful.");
                        loggedIn = true;
                    } else {
                        System.out.println("Login failed. Please check your credentials and try again.");
                    }
                    break;
                case 2:
                    if (signupUser()) {
                        System.out.println("Signup successful.");
                        loggedIn = true;
                    } else {
                        System.out.println("Signup failed. Please try another username.");
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 for login or 2 for signup.");
                    break;
            }
        }
    }

    private boolean loginUser() throws RemoteException {
        System.out.print("Enter your username: ");
        String username = readString();

        System.out.print("Enter your password: ");
        String password = readString();

        User user = new User(username, password, User.Role.Client);
        user.setDisplayBoardId(displayBoard.getBoardID());
        this.userId = clientRMI.login(user);

        return userId != null;
    }

    private boolean signupUser() throws RemoteException {
        System.out.print("Enter your username: ");
        String username = readString();

        System.out.print("Enter your password: ");
        String password = readString();

        User user = new User(username, password, User.Role.Client);
        user.setDisplayBoardId(displayBoard.getBoardID());
        this.userId = clientRMI.signup(user);

        return userId != null;
    }

    private void forwardMessage() {
        System.out.print("Message id: ");
        int messageID = readInteger();

        try {
            clientRMI.forwardMessage(messageID, userId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void viewMessages() {
        if (displayBoard == null) {
            ServerLogger.LogError("could not find displayboard");
            return;
        }

        try {
            List<Message> messages = clientRMI.getAllMessages(displayBoard);
            if (messages == null) {
                System.out.println("Could not find any Message");
                return;
            }

            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Displayboard Name: " + displayBoard.getBoardName());
            messages.forEach(System.out::println);
            System.out.println("--------------------------------------------------------------------------------");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void viewForwardMessages() {
        try {


            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Public Messages: " + displayBoard.getBoardName());

            List<Message> messages = clientRMI.getForwardMessages();
            if (messages == null) {
                System.out.println("Could not find any Message");
                return;
            }

            messages.forEach(System.out::println);
            System.out.println("--------------------------------------------------------------------------------");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void addMessage() {
        System.out.println("Enter your message:");
        String message = readString();
        System.out.println("You entered: " + message);

        try {
            clientRMI.sendMessage(userId, message, displayBoard);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMessage() {
        System.out.println("Enter the message ID to delete:");
        int messageToDelete = readInteger();
        System.out.println("Deleted message: " + messageToDelete);

        try {
            boolean state = clientRMI.deleteMessage(messageToDelete, userId);
            if (!state)
                System.out.println("Could not delete the message");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    public void modifyMessage() {
        System.out.println("Enter the message ID to modify:");
        int messageToModify = readInteger();
        System.out.println("Enter the new content for the message:");
        String newContent = readString();

        try {
            boolean state = clientRMI.modifyMessage(messageToModify, userId, newContent);
            if (state)
                System.out.println("Message modified");
            else
                System.out.println("Could not modify the message");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void displayMessage() {
        try {

            boolean isDisplayed = clientRMI.displayMessage(null);

            if (isDisplayed) {
                System.out.println("Message displayed successfully.");
            } else {
                System.out.println("Failed to display the message.");
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void receiveNotification() {
        try {
            clientRMI.receiveNotification();
            System.out.println("Notification received successfully.");
        } catch (RemoteException e) {

            handleRemoteException(e);
        }
    }

    private void handleRemoteException(RemoteException e) {
        throw new RuntimeException("Error receiving notification", e);
    }

}


