package com.htwsaar.anzeigetafel.server;


import com.htwsaar.anzeigetafel.model.DisplayBoard;
import com.htwsaar.anzeigetafel.model.Message;
import com.htwsaar.anzeigetafel.model.User;
import com.htwsaar.anzeigetafel.server.registrarservice.IServiceRegistrar;
import com.htwsaar.anzeigetafel.server.registrarservice.ServiceRegistrar;
import com.htwsaar.anzeigetafel.server.util.ServerLogger;
import com.htwsaar.anzeigetafel.service.DisplayBoardService;
import com.htwsaar.anzeigetafel.service.MessageService;
import com.htwsaar.anzeigetafel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServerRMI implements IServerRMI {
    private int port = 42424; // default
    private Registry registry = null;
    private Registry registerService = null;
    private InetAddress localHost;



    private Server serverInstance;


    private DisplayBoardService displayBoardService;
    private UserService userService;
    private MessageService messageService;

    public ServerRMI(DbService dbService, Server serverInstance) {
        this.displayBoardService = dbService.getDisplayBoardService();
        this.userService = dbService.getUserService();
        this.messageService = dbService.getMessageService();
        this.serverInstance = serverInstance;
    }

    public ServerRMI(DisplayBoardService displayBoardService, UserService userService, MessageService messageService, Server serverInstance) {
        this.displayBoardService = displayBoardService;
        this.userService = userService;
        this.messageService = messageService;
        this.serverInstance = serverInstance;
    }

    @Autowired
    public ServerRMI(DisplayBoardService displayBoardService, UserService userService, MessageService messageService) {
        this.displayBoardService = displayBoardService;
        this.userService = userService;
        this.messageService = messageService;
    }

    /**
     * Start the server
     *
     * @param serverPort
     * @param registerServiceIp
     * @throws RemoteException
     * @throws NotBoundException
     * @throws UnknownHostException
     */
    public void startServer(int serverPort, String registerServiceIp) throws RemoteException, NotBoundException, UnknownHostException {
        this.port = serverPort;
        registry = LocateRegistry.createRegistry(serverPort);
        ServerLogger.LogInfo("Server started");

        registerService = LocateRegistry.getRegistry(registerServiceIp, ServiceRegistrar.PORT);
        if (registerService == null) {
            throw new RuntimeException("Could not find the register service with ip: " + registerServiceIp + ", on port: " + ServiceRegistrar.PORT);
        }

        // shutdown the server if we can not find the register
        try {
            this.registerService.lookup(ServiceRegistrar.SERVICE_NAME);
        } catch (Exception e) {
            ServerLogger.LogError(e.getMessage());
            System.exit(1);
        }

        this.localHost = InetAddress.getLocalHost();
    }

    /**
     * Host a display board
     *
     * @param displayBoard
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void hostDisplayboard(DisplayBoard displayBoard) throws RemoteException, NotBoundException {
        try {
            if (registry.lookup(displayBoard.getBoardName()) != null) {
                ServerLogger.LogWarn("The board is already hosted!");
                return;
            }
        } catch (RemoteException | NotBoundException e) {
            ServerLogger.LogInfo("Could not find a board with name (" + displayBoard.getBoardName() + ")");
        }

        ServerLogger.LogInfo("Try hosting/creating board with the name (" + displayBoard.getBoardName() + ")");
        ServerRMI obj = new ServerRMI(this.displayBoardService, this.userService, this.messageService, serverInstance);
        obj.setRegisterService(this.registerService);

        IServerRMI stub = (IServerRMI) UnicastRemoteObject.exportObject(obj, port);

        registry.rebind(displayBoard.getBoardName(), stub);

        // CREATE board in DB if not present
        if (displayBoardService.getBoard(displayBoard.getBoardName()) == null) {
            this.displayBoardService.crateBoard(displayBoard);
        }

        ServerLogger.LogInfo("Server ready, Display-board (" + displayBoard.getBoardName() + ") is hosted");

        // register service
        ServiceRegistrar.ServiceInfo info = new ServiceRegistrar.ServiceInfo();
        info.hostIP = localHost.getHostAddress();
        info.port = this.port;

        IServiceRegistrar stubRegistrar = (IServiceRegistrar) registerService.lookup(ServiceRegistrar.SERVICE_NAME);
        stubRegistrar.registerService(info, displayBoard.getBoardName());


    }

    public UserService getUserService() {
        return userService;
    }

    /**
     * Add a user to the server
     *
     * @param user
     * @return the ID of the user if the user was added successfully, -1 otherwise
     */
    @Override
    public Integer addUser(User user) {
        try {
            int id = this.userService.createUser(user);
            System.out.println("UserID: " + id);
            return id;
        } catch (Exception e) {
            ServerLogger.LogError(e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Login a user
     *
     * @param user
     * @return the ID of the user if the user was logged in successfully, null otherwise
     */
    @Override
    public Integer login(User user) throws RemoteException {
        User userByName = userService.findUserByName(user.getUsername());
        if (userByName == null || !userByName.getPassword().equals(user.getPassword()))
            return null;

        serverInstance.addNewConnectedUser(userByName.getUserID(), userByName.getUsername());

        return userByName.getUserID();
    }

    /**
     * Sign up a user
     *
     * @param user
     * @return the ID of the user if the user was signed up successfully, null otherwise
     */
    @Override
    public Integer singup(User user) throws RemoteException {
        try {
            User userByName = userService.findUserByName(user.getUsername());
            if (userByName != null) // means there is an existing user with same username
                return null;

            Integer userId = userService.createUser(user);
            serverInstance.addNewConnectedUser(userId, user.getUsername());
            return userId;
        } catch (Exception e) {
            ServerLogger.LogError(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Remove a user by its ID
     *
     * @param userID
     * @return true if the user was removed successfully, false otherwise
     */
    @Override
    @Async("asyncTaskExecutor")
    public Boolean removeUser(Integer userID) {
        try {
            userService.deleteuserById(userID);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Add a board to the server
     *
     * @param board
     * @return true if the board was added successfully, false otherwise
     */
    @Override
    @Async("asyncTaskExecutor")
    public Boolean addBoard(DisplayBoard board) {
        try {
            displayBoardService.crateBoard(board);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Remove a board by its ID
     *
     * @param boardID
     * @return true if the board was removed successfully, false otherwise
     */
    @Override
    @Async("asyncTaskExecutor")
    public Boolean removeBoard(Integer boardID) {
        try {
            displayBoardService.deleteBoard(boardID);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get a display board by its name
     *
     * @param boardName
     * @return the display board with the name
     * @throws RemoteException
     */
    @Override
    @Async("asyncTaskExecutor")
    public DisplayBoard getDisplayboard(String boardName) throws RemoteException {
        return displayBoardService.getBoard(boardName);
    }

    @Override
    @Async("asyncTaskExecutor")
    public User getUserByID(Integer userID) {
        try {
            return userService.findUserById(userID);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get a user by its username
     *
     * @param username
     * @return the user with the username
     */
    @Override
    @Async("asyncTaskExecutor")
    public User getUserByUsername(String username) {
        try {
            return userService.findUserByName(username);
        } catch (Exception e) {
            return null;
        }
    }


    public List<DisplayBoard> getAllBoards() {
        return displayBoardService.getAllBoards();
    }

    /**
     * Get all the users from the database
     *
     * @return a list of all the users
     */
    @Async("asyncTaskExecutor")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    @Override
    @Async("asyncTaskExecutor")
    public List<User> getUserByRole(User.Role role) {
        //@Note(Basel): This is not implemented in the database service

        //try {
        //    return userDatabase.get(role);
        //} catch (Exception e) {
        //    return null;
        //}

        return null;
    }


    /**
     * Modify a message by its ID and the user ID for who wants to modify it or if the user is a coordinator
     *
     * @param messageID
     * @param userid
     * @param newContent
     * @return true if the message was modified successfully, false otherwise
     */
    @Override
    @Async("asyncTaskExecutor")
    public Boolean modifyMessage(Integer messageID, Integer userid, String newContent) {
        Message message = null;
        try {
            message = messageService.getMessageById(messageID);
        } catch (Exception e) {
            ServerLogger.LogError("Could find message Id: " + messageID);
        }

        if (message == null) {
            ServerLogger.LogError("Could find message Id: " + messageID);
            return false;
        }

        //@Note(basel): check if coordinator first
        if (userService.findUserById(userid).getRole() != User.Role.ClientCoordinator && message.getSenderID() != userid)
            return false;

        message.setContent(newContent);
        messageService.updateMessage(messageID, message);
        serverInstance.registerNewUpdate(displayBoardService.getBoard(message.getDisplayBoardID()).getBoardName());

        return true;
    }

    /**
     * Delete a message by its ID and the user ID for who wants to delete it or if the user is a coordinator
     *
     * @param messageID
     * @param userid
     * @return true if the message was deleted successfully, false otherwise
     */
    @Override
    @Async("asyncTaskExecutor")
    public Boolean deleteMessage(Integer messageID, Integer userid) {
        try {

            Message message = messageService.getMessageById(messageID);
            //@Note(basel): check if coordinator first
            if (userService.findUserById(userid).getRole() != User.Role.ClientCoordinator && message.getSenderID() != userid) {
                return false;
            }

            messageService.deleteMessage(messageID);
            serverInstance.registerNewUpdate(displayBoardService.getBoard(message.getDisplayBoardID()).getBoardName());

            return true;
        } catch (Exception e) {
            ServerLogger.LogError(e.getMessage());
        }

        return true;
    }

    /**
     * Forward a message by its ID and the user ID only if the user is a coordinator you can forward the message
     *
     * @param messageID
     * @param userId
     * @return true if the message was forwarded successfully, false otherwise
     * @throws RemoteException
     */
    @Override
    @Async("asyncTaskExecutor")
    public Boolean forwardMessage(Integer messageID, Integer userId) throws RemoteException {
        User user = null;
        Message message = null;
        // check if the user is a coordinator
        try {
            user = userService.findUserById(userId);
            message = messageService.getMessageById(messageID);
        } catch (Exception e) {
            ServerLogger.LogError(e.getMessage());
            return false;
        }

        if (user == null || !user.getRole().equals(User.Role.ClientCoordinator)) {
            ServerLogger.LogError("User is not a ClientCoordinator or doesn't exist.");
            return false;
        }

        if (message == null) {
            ServerLogger.LogError("Message with ID " + messageID + " not found.");
            return false;
        }
        // set the message as public and update it
        message.setPublic(true);
        messageService.updateMessage(messageID, message);
        return true;
    }


    /**
     * Get all the messages that are marked as public (forwardet) from all the display boards from all servers
     *
     * @return a list of messages that are marked as public
     * @throws RemoteException
     */
    @Override
    public List<Message> getForwardMessages() throws RemoteException {
        IServiceRegistrar stubRegistrar = null;
        // get the register service
        try {
            stubRegistrar = (IServiceRegistrar) this.registerService.lookup(ServiceRegistrar.SERVICE_NAME);
        } catch (NotBoundException e) {
            ServerLogger.LogError(e.getMessage());
            return null;
        }
        // list of messages that are marked as public
        List<Message> forwardMessages = new ArrayList<>();

        Map<String, ServiceRegistrar.ServiceInfo> services = stubRegistrar.getAllServices();
        // get all the services and get the messages from them
        for (Map.Entry<String, ServiceRegistrar.ServiceInfo> entry : services.entrySet()) {
            ServiceRegistrar.ServiceInfo serviceInfo = entry.getValue();

            Registry serviceRegistry = LocateRegistry.getRegistry(serviceInfo.hostIP, serviceInfo.port);
            IServerRMI stub = null;
            // get the stub for the service
            try {
                stub = (IServerRMI) serviceRegistry.lookup(entry.getKey());
            } catch (NotBoundException e) {
                ServerLogger.LogError(e.getMessage());
            }

            if (stub == null)
                continue;

            // name of display-board is the name of the service
            List<Message> allMessages = stub.getAllMessages(stub.getDisplayboard(entry.getKey()));
            // Filter and add public messages to the forwardMessages list
            allMessages.stream()
                    .filter(Message::isMarkedPublic)
                    .forEach(forwardMessages::add);
        }

        return forwardMessages;
    }

    /**
     * Send a message to a display board by its ID and the user ID and the content of the message
     *
     * @param userID
     * @param content
     * @param displayBoard
     * @return if the message was sent successfully, false otherwise
     * @throws RemoteException
     */
    @Override
    @Async("asyncTaskExecutor")
    public Message sendMessage(Integer userID, String content, DisplayBoard displayBoard) {
        try {
            //Create a message
            Message message = new Message();
            message.setContent(content);
            message.setSenderID(userID);
            message.setUser(userService.findUserById(userID));
            message.setDisplayBoardID(displayBoard.getBoardID());
            message.setTimeStamp(LocalDateTime.now().toString());
            messageService.createMessage(message);

            serverInstance.registerNewUpdate(displayBoard.getBoardName());

        } catch (Exception e) {
            ServerLogger.LogError(e.getMessage());
        }

        return null;
    }


    /**
     * Get all the messages from a display board by its ID
     *
     * @param displayBoard
     * @return a list of messages from the display board
     * @throws RemoteException
     */
    @Override
    @Async("asyncTaskExecutor")
    public List<Message> getAllMessages(DisplayBoard displayBoard) throws RemoteException {
        if (displayBoard == null) {
            // Handle null display board gracefully
            return Collections.emptyList();
        }

        //return all messages from the display board by Board ID
        return messageService.getAllMessages().stream()
                .filter(message -> message.getDisplayBoardID() == displayBoard.getBoardID())
                .collect(Collectors.toList());
    }


    /**
     * Display a message on the display board
     *
     * @param message
     * @return true if the message was displayed successfully, false otherwise
     */
    @Override
    @Async("asyncTaskExecutor")
    public Boolean displayMessage(Message message) {
        try {
            //Find the display board for the message

            ServerLogger.LogInfo("Displaying message: " + message.getContent());

            return true; //Return true wenn the diplay war erfolgreich (ggwp)
        } catch (Exception e) {
            ServerLogger.LogError("Error displaying message: " + e.getMessage());
            return false;
        }
    }


    /**
     * Receive a notification on the server
     */
    @Override
    @Async("asyncTaskExecutor")
    public void receiveNotification() {
        try {
            ServerLogger.LogInfo("Received notification on the server.");
        } catch (Exception e) {
            ServerLogger.LogError("Error receiving notification: " + e.getMessage());
        }
    }

    /**
     * Set the register service
     *
     * @param registerService
     */
    public void setRegisterService(Registry registerService) {
        this.registerService = registerService;
    }



    public void setServerInstance(Server serverInstance) {
        this.serverInstance = serverInstance;
    }

    @Override
    @Async("asyncTaskExecutor")
    public Long getLastChangeTime(String boardName) throws RemoteException {
        return serverInstance.getLastChangeTime(boardName);
    }

    public void shutdown() {
        try {
            String[] list = registry.list();
            for (String obj : list) {
                registry.unbind(obj);
            }

            UnicastRemoteObject.unexportObject(registry, true);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public Registry getRegisterService() {
        return registerService;
    }

    public InetAddress getLocalHost() {
        return localHost;
    }

    public void setLocalHost(InetAddress localHost) {
        this.localHost = localHost;
    }

    public Server getServerInstance() {
        return serverInstance;
    }

    public DisplayBoardService getDisplayBoardService() {
        return displayBoardService;
    }

    public void setDisplayBoardService(DisplayBoardService displayBoardService) {
        this.displayBoardService = displayBoardService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
}
