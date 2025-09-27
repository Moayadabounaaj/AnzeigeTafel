package com.htwsaar.anzeigetafel.server;

import com.htwsaar.anzeigetafel.model.DisplayBoard;
import com.htwsaar.anzeigetafel.model.Message;
import com.htwsaar.anzeigetafel.model.User;
import com.htwsaar.anzeigetafel.server.controller.ConsoleDialog;
import com.htwsaar.anzeigetafel.server.controller.Layer;
import com.htwsaar.anzeigetafel.server.util.ServerLogger;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Server {
    int serverID;
    Map<String, Integer> connectedClients; // username to userid
    Map<String, Long> lastChangeTime;

    private ServerRMI serverRMI;
    private DbService dbService;

    public Server(DbService dbService, ServerRMI serverRMI) {
        this.dbService = dbService;

        serverRMI.setServerInstance(this);
        this.serverRMI = serverRMI;

        connectedClients = new HashMap<>();
        lastChangeTime = new HashMap<>();
    }

    /**
     * startup console dialog or gui here
     *
     * @param args
     */
    public void run(String[] args) {
        Layer consoleDialog = new ConsoleDialog(this);

        consoleDialog.onStart(args);
        consoleDialog.onRun();
        consoleDialog.onShutdown();
    }

    public void shutdown() {
        serverRMI.shutdown();
    }

    public void addUser(User user) {
        dbService.getUserService().createUser(user);
    }

    public void removeUser(int userID) {
        dbService.getUserService().deleteuserById(userID);
    }

    public void configureBoard(int boardID) {
    }

    // @Note(basel): return Status object
    public void monitorStatus() {
    }

    public void NotifyUser(int userID) {
    }

    /**
     * Start the server
     *
     * @param serverPort
     * @param registerService
     */
    public void startServer(int serverPort, String registerService) {
        try {
            serverRMI.startServer(serverPort, registerService);
        } catch (RemoteException | NotBoundException | UnknownHostException e) {
            ServerLogger.LogCritical(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stop hosting the displayboard
     *
     * @param displayBoard the displayboard to stop
     */
    public void hostDisplayboard(DisplayBoard displayBoard) {
        try {
            serverRMI.hostDisplayboard(displayBoard);
        } catch (RemoteException | NotBoundException e) {
            ServerLogger.LogCritical(e.getMessage());
        }
    }

    /**
     * set a user as a coordinator
     *
     * @param userid           the user id
     * @param displayBoardName the displayboard name
     */
    public void setCoordinator(int userid, String displayBoardName) {
        try {
            DisplayBoard board = serverRMI.getDisplayboard(displayBoardName);
            User user = serverRMI.getUserByID(userid);

            if (board == null) {
                ServerLogger.LogError("Could not find the board: " + displayBoardName);
                return;
            }

            if (user == null) {
                ServerLogger.LogError("Could not find the user: " + userid);
                return;
            }

            if (board.getBoardID() != user.getDisplayBoardId()) {
                ServerLogger.LogError("the user is not in Display-board!");
                return;
            }

            user.setRole(User.Role.ClientCoordinator);
            serverRMI.getUserService().updateUser(user);

        } catch (RemoteException | RuntimeException e) {
            ServerLogger.LogCritical(e.getMessage());
        }
    }

    /**
     * get all messages from a board
     *
     * @param boardName the board name
     * @return list of messages
     */
    public List<Message> getAllMessages(String boardName) {
        DisplayBoard board = null;
        try {
            board = serverRMI.getDisplayboard(boardName);
            return serverRMI.getAllMessages(board);
        } catch (RemoteException e) {
            return null;
        }
    }

    /**
     * get all forward messages
     *
     * @return list of forward messages
     */
    public List<Message> getForwardMessages() {
        try {
            return serverRMI.getForwardMessages();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get all users
     *
     * @param boardName the board name
     * @return list of users
     */
    public List<User> getAllUsers(String boardName) {
        return serverRMI.getAllUsers();
    }

    public void addNewConnectedUser(int userid, String username) {
        connectedClients.put(username, userid);
    }

    public int getHowManyUserConnected() {
        return connectedClients.size();
    }

    public void registerNewUpdate(String boardName){
        lastChangeTime.put(boardName, new Date().getTime());
    }

    public Long getLastChangeTime(String boardName){
        return lastChangeTime.get(boardName);
    }

}
