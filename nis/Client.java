package nis;

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

				System.out.println("Attempting connection to " + serverName
						+ " on " + port);
				socket = new Socket(serverName, port);

				DiffieHellman keyExchange = new DiffieHellman(socket, 16);
				keyExchange.initialise();
				keyExchange.generateKeyPair();
				keyExchange.printKeys();

				RSAHandshake rsa = new RSAHandshake(socket);
				rsa.clientHandshake();
				
				String key = new String("0123456789abcdef");
				byte[] keyBytes = key.getBytes("US-ASCII");
				AES aesMachine = new AES(keyBytes);
				byte[] plainText = "foo".getBytes("US-ASCII");
				aesMachine.encryptThenSend(plainText, socket);
				
				break;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
