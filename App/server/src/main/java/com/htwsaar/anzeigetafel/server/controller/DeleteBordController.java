package com.htwsaar.anzeigetafel.server.controller;

import com.htwsaar.anzeigetafel.server.ServerRMI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

public class DeleteBordController implements ControllerApi{

    private ServerRMI serverRMI;
    public TextField bordId;

    public void addBorde(ActionEvent event) {
        String id = bordId.getText();
        if(id.strip().isEmpty() ){
            InfoController.showMessage(InfoController.LogLevel.Error, "Bord Id", "Please fill the Bord ID");
            return;
        }

        try {

            serverRMI.removeBoard(Integer.parseInt(id));
            InfoController.showMessage(InfoController.LogLevel.Info, "Bord Added", "Bord Deleted Successfully");
        } catch (Exception e) {
            InfoController.showMessage(InfoController.LogLevel.Error, "Bord Added", e.getMessage());
        }

    }

    public static void makeFieldOnlyNumbers(TextField textField)
    {
        if (textField == null)
            return;

        textField.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                if (!newValue.matches("\\d*"))
                {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    @Override
    public void onStart() {
        makeFieldOnlyNumbers(bordId);
    }

    @Override
    public void onUpdate() {

    }
}
