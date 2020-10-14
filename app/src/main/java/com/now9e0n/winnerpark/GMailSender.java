package com.now9e0n.winnerpark;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.Getter;
import lombok.SneakyThrows;

public class GMailSender extends javax.mail.Authenticator {
    @Getter private static GMailSender instance = new GMailSender();

    private String user;
    private String password;
    @Getter private String emailCode;
    private Session session;

    private GMailSender() {
        user = "now9e0n@gmail.com";
        password = "adicoehaphdzndgf";

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    private String createCode() {
        String[] str = { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        StringBuilder newCode = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int random = (int) (Math.random() * str.length);
            newCode.append(str[random]);
        }
        
        return newCode.toString();
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
            Log.e("GMailSender", "Email Address Validate Failed", e);
            result = false;
        }

        return result;
    }

    public void sendMail(String recipients, Runnable runnable) {
        new Thread() {
            @SneakyThrows(Exception.class)
            @Override
            public void run() {
                emailCode = createCode();
                String body = emailCode + " is your email verification code";
                DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));

                MimeMessage message = new MimeMessage(session);
                try {
                    message.setSender(new InternetAddress(user));
                } catch (MessagingException e) {
                    Log.e("GMailSender", "Set Sender Failed", e);
                }
                message.setSubject("Email Verification Code");
                message.setDataHandler(handler);

                if (recipients.indexOf(',') > 0)
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
                else message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));

                Transport.send(message);
                runnable.run();
            }
        }.start();
    }

    private static class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        @Override
        public String getContentType() {
            if (type == null) return "application/octet-stream";
            else return type;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(data);
        }

        @Override
        public String getName() {
            return "ByteArrayDataSource";
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}