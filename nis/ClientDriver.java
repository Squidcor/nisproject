package nis;

public class ClientDriver {// Contains the main method to start the client kills it after 80 seconds

	public static void main(String[] args) {
		try {

			Client client = new Client("localhost", 32528);
			client.start();

			Thread.currentThread().sleep(80000);

			client.running = false;

		} catch (Exception e) {
			e.printStackTrace();

		}

	}
}
