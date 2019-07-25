package com.siemens.controller;

import com.siemens.view.ClientView;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @Author: Siemens CT Cluj-Napoca, Romania
 * @Since: Jul 25, 2019
 * @Description:
 *  Controller for the interface elements of the client.
 */
public class ClientController {

    private static final String NUMBER_FORMAT_EXCEPTION_MESSAGE = "Please enter a number!";

    private ClientView clientView;

    public ClientController() {
        clientView = new ClientView();
        clientView.setVisible(true);

        clientView.addBtnTestActionListener(new TestActionListener());
    }


    public void displayErrorMessage(String message) {
        clientView.clear();
        JOptionPane.showMessageDialog(clientView, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Provides functionality for the TEST button.
     */
    class TestActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                clientView.printHello();

            } catch (NumberFormatException ex) {
                displayErrorMessage(NUMBER_FORMAT_EXCEPTION_MESSAGE);
            }
        }
    }
}
