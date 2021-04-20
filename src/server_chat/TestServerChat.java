package server_chat;

import javax.swing.*;

public class TestServerChat {
    public static void main(String[] args) {
        ServerChat sc = new ServerChat();
        sc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sc.startServer();
    }
}
