package com.siemens.configuration;

import com.siemens.view.ClientStart;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.*;
import java.security.CodeSource;
import java.util.Properties;

public class MailConfiguration {
// OBSOLETE, THE MAIL DOES NOT CONTAIN ATTACH ANY LONGER, UNCOMMENT IF IT IS DESIRED TO USE IN THE FUTURE
//    public static void sendMessage(String to, String subject, String text, String path) {
//
//        try {
//            Properties prop = new Properties();
//
//
//            CodeSource codeSource = ClientStart.class.getProtectionDomain().getCodeSource();
//            File jarFile = new File(codeSource.getLocation().toURI().getPath());
//            String jarDir = jarFile.getParentFile().getPath();
//            FileInputStream file = new FileInputStream(jarDir + "\\mail.properties");
//            //load all the properties from this file
//            prop.load(file);
//            //we have loaded the properties, so close the file handle
//            file.close();
//
//            String username = prop.getProperty("username");
//            String password = prop.getProperty("password");
//
//            StringBuilder decryptedPassword = new StringBuilder();
//
//            for (int i = 0; i < password.length(); i++) {
//                if (i % 2 == 0) {
//                    decryptedPassword.append((char)(password.charAt(i) - 3));
//                } else {
//                    decryptedPassword.append((char)(password.charAt(i) - 4));
//                }
//            }
//
//
//            Properties props = new Properties();
//            props.put("mail.smtp.host", "smtp.gmail.com");
//            props.put("mail.transport.protocol", "smtp");
//            props.put("mail.smtp.starttls.enable", "true");
//            props.put("mail.smtp.port", "587");
//            props.put("mail.smtp.user", username);
//            props.put("mail.smtp.password", decryptedPassword.toString());
//            props.put("mail.smtp.auth", "true");
//
//
//            Session session = Session.getInstance(props,
//                    new javax.mail.Authenticator() {
//                        protected PasswordAuthentication getPasswordAuthentication() {
//                            return new PasswordAuthentication(username, decryptedPassword.toString());
//                        }
//                    });
//
//            try {
//                InternetAddress fromAddress = new InternetAddress(username);
//                InternetAddress toAddress = new InternetAddress(to);
//
//                Message message = new MimeMessage(session);
//                message.setFrom(fromAddress);
//                message.setRecipient(Message.RecipientType.TO, toAddress);
//                message.setSubject(subject);
//
//                BodyPart messageBodyPart = new MimeBodyPart();
//                messageBodyPart.setText(text);
//
//
//                Multipart multipart = new MimeMultipart();
//                multipart.addBodyPart(messageBodyPart);
//
//
//                messageBodyPart = new MimeBodyPart();
//                DataSource source = new FileDataSource(path); // "/project/whatever/file.txt"
//                messageBodyPart.setDataHandler(new DataHandler(source));
//                messageBodyPart.setFileName(path);
//                multipart.addBodyPart(messageBodyPart);
//
//                message.setContent(multipart);
//
//                Transport.send(message);
//
//            } catch (MessagingException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            System.out.println("Error reading credentials");
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }
    public static void sendMessage(String to, String subject, String text) {

        try {
            Properties prop = new Properties();


            CodeSource codeSource = ClientStart.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();
            FileInputStream file = new FileInputStream(jarDir + "\\mail.properties");
            //load all the properties from this file
            prop.load(file);
            //we have loaded the properties, so close the file handle
            file.close();

            String password = ClientStart.decodeMessage(prop.getProperty(ClientStart.encodeMessage("password")));

            Properties props = new Properties();
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.host", "smtp.gmail.com");

//            props.put("mail.smtp.host", "mail.siemens.de");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.user", ClientStart.senderMail);
            props.put("mail.smtp.password",password);
            props.put("mail.smtp.auth", "true");


            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(ClientStart.senderMail, password);
                        }
                    });

            try {
                InternetAddress fromAddress = new InternetAddress(ClientStart.senderMail);
                InternetAddress toAddress = new InternetAddress(to);

                Message message = new MimeMessage(session);
                message.setFrom(fromAddress);
                message.setRecipient(Message.RecipientType.TO, toAddress);
                message.setSubject(subject);

                //No longer needed, no attachments added
//                BodyPart messageBodyPart = new MimeBodyPart();
//                messageBodyPart.setText(text);
//
//
//                Multipart multipart = new MimeMultipart();
//                multipart.addBodyPart(messageBodyPart);

                message.setContent(text, "text/html; charset=utf-8");

                Transport.send(message);

            } catch (MessagingException e) {
                ClientStart.logger.severe(e.getMessage());
            }
        } catch (IOException e) {
            ClientStart.logger.severe(e.getMessage());
        }
        catch (Exception e){
            ClientStart.logger.severe(e.getMessage());
        }
    }

}