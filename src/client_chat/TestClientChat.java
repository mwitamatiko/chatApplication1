package client_chat;

import javax.swing.*;

public class TestClientChat {
    public static void main(String[] args) {
        ClientChat clientChat;
        //if no command line arguments
        if(args.length==0)
            clientChat=new ClientChat("127.0.0.1");
        else
            clientChat=new ClientChat(args[0]);

        clientChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientChat.startClient();
    }
}
