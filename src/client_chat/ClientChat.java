package client_chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientChat extends JFrame {
    private JTextField messageField;
    private JTextArea displayArea;
    private JLabel label;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String msg="";
    private String chatServer;
    private Socket socket;


    public  ClientChat(String host){
        Font font = new Font("SansSerif",Font.BOLD,25);
        label = new JLabel("CLIENT APPLICATION");
        add(label,BorderLayout.NORTH);
        label.setFont(font);
        messageField=new JTextField();
        messageField.setEditable(false);
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //send message to server
                sendInfo(e.getActionCommand());
                messageField.setText(" ");
            }
        });

        add(messageField,BorderLayout.SOUTH);
        displayArea=new JTextArea();
        add(new JScrollPane(displayArea),BorderLayout.CENTER);
        setSize(400,400);
        setVisible(true);
    }

    //connect to server and process any message coming from the server
    public void startClient(){
        try{
            connectToServer();
            getTheStreams();
            processTheConnection();
        }catch (EOFException eofException){
            displayMessage("\nclient terminated connection");
        }catch (IOException ioException){
            ioException.printStackTrace();
        }finally {
            closeConnection();
        }
    }

    //connect to the server
    private void connectToServer() throws IOException{
        displayMessage("Attempting to connect\n");
        //create a socket to connect to server
        socket = new Socket(InetAddress.getByName(chatServer),56789);
        displayMessage("connected to "+socket.getInetAddress().getHostName());
    }

    //get streams
    private void getTheStreams() throws IOException{
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(socket.getInputStream());

        displayMessage("\ngot the I/O streams\n");
    }

    //connect with the server
    private void processTheConnection() throws IOException{
        setTextFieldEditable(true);

        do{
            try{
                msg = (String)input.readObject();
                displayMessage("\n "+msg);
            }catch (ClassNotFoundException c){
                displayMessage("\nunknown object type received");
            }
        }while(!msg.equals("SERVER>>> BYE"));
    }

    //close all streams and socket
    private void closeConnection(){
        displayMessage("\nclosing connection");
        setTextFieldEditable(false);

        try{
            output.close();
            input.close();
            socket.close();
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    //send message to the server
    private void sendInfo(String message){
        try{
            output.writeObject("CLIENT>>> "+message);
            output.flush();
            displayMessage("\nCLIENT>>> "+message);
        }catch (IOException ioe){
            displayArea.append("\nerror writing object");
        }
    }

    //manipulate display area
    private void displayMessage(final String msgToDisplay){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                displayArea.append(msgToDisplay);
            }
        });
    }

    //manipulate text field
    private void setTextFieldEditable(final boolean editable){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                messageField.setEditable(editable);
            }
        });
    }
}
