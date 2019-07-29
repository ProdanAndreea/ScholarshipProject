package com.siemens.configuration;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class MailConfiguration {

    public static void sendMessage(String to, String subject, String text, String path) {

        try {
            Properties prop = new Properties();
            String configFile = "mail.properties";

            InputStream inputStream = MailConfiguration.class.getResourceAsStream(configFile);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + configFile + "' not found in the classpath");
            }

            String username = prop.getProperty("username");
            String password = prop.getProperty("password");


            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.user", username);
            props.put("mail.smtp.password", password);
            props.put("mail.smtp.auth", "true");


            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {
                InternetAddress fromAddress = new InternetAddress(username);
                InternetAddress toAddress = new InternetAddress(to);

                Message message = new MimeMessage(session);
                message.setFrom(fromAddress);
                message.setRecipient(Message.RecipientType.TO, toAddress);
                message.setSubject(subject);

                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(text);

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);

                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(path); // "/project/whatever/file.txt"
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(path);
                multipart.addBodyPart(messageBodyPart);
                message.setContent(multipart);

                Transport.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Error reading credentials");
        }
    }

}
