package com.example.yujuworkfx.FinalProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;

public class SimpleChatApp {

    public static void main(String[] args) {
        JFrame serverFrame = new JFrame("Server");
        JFrame clientFrame = new JFrame("Client");

        serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        serverFrame.setSize(400, 300);
        clientFrame.setSize(400, 300);

        JTextArea serverTextArea = new JTextArea();
        JTextArea clientTextArea = new JTextArea();

        JTextField serverInputField = new JTextField();
        JTextField clientInputField = new JTextField();

        serverTextArea.setEditable(false);
        clientTextArea.setEditable(false);

        JButton serverSendButton = new JButton("Send");
        JButton clientSendButton = new JButton("Send");

        serverFrame.setLayout(new BorderLayout());
        clientFrame.setLayout(new BorderLayout());

        serverFrame.add(new JScrollPane(serverTextArea), BorderLayout.CENTER);
        clientFrame.add(new JScrollPane(clientTextArea), BorderLayout.CENTER);

        JPanel serverInputPanel = new JPanel(new BorderLayout());
        serverInputPanel.add(serverInputField, BorderLayout.CENTER);
        serverInputPanel.add(serverSendButton, BorderLayout.EAST);

        JPanel clientInputPanel = new JPanel(new BorderLayout());
        clientInputPanel.add(clientInputField, BorderLayout.CENTER);
        clientInputPanel.add(clientSendButton, BorderLayout.EAST);

        serverFrame.add(serverInputPanel, BorderLayout.SOUTH);
        clientFrame.add(clientInputPanel, BorderLayout.SOUTH);

        serverFrame.setVisible(true);
        clientFrame.setVisible(true);

        ChatServer1 server = new ChatServer1(serverTextArea);
        ChatClient1 client = new ChatClient1(clientTextArea);

        serverSendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                server.sendMessage(serverInputField.getText());
                serverInputField.setText("");
            }
        });

        clientSendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                client.sendMessage(clientInputField.getText());
                clientInputField.setText("");
            }
        });

        Thread serverThread = new Thread(server);
        Thread clientThread = new Thread(client);

        serverThread.start();
        clientThread.start();
    }
}

class ChatServer implements Runnable {
    private JTextArea textArea;
    private ArrayList<PrintWriter> clientWriters = new ArrayList<>();

    public ChatServer(JTextArea textArea) {
        this.textArea = textArea;
    }

    public void sendMessage(String message) {
        textArea.append("Server: " + message + "\n");
        for (PrintWriter writer : clientWriters) {
            writer.println("Server: " + message);
        }
    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(12345);
            textArea.append("Server is running and waiting for clients...\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                textArea.append("Client connected.\n");

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(out);

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String clientMessage;

                while ((clientMessage = in.readLine()) != null) {
                    textArea.append("Client: " + clientMessage + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class ChatClient implements Runnable {
    private JTextArea textArea;
    private PrintWriter out;

    public ChatClient(JTextArea textArea) {
        this.textArea = textArea;
    }

    public void sendMessage(String message) {
        textArea.append("You: " + message + "\n");
        out.println("You: " + message);
    }

    public void run() {
        try {
            String serverIP = "localhost"; // Change this to the IP address of your server if needed
            int serverPort = 12345;

            Socket socket = new Socket(serverIP, serverPort);
            textArea.append("Connected to the server.\n");

            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverMessage;

            while ((serverMessage = in.readLine()) != null) {
                textArea.append(serverMessage + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}