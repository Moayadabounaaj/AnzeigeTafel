package com.htwsaar.anzeigetafel.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class InfoController {

    public Button yesBtn;
    public Button noBtn;
    public Button okBtn;
    public AnchorPane root;

    public Text displayText;

    private boolean answer;

    public enum LogLevel
    {
        Info, Warn, Error
    }

    public static void showMessage(LogLevel alertType, String title, String content)
    {
        try
        {
            InfoController infoController = new InfoController();
            FXMLLoader fxmlLoader = new FXMLLoader(infoController.getClass().getResource("info.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            //give information about what happened after adding a guest if it went well or wrong
            infoController = fxmlLoader.getController();
            infoController.setText(content);
            infoController.okBtn.setVisible(true);
            infoController.yesBtn.setVisible(false);
            infoController.noBtn.setVisible(false);
            stage.show();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean showConfirmMessage(Alert.AlertType type, String title, String content)
    {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);
        Alert alert = new Alert(Alert.AlertType.WARNING, content, yes, no);

        alert.setTitle(title);
        Optional<ButtonType> result = alert.showAndWait();

        return result.equals(yes);
    }

    @FXML
    void setText(String msg)
    {
        displayText.setText(msg);
    }

    @FXML
    void cancel(ActionEvent event)
    {
        ((Stage) displayText.getScene().getWindow()).close();
    }

    public void onYesBtnClicked(ActionEvent event)
    {
        answer = true;
        cancel(event);
    }

    public void onNoBtnClicked(ActionEvent event)
    {
        answer = false;
        cancel(event);
    }
}
