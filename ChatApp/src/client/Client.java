package client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Client extends JFrame{

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String serverIP;
    private Socket connection;

    public Client(String host) throws HeadlessException {
        super("AbstractThinking Client");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.setBackground(Color.lightGray);

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
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(720, 450);
        setVisible(true);
/*
        TextArea t=new TextArea();
        add(t, BorderLayout.SOUTH);
        t.setBackground(Color.lightGray);
        add(t);
        setLayout(new FlowLayout());
        setSize(720, 450);
        setVisible(true);
  */
    }

    public void startRunning(){


        try {
            Connection();
            Setup();
            chatting();
        } catch (EOFException eofException) {
            showMessage("\n Client Connection Lost");
        } catch (IOException e){
            e.printStackTrace();
        }
        finally {
            closeAll();
        }

    }

    private void Connection() throws IOException{
        showMessage("Connecting...\n");
        connection = new Socket(InetAddress.getByName(serverIP), 3000);
        showMessage("Connected to " + connection.getInetAddress().getHostName() + "\n");
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
        while (!message.equals("SERVER - END"));
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
            output.writeObject("CLIENT - " + message);
            output.flush();
            showMessage("CLIENT - " + message + "\n");
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
