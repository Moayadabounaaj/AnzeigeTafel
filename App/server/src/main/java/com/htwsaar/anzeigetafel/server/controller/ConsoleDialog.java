package com.htwsaar.anzeigetafel.server.controller;

import com.htwsaar.anzeigetafel.model.DisplayBoard;
import com.htwsaar.anzeigetafel.model.Message;
import com.htwsaar.anzeigetafel.model.User;
import com.htwsaar.anzeigetafel.server.Server;
import com.htwsaar.anzeigetafel.server.util.ServerLogger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import java.util.Scanner;


public class ConsoleDialog implements Layer {

    //-------------------Const fields--------------------------------//
    private static final int ADD_BOARD = 1;
    private static final int DELETE_BOARD = 2;
    private static final int VIEW_CONNECTED_USERS = 3;
    private static final int VIEW_MESSAGES = 4;
    private static final int VIEW_USERS = 5;
    private static final int SET_COORDINATOR = 6;
    private static final int VIEW_FORWARD_MESSAGES = 7;
    private static final int END = 0;

    Scanner input;
    private Server server;


    public ConsoleDialog(Server server) {
        input = new Scanner(System.in);
        this.server = server;
    }

    @Override
    public void onStart(String[] args) {
        System.out.print("Enter Port for server (example: 42424): ");
        int port = readInteger();
        System.out.print("\nIp of register service: ");
        String ip = readString();
        server.startServer(port, ip);
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
        ServerLogger.LogInfo("Shutdown the server");
    }

    private void printInputInformation() {
        System.out.println("Add Board: " + ADD_BOARD);
        System.out.println("Delete Board: " + DELETE_BOARD);
        System.out.println("View connected users: " + VIEW_CONNECTED_USERS);
        System.out.println("View messages: " + VIEW_MESSAGES);
        System.out.println("View Users: " + VIEW_USERS);
        System.out.println("Set Coordinator: " + SET_COORDINATOR);
        System.out.println("View Forward messages: " + VIEW_FORWARD_MESSAGES);
        System.out.println("Shutdown: " + END);
    }

    private void handleInput(int inputParam) {
        switch (inputParam) {
            case ADD_BOARD:               addDisplayBoard();           break;
            case DELETE_BOARD:            deleteDisplayboard();        break;
            case VIEW_CONNECTED_USERS:    viewConnectedClients();      break;
            case VIEW_MESSAGES:           viewMessages();              break;
            case VIEW_USERS:              viewAllUsers();              break;
            case SET_COORDINATOR:         setCoordinator();            break;
            case VIEW_FORWARD_MESSAGES:   viewForwardMessages();       break;
            case END:                     return;

            default:
                System.out.println("Invalid choice. Please choose a valid option.");
        }
    }

    public void viewConnectedClients() {
       System.out.println("Connected Users: " + server.getHowManyUserConnected());
    }

    public void addDisplayBoard() {
        System.out.println("Enter displayboard name: ");
        String boardName = input.nextLine();

        DisplayBoard displayBoard = new DisplayBoard(boardName);

        server.hostDisplayboard(displayBoard);
    }

    public void viewMessages()
    {
        System.out.print("Displayboard Name: ");
        String boardName = readString();

        List<Message> message = server.getAllMessages(boardName);
        if (message == null)
        {
            ServerLogger.LogError("No messages");
            return;
        }

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Displayboard: "+ boardName);
        message.forEach(System.out::println);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    public void viewForwardMessages()
    {
        List<Message> message = server.getForwardMessages();
        if (message == null)
        {
            ServerLogger.LogError("No messages");
            return;
        }

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Public: ");
        message.forEach(System.out::println);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    public void viewAllUsers()
    {
        List<User> users = server.getAllUsers("");

        System.out.println("-----------------------------------------------------------------------------------------");
        users.forEach(System.out::println);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    public void setCoordinator() {
        System.out.print("Enter user id: ");
        int userid = readInteger();

        System.out.print("Displayboard name: ");
        String displayBoardName = readString();

        server.setCoordinator(userid, displayBoardName);
    }

    public void deleteDisplayboard()
    {
        System.out.println("Delete Displayboard");
    }

    private int readInteger()
    {
        System.out.print("--> ");
        int in = input.nextInt();
        input.nextLine();
        return in;
    }

    private float readFloat()
    {
        System.out.print("--> ");
        float in = input.nextFloat();
        input.nextLine();
        return in;
    }

    private String readString(){
        System.out.print("--> ");
        return input.nextLine();
    }


}
