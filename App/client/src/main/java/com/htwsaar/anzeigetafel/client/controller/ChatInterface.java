package com.htwsaar.anzeigetafel.client.controller;

import com.htwsaar.anzeigetafel.client.ClientRMI;
import com.htwsaar.anzeigetafel.model.DisplayBoard;
import com.htwsaar.anzeigetafel.model.Message;
import com.htwsaar.anzeigetafel.model.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.function.Consumer;

public class ChatInterface implements ControllerApi { // Controller for the chat interface
    public ListView<MessagePanel> chatPane;
    public Label usernameLabel;
    public TextArea messageBox;
    public Button sendbtn;
    public ComboBox statusComboBox;
    private ClientRMI clientRMI;

    private DisplayBoard displayBoard;
    private int userId;
    private String username;
    private User user;

    private Long lastChangeTime;

    private ContextMenu contextMenu;

    /**
     * This method is called when the controller is started.
     */
    @Override
    public void onStart() {
        List<Message> messageList = null;
        try {
            this.user = clientRMI.getUser(userId);
            messageList = clientRMI.getAllMessages(displayBoard);
            if (messageList != null)
                messageList.forEach(this::viewMessage);
        } catch (RemoteException e) {
            InfoController.showMessage(InfoController.LogLevel.Error, "Messages", e.getMessage());
        }

        chatPane.setCellFactory(new Callback<ListView<MessagePanel>, ListCell<MessagePanel>>() {
            @Override
            public ListCell<MessagePanel> call(ListView<MessagePanel> listView) {
                return new ListCell<MessagePanel>() {
                    /**
                     * This method is called when the cell is updated.
                     * @param item The item to be updated.
                     * @param empty A boolean value that indicates if the cell is empty.
                     */
                    @Override
                    protected void updateItem(MessagePanel item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setGraphic(item.getContent());
                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });

        usernameLabel.setText(username);

        sendbtn.setOnAction(actionEvent -> {
            sendMethod();
        });

        try {
            lastChangeTime = clientRMI.getLastChangeTime(displayBoard.getBoardName());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        Timeline chatWatcher = new Timeline(
                new KeyFrame(Duration.millis(500),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                Long newLastChangeTime = null;

                                try {
                                    newLastChangeTime = clientRMI.getLastChangeTime(displayBoard.getBoardName());
                                } catch (RemoteException e) {
                                    throw new RuntimeException(e);
                                }

                                if (newLastChangeTime == null)
                                    return;

                                if (!newLastChangeTime.equals(lastChangeTime)) {
                                    lastChangeTime = newLastChangeTime;
                                    reloadMessages();
                                }
                            }
                        }));
        chatWatcher.setCycleCount(Timeline.INDEFINITE);
        chatWatcher.play();

        listviewListener();
        comboBoxListener();
    }
    /**
     * This method is called when the controller is updated.
     */

    @Override
    public void onUpdate() {

    }

    private void listviewListener() {
        contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete Message");
        MenuItem modifyMenuItem = new MenuItem("Modify Message");
        MenuItem forwardMenuItem = new MenuItem("Forward Message");

        deleteMenuItem.setOnAction(event -> {
            MessagePanel selectedItem = chatPane.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                try {
                    boolean state = clientRMI.deleteMessage(selectedItem.getMessageId(), userId);
                    if (!state)
                        InfoController.showMessage(InfoController.LogLevel.Error, "Error", "Can not delete the message");
                } catch (RemoteException e) {
                    InfoController.showMessage(InfoController.LogLevel.Error, "Error", e.getMessage());
                }
            }
        });

        modifyMenuItem.setOnAction(event -> {
            MessagePanel selectedItem = chatPane.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editMessageWindow.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Stage stage = new Stage();

                stage.setTitle("edit Message");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL); //default, for closing th pop up window
                stage.resizableProperty().setValue(false);
                stage.show();

                if (fxmlLoader.getController() instanceof EditMessageController)
                {
                    EditMessageController editMessageController = ((EditMessageController) fxmlLoader.getController());
                    editMessageController.getContent(selectedItem.messageContent.getText());
                    editMessageController.setMessageID(selectedItem.getMessageId());
                    editMessageController.setClientRMI(clientRMI);
                    editMessageController.setUserID(userId);
                }
            }
        });

        forwardMenuItem.setOnAction(event -> {
            MessagePanel selectedItem = chatPane.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                try {
                    clientRMI.forwardMessage(selectedItem.getMessageId(), userId);
                } catch (RemoteException e) {
                    InfoController.showMessage(InfoController.LogLevel.Error, "Error", e.getMessage());
                }
            }
        });

        if (user.getRole() == User.Role.ClientCoordinator) {
            contextMenu.getItems().addAll(deleteMenuItem, modifyMenuItem, forwardMenuItem);
        } else {
            contextMenu.getItems().addAll(deleteMenuItem, modifyMenuItem);
        }

        // Set the context menu to appear on right-click
        chatPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(chatPane, event.getScreenX(), event.getScreenY());
            }
        });
    }

    private void comboBoxListener(){
        statusComboBox.valueProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                List<Message> messageList = null;
                chatPane.getItems().clear();

                if (newValue.equals("Forward")){

                    try {
                        messageList = clientRMI.getForwardMessages();
                        if (messageList != null)
                            messageList.forEach(new Consumer<Message>() {
                                @Override
                                public void accept(Message message) {
                                    viewMessage(message);
                                }
                            });

                    } catch (RemoteException e) {
                        InfoController.showMessage(InfoController.LogLevel.Error, "Messages", e.getMessage());
                    }
                }else{
                    try {
                        messageList = clientRMI.getAllMessages(displayBoard);
                        if (messageList != null)
                            messageList.forEach(new Consumer<Message>() {
                                @Override
                                public void accept(Message message) {
                                    viewMessage(message);
                                }
                            });

                    } catch (RemoteException e) {
                        InfoController.showMessage(InfoController.LogLevel.Error, "Messages", e.getMessage());
                    }
                }
            }
        });
    }

    private void viewMessage(Message message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("message.fxml"));
            AnchorPane root = loader.load();

            MessagePanel messagePanel = loader.getController();
            if (messagePanel == null)
                return;

            // better will be if the message has username in it
            messagePanel.username.setText(clientRMI.getUser(message.getSenderID()).getUsername());
            messagePanel.messageContent.setText(message.getContent());
            messagePanel.setMessageId(message.getMessageID());
            messagePanel.timestamp.setText(String.valueOf(message.getTimeStamp()));
            chatPane.getItems().add(messagePanel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void reloadMessages() {
        System.out.println("Reload Messages");
        chatPane.getItems().clear();

        List<Message> messageList = null;
        try {
            messageList = clientRMI.getAllMessages(displayBoard);
            if (messageList != null)
                messageList.forEach(this::viewMessage);
        } catch (RemoteException e) {
            InfoController.showMessage(InfoController.LogLevel.Error, "Messages", e.getMessage());
        }
    }

    public void sendMethod() {
        try {
            clientRMI.sendMessage(userId, messageBox.getText(), displayBoard);
            messageBox.clear();
        } catch (RemoteException e) {
            InfoController.showMessage(InfoController.LogLevel.Error, "Error", e.getMessage());
        }
    }

    public void closeApplication(MouseEvent mouseEvent) {
    }

    public void setClientRMI(ClientRMI clientRMI) {
        this.clientRMI = clientRMI;
    }

    public void setDisplayBoard(DisplayBoard displayBoard) {
        this.displayBoard = displayBoard;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
