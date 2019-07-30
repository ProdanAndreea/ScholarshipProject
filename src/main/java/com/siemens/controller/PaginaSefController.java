package com.siemens.controller;

import com.siemens.model.Request;
import com.siemens.view.ClientStart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PaginaSefController {
    private ObservableList<Request> requestObservableList = FXCollections.observableArrayList();

    private final File folder = new File(ClientStart.fileDirectoryPath);
    @FXML
    private Button acceptButton;

    @FXML
    private Button denyButton;

    @FXML
    private ListView requestListView;

    private void populateRequests(){
        for(File file : folder.listFiles()){
            requestObservableList.add(
                    new Request(file.getName(), "", file, false)
            );
        }
    }
    private void refreshItems(){
        requestListView.setItems(null);
        requestListView.setItems(requestObservableList);
    }

    private void setHandlers(){
        requestListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2){
                    Request selectedRequest = (Request) requestListView.getSelectionModel().getSelectedItem();
                    requestObservableList.forEach(request -> {
                        if(request.equals(selectedRequest)){
                            try{
                                Desktop.getDesktop().open(request.getFile());
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            request.setSigned(true);
                        }

                    });
                    refreshItems();
                }
            }
        });
        acceptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });
        denyButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });
    }

    public void initialize(){
        requestListView.setItems(requestObservableList);
        populateRequests();
        setHandlers();
    }
}
