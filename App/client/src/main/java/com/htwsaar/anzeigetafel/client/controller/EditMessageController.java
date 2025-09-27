package com.htwsaar.anzeigetafel.client.controller;

import com.htwsaar.anzeigetafel.client.ClientRMI;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class EditMessageController {

    public TextField messageField;
    public Button savebtn;
    public Button cancelbtn;

    private ClientRMI clientRMI;
    private int messageID;
    private int userID;


    public void getContent(String msgContent) {
        messageField.setText(msgContent);
    }

    public void setClientRMI(ClientRMI clientRMI) {
        this.clientRMI = clientRMI;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }


    public void saveClick(ActionEvent actionEvent) {
        try {
            clientRMI.modifyMessage(messageID, userID,messageField.getText());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        Stage stage = (Stage) savebtn.getScene().getWindow();
        stage.close();
    }

    public void cancelClick(ActionEvent actionEvent) {
        Stage stage = (Stage) savebtn.getScene().getWindow();
        stage.close();
    }
}
