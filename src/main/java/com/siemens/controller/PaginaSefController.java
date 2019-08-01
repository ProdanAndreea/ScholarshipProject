package com.siemens.controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.SignatureUtil;
import com.siemens.model.Client;
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
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        SimpleDateFormat parser = new SimpleDateFormat("HH-mm");

        PdfDocument pdfDoc;
        boolean isSigned;

        ArrayList<Request> array = new ArrayList<>();
        for(File file : folder.listFiles()){
            String[] parts = file.getName().split("_");

            try {
                pdfDoc = new PdfDocument(new PdfReader(ClientStart.fileDirectoryPath + "\\" + file.getName()));
                SignatureUtil signUtil = new SignatureUtil(pdfDoc);
                isSigned = (signUtil.getSignatureNames().size() == 0 ? false : true);

                requestObservableList.add(
                        new Request(file.getName(), "", file, isSigned, LocalDate.parse(parts[2], formatter), parser.parse(parts[3]))
                );

                array.add(new Request(file.getName(), "", file, isSigned, LocalDate.parse(parts[2], formatter), parser.parse(parts[3])));


            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Request> sorted = mergeSort(array);
        for (int i=0; i<array.size(); i++) {
            requestObservableList.set(i, sorted.get(i));
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



    private ArrayList<Request> mergeSort(ArrayList<Request> whole) {
        ArrayList<Request> left = new ArrayList<Request>();
        ArrayList<Request> right = new ArrayList<Request>();
        int center;

        if (whole.size() == 1) {
            return whole;
        } else {
            center = whole.size()/2;
            // copy the left half of whole into the left.
            for (int i=0; i<center; i++) {
                left.add(whole.get(i));
            }

            //copy the right half of whole into the new arraylist.
            for (int i=center; i<whole.size(); i++) {
                right.add(whole.get(i));
            }

            // Sort the left and right halves of the arraylist.
            left  = mergeSort(left);
            right = mergeSort(right);

            // Merge the results back together.
            merge(left, right, whole);
        }
        return whole;
    }

    private void merge(ArrayList<Request> left, ArrayList<Request> right, ArrayList<Request> whole) {
        int leftIndex = 0;
        int rightIndex = 0;
        int wholeIndex = 0;

        // As long as neither the left nor the right ArrayList has
        // been used up, keep taking the smaller of left.get(leftIndex)
        // or right.get(rightIndex) and adding it at both.get(bothIndex).
        while (leftIndex < left.size() && rightIndex < right.size()) {
            if ( (left.get(leftIndex).compareTo(right.get(rightIndex))) < 0) {
                whole.set(wholeIndex, left.get(leftIndex));
                leftIndex++;
            } else {
                whole.set(wholeIndex, right.get(rightIndex));
                rightIndex++;
            }
            wholeIndex++;
        }

        ArrayList<Request> rest;
        int restIndex;
        if (leftIndex >= left.size()) {
            // The left ArrayList has been use up...
            rest = right;
            restIndex = rightIndex;
        } else {
            // The right ArrayList has been used up...
            rest = left;
            restIndex = leftIndex;
        }

        // Copy the rest of whichever ArrayList (left or right) was not used up.
        for (int i=restIndex; i<rest.size(); i++) {
            whole.set(wholeIndex, rest.get(i));
            wholeIndex++;
        }
    }
}
