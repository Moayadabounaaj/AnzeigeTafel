package com.htwsaar.anzeigetafel.server;


import com.htwsaar.anzeigetafel.model.User;

import java.util.ArrayList;
import java.util.List;

public class Admin {
    private int adminID;
    private String username;
    private String Password;
    private Server server;
    private List<User> connectedClients;
    private List<Server> connectedServers;

    public Admin(int adminID, String username, Server server, List<User> connectedClients, List<Server> connectedServers) {
        if (adminID < 0) {
            throw new ServerException("AdminID darf nicht negativ sein.");
        }
        this.adminID = adminID;

        if (username == null || username.trim().isEmpty()) {
            throw new ServerException("Der Benutzername darf nicht leer sein.");
        }
        this.username = username;

        if (server == null) {
            throw new ServerException("Die Benutzerdatenbank darf nicht null sein.");
        }

        this.server = server;

        this.connectedClients = new ArrayList<>();
        this.connectedServers = new ArrayList<>();
    }

    public void addUser(User user) {
        // User newUser = new User(username, password, role);
        // server.addUser(newUser);
    }

    public void removeUser(int userID) {
        server.removeUser(userID);
    }

    public void configure() {
        System.out.println("Systemkonfiguration wird durchgeführt...");
    }

    public void addBoard() {
        //Server newServer = new Server();
       // connectedServers.add(newServer);
    }

    public void removeBoard(Server server) {
        connectedServers.remove(server);
    }
}

