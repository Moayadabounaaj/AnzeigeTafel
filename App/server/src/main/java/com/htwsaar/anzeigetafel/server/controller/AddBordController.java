package com.htwsaar.anzeigetafel.server.controller;

import com.htwsaar.anzeigetafel.model.DisplayBoard;
import com.htwsaar.anzeigetafel.server.ServerRMI;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.beans.EventHandler;

public class AddBordController implements ControllerApi{

    private ServerRMI serverRMI;
    public TextField bordName;

    public void addBorde(ActionEvent event) {
        String name = bordName.getText();
        if(name.strip().isEmpty() ){
            InfoController.showMessage(InfoController.LogLevel.Error, "Bord Name", "Please fill the Bord Name");
            return;
        }

        try {
            DisplayBoard displayBoard = new DisplayBoard(name);
            serverRMI.addBoard(displayBoard);
            InfoController.showMessage(InfoController.LogLevel.Info, "Bord Added", "Bord Added Successfully");
        } catch (Exception e) {
            InfoController.showMessage(InfoController.LogLevel.Error, "Bord Added", e.getMessage());
        }

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onUpdate() {

    }
}
