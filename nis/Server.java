package nis;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread {// The server thread, listens for incomming connections, accepts them and then creates a server thread to deal with them

    private ServerSocket serverSocket;
    private Socket socket = null;
    int port;
    public boolean listening = true;

    public Server(int p) {
        port = p;

    }

    public void run() {

        try {

            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(30000);
            System.out.println("Listening on " + port);

            while (listening) {
                socket = serverSocket.accept();
                System.out.println("Accepted");

                System.out.println("Recieved: " + NetUtil.getString(socket));
                NetUtil.sendMessage("reply", socket);

            }

            socket.getInputStream().close();
            socket.getOutputStream().close();
            socket.close();
            serverSocket.close();

        } catch (SocketTimeoutException e) {
            // Die gracefully
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}