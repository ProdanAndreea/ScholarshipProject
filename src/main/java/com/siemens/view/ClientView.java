package com.siemens.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.jdesktop.swingx.JXDatePicker;

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
        btnGet = createSimpleButton("GET");
        btnGet.setBounds(235, 77, 89, 23);
        contentPane.add(btnGet);

        textArea = new JTextArea();
        textArea.setBounds(235, 131, 171, 120);
        contentPane.add(textArea);

        JXDatePicker picker = new JXDatePicker();
        picker.setDate(Calendar.getInstance().getTime());
        picker.setBounds(365, 77, 109, 23);
        picker.setFormats(new SimpleDateFormat("dd.MM.yyyy"));
        contentPane.add(picker);
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

    private JButton createSimpleButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);
        button.setFocusable(false);
        //Border line = new LineBorder(Color.BLACK);
        //Border margin = new EmptyBorder(5, 15, 5, 15);
        //xBorder compound = new CompoundBorder(line, margin);
        //button.setBorder(compound);
        return button;
    }

}

