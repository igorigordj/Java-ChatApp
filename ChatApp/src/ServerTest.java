import server.Server;

import javax.swing.*;

public class ServerTest {
    public static void main(String[] args) {
        Server Host = new Server();
        Host.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Host.startRunning();
    }
}
