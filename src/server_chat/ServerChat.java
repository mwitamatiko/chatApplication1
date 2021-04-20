package server_chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerChat extends JFrame {
    private JTextField messageField;
    private JTextArea displayArea;
    private JLabel label;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket socket;
    private int clientNo=1;

    public ServerChat(){

        messageField=new JTextField();
        messageField.setEditable(false);
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendInfo(e.getActionCommand());
                messageField.setText(" ");
            }
        });
        Font font = new Font("SansSerif",Font.BOLD,25);
        displayArea=new JTextArea();
        label = new JLabel("SERVER APPLICATION");
        add(label,BorderLayout.NORTH);
        label.setFont(font);
        add(new JScrollPane(displayArea),BorderLayout.CENTER);
        add(messageField,BorderLayout.SOUTH);

        setSize(400,400);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    //set up and run the server application
    public void startServer(){
        try{
            server = new ServerSocket(56789,100);
            while(true){
                try{
                    waitForConnection();
                    getTheStreams();
                    processClientConnection();
                }catch (EOFException eofException){
                    displayMessage("\nserver terminated the connection");
                }finally {
                    closeConnection();
                    clientNo++;
                }
            }
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    //keep waiting for a connection & display connection info
    private void waitForConnection() throws IOException{
        displayMessage("waiting for client connection\n");
        socket = server.accept();
        displayMessage("Connection "+clientNo+" received from: "+socket.getInetAddress().getHostName());
    }

    //get the input and output streams
    private void getTheStreams() throws IOException{
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();

        input = new ObjectInputStream(socket.getInputStream());
        displayMessage("\nGet I/O streams\n");
    }

    //next, process the connection with a client
    private void processClientConnection() throws IOException{
        String msg = "connection successful";
        sendInfo(msg);
        setTextFieldEditable(true);

        //process message received from client
        do{
            try{
                msg=(String)input.readObject();
                displayMessage("\n"+msg);
            }catch (ClassNotFoundException classNotFoundException){
                displayMessage("\nUnknown object type received");
            }
        }while(!msg.equals("CLIENT>>> BYE"));
    }

    //close the streams and socket
    private void closeConnection(){
        displayMessage("\nTerminating the connection\n");
        setTextFieldEditable(false);

        try{
            output.close();
            input.close();
            socket.close();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    //send message to client
    private void sendInfo(String message){
        try{
            output.writeObject("SERVER>>> "+message);
            output.flush();
            displayMessage("\nSERVER>>> "+message);
        }catch (IOException ioException){
            displayArea.append("\nError writing object");
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
