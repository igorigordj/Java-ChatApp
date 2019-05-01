package server;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import static java.awt.Color.magenta;


public class Server extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;

    public Server() throws HeadlessException {
        super("AbstractThinking Server");
        userText = new JTextField();
        userText.setEditable(false);
        userText.setBackground(Color.magenta);
        userText.setFont(Ti);
        userText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(e.getActionCommand());
                userText.setText("");
            }
        });
        add(userText, BorderLayout.SOUTH);
        chatWindow = new JTextArea();
        chatWindow.setEditable(false);

        add(new JScrollPane(chatWindow));
        setSize(720, 480);
        setVisible(true);
    }

    public void startRunning(){
        try {
            server = new ServerSocket(3000, 20);
            while (true){
                try {
                    Connection();
                    Setup();
                    chatting();
                }
                catch (EOFException eofException){
                    showMessage("\n Server Connection Lost");
                }
                finally {
                    closeAll();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Connection() throws IOException {
        showMessage("Waiting for connection...\n");
        connection = server.accept();
        showMessage(connection.getInetAddress().getHostName() + " connected\n");
    }

    private void Setup() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();

        input = new ObjectInputStream(connection.getInputStream());
        showMessage("Setup complete!\n");
    }

    private void chatting() throws IOException {
        String message = "Connected";
        sendMessage(message);
        canType(true);

        do {
            try{
                message = (String) input.readObject();
                showMessage(message + "\n");
            }
            catch (ClassNotFoundException e){
                showMessage("Unknown command\n");
            }
        }
        while (!message.equals("CLIENT - END"));
    }

    private void closeAll() {
        showMessage("Closing connections...\n");
        canType(false);
        try{
            output.close();
            input.close();
            connection.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void sendMessage(String message){
        try {
            output.writeObject("SERVER - " + message);
            output.flush();
            showMessage("SERVER - " + message + "\n");
        }
        catch (IOException e){
            chatWindow.append("Error - Message cannot be sent");
        }
    }

    private void showMessage(final String text) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(text);
                    }
                }
        );
    }

    private void canType(final boolean tof) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        userText.setEditable(tof);
                    }
                }
        );
    }
}
