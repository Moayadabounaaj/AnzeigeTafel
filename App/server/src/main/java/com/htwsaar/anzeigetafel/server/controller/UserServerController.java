package com.htwsaar.anzeigetafel.server.controller;

import com.htwsaar.anzeigetafel.model.User;
import com.htwsaar.anzeigetafel.server.ServerRMI;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;

public class UserServerController implements ControllerApi{


    public TableView<User> userTable;

    public TableColumn<User, Integer> userid;

    public TableColumn<User, String> username;

    public TableColumn<User, String> userpassword;

    public TableColumn<User, String> roll;

    public TableColumn<User, String> bords;

    public TextField seachBarID;

    private ServerRMI serverRMI ;



    @Override
    public void onStart() {
        userid.setCellValueFactory(new PropertyValueFactory<>("id"));
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        userpassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        roll.setCellValueFactory(new PropertyValueFactory<>("roll"));
        bords.setCellValueFactory(new PropertyValueFactory<>("bords"));

        //refresh the database and load the data from it on the table
        onRefresh();
    }

    @Override
    public void onUpdate() {

    }


    public void onRefresh()
    {
            ObservableList<User> userObservableList = FXCollections.observableArrayList(serverRMI.getAllUsers());
            userTable.setItems(userObservableList);

    }

    public void onAddUser(ActionEvent event) throws IOException
    {
        createFXMLoader("addUser.fxml", 600, 400, "Add User");
    }

    public void onDeleteUser (ActionEvent event) throws IOException
    {
        createFXMLoader("deleteUser.fxml", 370, 161, "Delete User");
    }

    public void onUpdateUser(ActionEvent event) throws IOException{

        createFXMLoader("updateUser.fxml", 600, 400, "Update User");
    }

    public void onSearch() throws RemoteException
    {
        User user = serverRMI.getUserByUsername(seachBarID.getText());
        ObservableList<User> userObservableValue = FXCollections.observableArrayList(user);
        userTable.setItems(userObservableValue);
    }

    public void createFXMLoader(String string, int width, int height, String description) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(string));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        Stage stage = new Stage();
        stage.setTitle(description);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL); //default, for closing th pop up window
        stage.show();
        stage.resizableProperty().setValue(false);
        // todo curent stage
        ((ControllerApi)fxmlLoader.getController()).onStart();
    }

}
