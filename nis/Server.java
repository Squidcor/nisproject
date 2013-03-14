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
                System.out.println("Accepted Connection");

                DiffieHellman keyExchange = new DiffieHellman(socket,16);
                keyExchange.generateKeyPair();
                keyExchange.printKeys();

                String key = new String("0123456789abcdef");
				byte[] keyBytes = key.getBytes("US-ASCII");
				AES aesMachine = new AES(keyBytes);
				byte[] plainText = aesMachine.receiveThenDecrypt(socket);
				System.out.println(new String(plainText));
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
