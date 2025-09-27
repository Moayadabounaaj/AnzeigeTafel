package com.htwsaar.anzeigetafel.client;


import com.htwsaar.anzeigetafel.model.DisplayBoard;
import com.htwsaar.anzeigetafel.model.Message;
import com.htwsaar.anzeigetafel.model.User;


import com.htwsaar.anzeigetafel.server.IServerRMI;
import com.htwsaar.anzeigetafel.server.registrarservice.IServiceRegistrar;
import com.htwsaar.anzeigetafel.server.registrarservice.ServiceRegistrar;
import com.htwsaar.anzeigetafel.server.util.ServerLogger;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ClientRMI {
    private IServiceRegistrar registrarStub;
    private IServerRMI stub;
    private ExecutorService threadPool;

    public ClientRMI() {
        this.threadPool = Executors.newFixedThreadPool(4);
    }


    /**
     * Connect to the server
     *
     * @param boardName the name of the board
     * @param hostIP    the ip of the server
     * @return the display board
     */
    public DisplayBoard connectToServer(String boardName, String hostIP) {

        Registry registry = null;
        try {
            // connect first to the ServiceRegistrar
            registry = LocateRegistry.getRegistry(hostIP, ServiceRegistrar.PORT);
            if (registry == null) {
                throw new RuntimeException("Could not find the register service with ip: " + hostIP + ", on port: " + ServiceRegistrar.PORT);
            }
            registrarStub = (IServiceRegistrar) registry.lookup(ServiceRegistrar.SERVICE_NAME);

            ServiceRegistrar.ServiceInfo serverInfo = registrarStub.searchService(boardName);
            if (serverInfo == null) {
                throw new RuntimeException("The board you want is not hosted!!, (" + boardName + ")");
            }

            // after we get the info now connect to the server that host the board
            registry = LocateRegistry.getRegistry(serverInfo.hostIP, serverInfo.port);
            stub = (IServerRMI) registry.lookup(boardName);

            //System.out.println("Verbindung mit Server erfolgt");

            return stub.getDisplayboard(boardName);
        } catch (RemoteException | NotBoundException e) {
            ServerLogger.LogError(e.getMessage());
        }

        return null;
    }

    /**
     * send a message to the server
     * @param userID the id of the user
     * @param content the content of the message
     * @param displayBoard the display board
     * @return the display board
     * @throws RemoteException
     */
    public void sendMessage(Integer userID, String content, DisplayBoard displayBoard) throws RemoteException {
        Runnable runnable = () ->
        {
            try {
                stub.sendMessage(userID, content, displayBoard);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        };

        threadPool.submit(runnable);
    }

    /**
     * Modify a message
     * @param messageID the id of the message
     * @param userId the id of the user
     * @param newContent the new content
     * @return true if the message was modified
     * @throws RemoteException
     */
    public Boolean modifyMessage(Integer messageID, Integer userId, String newContent) throws RemoteException {

        Runnable runnable = () ->
        {
            try {
                stub.modifyMessage(messageID, userId, newContent);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        };

        threadPool.submit(runnable);

        return true;
    }

    /**
     * Delete a message
     * @param messageID the id of the message
     * @param userId the id of the user
     * @return true if the message was deleted
     * @throws RemoteException
     */
    public Boolean deleteMessage(Integer messageID, Integer userId) throws RemoteException {
        return stub.deleteMessage(messageID, userId);
    }

    /**
     * Display a message
     * @param message the message to display
     * @return true if the message was displayed
     * @throws RemoteException
     */
    public Boolean displayMessage(Message message) throws RemoteException {
        Runnable runnable = () ->
        {
            try {
                stub.displayMessage(message);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        };

        threadPool.submit(runnable);
        return true;
    }

    /**
     * Receive a notification
     * @throws RemoteException
     */
    public void receiveNotification() throws RemoteException {
        Runnable runnable = () ->
        {
            try {
                stub.receiveNotification();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        };

        threadPool.submit(runnable);
    }

    public Integer addUser(User user) throws RemoteException {
        // no need for sync, add user only happens once at the login
        return stub.addUser(user);
    }

    public Integer login(User user) throws RemoteException {
        return stub.login(user);
    }

    public Integer signup(User user) throws RemoteException {
        return stub.singup(user);
    }

    /**
     * Get all messages
     * @param displayBoard the display board
     * @return the list of messages
     * @throws RemoteException
     */
    public List<Message> getAllMessages(DisplayBoard displayBoard) throws RemoteException {
        CompletableFuture<List<Message>> future = new CompletableFuture<>();
        Runnable runnable = () -> {
            try {
                List<Message> messages = stub.getAllMessages(displayBoard);
                future.complete(messages);
            } catch (RemoteException e) {
                future.completeExceptionally(e);
            }
        };

        threadPool.submit(runnable);

        try {
            return future.get(); // This will block until the result is available
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all messages
     * @param messageId the display board
     * @param userid the user id
     * @return the list of messages
     * @throws RemoteException
     */
    public Boolean forwardMessage(int messageId, int userid) throws RemoteException {
        Runnable runnable = () ->
        {
            try {
                stub.forwardMessage(messageId, userid);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        };

        threadPool.submit(runnable);

        return true;
    }

    public List<Message> getForwardMessages() throws RemoteException {
        return stub.getForwardMessages();
    }

    public User getUser(int userId) throws RemoteException{
        return stub.getUserByID(userId);
    }

    public Long getLastChangeTime(String boardName) throws RemoteException{
        return stub.getLastChangeTime(boardName);
    }

}
