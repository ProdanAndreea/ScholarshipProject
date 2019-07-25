package com.siemens.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;

/**
 * @Author: Siemens CT Cluj-Napoca, Romania
 * @Since: Jul 25, 2019
 * @Description:
 *	ClientView is a JFrame which contains the UI elements of the Client application.
 */
public class ClientView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JButton btnGet;
    private JTextArea textArea;


    public ClientView() {
        setTitle("Invoire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(300, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);


        btnGet = new JButton("GET");
        btnGet.setBounds(235, 77, 89, 23);
        contentPane.add(btnGet);

        textArea = new JTextArea();
        textArea.setBounds(235, 131, 171, 120);
        contentPane.add(textArea);
    }


    public void addBtnTestActionListener(ActionListener e) {
        btnGet.addActionListener(e);
    }

    public void printHello() {
        textArea.setText("Hello!");
    }

    public void clear() {
        textArea.setText("");
    }

}

