package com.htwsaar.anzeigetafel.client.controller;


import com.htwsaar.anzeigetafel.client.ClientRMI;
import com.htwsaar.anzeigetafel.model.DisplayBoard;
import com.htwsaar.anzeigetafel.model.User;
import com.htwsaar.anzeigetafel.server.controller.Layer;
import jakarta.validation.constraints.Pattern;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;


public class LoginController extends Application implements Layer {

    public Button loginBtnId;
    public Button singupBtnId;
    public TextField usernameTextfield;
    public TextField passwordTextfield;
    public TextField ipfield;
    public TextField tafelfield;
    private DisplayBoard displayBoard;
    private ClientRMI clientRMI;
    private Integer userId;

    public static String IP_REGEX = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";


    @FXML
    void closeWindow() {
        Stage stage = (Stage) loginBtnId.getScene().getWindow();
        stage.close();
    }

    @FXML
    void login(ActionEvent event) throws Exception {
        String username = usernameTextfield.getText();
        String password = passwordTextfield.getText();
        String tafelName = tafelfield.getText();
        String hostaddress = ipfield.getText();

        if (username.isEmpty() || password.isEmpty() || tafelName.isEmpty() || hostaddress.isEmpty()) {
            InfoController.showMessage(InfoController.LogLevel.Error, "Error", "Please fill all fields");
            return;
        }

        if (!connectToServer(tafelName, hostaddress))
            return;

        User user = new User(username, password, User.Role.Client);
        user.setDisplayBoardId(displayBoard.getBoardID());
        this.userId = clientRMI.login(user);
        if (userId == null)
        {
            InfoController.showMessage(InfoController.LogLevel.Error, "Error"
                    , "Could not login, check you username or password");
            return;
        }
        closeWindow();

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("tafel.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Tafel");
        stage.setScene(scene);
        stage.show();

        if (fxmlLoader.getController() instanceof ChatInterface) {
            ChatInterface currentController = fxmlLoader.getController();
            currentController.setClientRMI(clientRMI);
            currentController.setDisplayBoard(displayBoard);
            currentController.setUserId(userId);
            currentController.setUsername(user.getUsername());
            currentController.onStart();
        }
    }

    @FXML
    void signup(ActionEvent event) throws Exception {
        String username = usernameTextfield.getText();
        String password = passwordTextfield.getText();
        String tafelName = tafelfield.getText();
        String hostaddress = ipfield.getText();

        if (username.isEmpty() || password.isEmpty() || tafelName.isEmpty() || hostaddress.isEmpty()) {
            InfoController.showMessage(InfoController.LogLevel.Error, "Error", "Please fill all fields");
            return;
        }

        if (!connectToServer(tafelName, hostaddress))
            return;

        User user = new User(username, password, User.Role.Client);
        user.setDisplayBoardId(displayBoard.getBoardID());
        this.userId = clientRMI.signup(user);
        if (userId == null)
        {
            InfoController.showMessage(InfoController.LogLevel.Error, "Error"
                    , "Signup failed. Please try another username.");
            return;
        }
        closeWindow();

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("tafel.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle(displayBoard.getBoardName());
        stage.setScene(scene);
        stage.show();

        if (fxmlLoader.getController() instanceof ChatInterface) {
            ChatInterface currentController = fxmlLoader.getController();
            currentController.setClientRMI(clientRMI);
            currentController.setDisplayBoard(displayBoard);
            currentController.setUserId(userId);
            currentController.setUsername(user.getUsername());
            currentController.onStart();
        }
    }

    private boolean connectToServer(String tafelName, String hostip) {
        if (!hostip.matches(IP_REGEX))
        {
            InfoController.showMessage(InfoController.LogLevel.Error, "Error", "IP address is not correct");
            return false;
        }

        this.clientRMI = new ClientRMI();
        this.displayBoard = clientRMI.connectToServer(tafelName, hostip);

        if (displayBoard == null) {
            InfoController.showMessage(InfoController.LogLevel.Warn, "Error", "Could not connect to the server");
            return false;
        }

        return true;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 350, 420);
        stage.setTitle("AnzeigeTafel v1.0");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void onStart(String[] args) {
        launch(args);
    }

    @Override
    public void onRun() {

    }

    @Override
    public void onShutdown() {

    }
}
