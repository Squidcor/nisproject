package nis;

import java.io.InputStream;
import java.net.Socket;



public class Client extends Thread {

    String serverName;
    int port;
    Socket socket = null;
    public boolean running = true;
  

    public Client(String sname, int p) {
        serverName = sname;
        port = p;

    }

    public void run() {

        try {


            while (running) {
            	
            	 System.out.println("Attempting connection to " + serverName + " on " + port);
                 socket = new Socket(serverName, port);
                 
                DiffieHellman keyExchange= new DiffieHellman(socket,16);
                keyExchange.initialise();
                keyExchange.generateKeyPair();
                keyExchange.printKeys();
               

                break;
                
            }


        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    

    
    
}
