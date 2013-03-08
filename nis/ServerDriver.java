package nis;

public class ServerDriver {

	public static void main(String[] args) {// Contains the main method to start the server, kills it after 120 seconds
		try {

			Server server = new Server(32528);

			server.start();

			Thread.currentThread().sleep(120000);

			server.listening = false;

		} catch (Exception e) {
			e.printStackTrace();

		}

	}
}