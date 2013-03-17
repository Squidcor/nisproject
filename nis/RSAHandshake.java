package nis;

import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import java.nio.ByteBuffer;

import javax.crypto.Cipher;

public class RSAHandshake {
	private Socket socket;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	
	final static int RSA_KEY_SIZE = 512;
	
	public RSAHandshake(Socket socket) throws NoSuchAlgorithmException{
		this.socket = socket;
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		
		kpg.initialize(RSA_KEY_SIZE);
		KeyPair keyPair = kpg.generateKeyPair();
		privateKey = keyPair.getPrivate();
		publicKey = keyPair.getPublic();
	}
	
	//Handshake Protocol:
	//Client:	send public key
	//Sever:	recieve client public key
	//			send server public key
	//Client:	recieve server public key
	//			generate nonce_C -> encrypt w sever public key
	//			send E[nonce_C]
	//Server:	decrypt -> iterate nonce_C -> encrypt
	//			generate nonce_S
	//			encrypt w client pub key
	//			send both encrypted nonces
	//Client:	decrypt & validate nonce_C
	//			decrypt -> iterate nonce_S -> encrypt
	//			send E[nonce_S]
	//Server:	decrpyt & validate nonce_S
	//			create AES key, encrypt and send
	//Client:	recieve AES key
	
	public void clientHandshake() throws Exception{
		System.out.println("\nStarting Handshake (as client)\n");
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		
		//Exchange public keys
		NetUtil.sendMessage(publicKey, socket);
		PublicKey serverPublicKey = (PublicKey) NetUtil.getMessage(socket);
		
		//Send client nonce
		Integer clientNonce = generateNonce();
		System.out.println("Generated client nonce: "+clientNonce);
		cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
		NetUtil.sendMessage(cipher.doFinal(intToByteArray(clientNonce)), socket);
		
		//Recieve & check iterated client nonce
		byte[] recievedClientNonceBytes = (byte[]) NetUtil.getMessage(socket);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		int recievedClientNonce = byteArrayToInt(cipher.doFinal(recievedClientNonceBytes));
		System.out.println("Recieved client nonce: "+recievedClientNonce);
		
		if(recievedClientNonce != clientNonce+1)
			System.out.println("ERROR: Recieved client nonce did not match expected value!");
		else{
			System.out.println("Recieved client nonce matched expected value.");

			//Recieve, decrypt, iterate, encrypt & send back server nonce
			byte[] serverNonceBytes = (byte[]) NetUtil.getMessage(socket);
			int serverNonce = byteArrayToInt(cipher.doFinal(serverNonceBytes));
			System.out.println("Recieved server nonce: "+serverNonce);
			serverNonce++;
			cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
			NetUtil.sendMessage(cipher.doFinal(intToByteArray(serverNonce)), socket);
		}
	}
	
	public void serverHandshake() throws Exception{
		System.out.println("\nStarting Handshake (as server)\n");
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		
		//Exchange public keys
		PublicKey clientPublicKey = (PublicKey)NetUtil.getMessage(socket);
		NetUtil.sendMessage(publicKey, socket);
		
		//Recieve, decrypt, iterate, encrypt & send back client nonce
		byte[] clientNonceEncrypted = (byte[]) NetUtil.getMessage(socket);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		int clientNonce = byteArrayToInt(cipher.doFinal(clientNonceEncrypted));
		System.out.println("Recieved client nonce: "+clientNonce);
		clientNonce++;
		cipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
		NetUtil.sendMessage(cipher.doFinal(intToByteArray(clientNonce)), socket);
		
		//Generate and send server nonce
		int serverNonce = generateNonce();
		System.out.println("Generated server nonce: "+serverNonce);
		NetUtil.sendMessage(cipher.doFinal(intToByteArray(serverNonce)), socket);
		
		//Recieve & check iterated server nonce
		byte[] recievedServerNonceBytes = (byte[]) NetUtil.getMessage(socket);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		int recievedServerNonce = byteArrayToInt(cipher.doFinal(recievedServerNonceBytes));
		System.out.println("Recieved server nonce: "+recievedServerNonce);
		if(recievedServerNonce!=serverNonce+1)
			System.out.println("ERROR: Recieved server nonce did not match the expected value");
		else{
			System.out.println("Server nonce matched the expected value");
			
			//TODO: Generate AES Key
		}
	}
	
	private static int generateNonce(){
		return (int) Math.round(Math.random()*(double)(Integer.MAX_VALUE-1000));
	}
	
	private static byte[] intToByteArray(final int i){
		return ByteBuffer.allocate(4).putInt(i).array();
	}
	
	private static int byteArrayToInt(final byte[] b){
		return ByteBuffer.wrap(b).getInt();
	}
}
