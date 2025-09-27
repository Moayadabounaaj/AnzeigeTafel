package com.htwsaar.anzeigetafel.server.controller;

import com.htwsaar.anzeigetafel.server.ServerRMI;
import com.htwsaar.anzeigetafel.model.DisplayBoard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;


import java.awt.event.ActionEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
public class BordServerController implements ControllerApi{


    public TableView<DisplayBoard> boardTable;

    public TableColumn<DisplayBoard, Integer> bordIdSer;

    public TableColumn<DisplayBoard, String> bordnameSer;

    private ServerRMI serverRMI;

    public TextField seachBarID;





    @Override
    public void onStart() {
        bordIdSer.setCellValueFactory(new PropertyValueFactory<>("idboardID"));
        bordnameSer.setCellValueFactory(new PropertyValueFactory<>("boardName"));

        //refresh the database and load the data from it on the table
        onRefresh();
    }

    @Override
    public void onUpdate() {

        onRefresh();
    }


    public void onRefresh()
    {
            List<DisplayBoard> displayBoards = serverRMI.getAllBoards();
            ObservableList<DisplayBoard> displayBoardObservableList = FXCollections.observableArrayList(displayBoards);
    }

    public void addBordBtn(ActionEvent event) throws IOException {
        createFXMLoader("addBord.fxml", 450, 720, "Add a Bord");

    }

    public void deleteBordBtn (ActionEvent event) throws IOException{

        createFXMLoader("deleteBord.fxml", 370, 161, "Delete a Bord");
    }


    public void onSearch() throws RemoteException {
        DisplayBoard displayBoards = serverRMI.getDisplayboard(seachBarID.getText());
        ObservableList<DisplayBoard> displayBoardObservableList = FXCollections.observableArrayList(displayBoards);
        boardTable.setItems(displayBoardObservableList);
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
