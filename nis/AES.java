/*
 * File: AES.java
 * Author: David Shorten
 * Date: 17 March 2013
 */

package nis;

import java.net.Socket;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* 
 * Class which performs AES encryption in CBC mode. It is written in order 
 * to function in the context of a client-server application sending messages
 * over a network (the client always sends, the server always receives).
 * Therefore, the encryption method also sends the data and the decryption 
 * method also receives data.
 */
public class AES
{
	SecretKeySpec key;
	Cipher theCipher;
	
	public AES(byte[] keyBytes) {
		key = new SecretKeySpec(keyBytes, 0, 16, "AES");
		try {
			theCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public void encryptThenSend(byte[] plainText, Socket sock, boolean verbose) {
		if(verbose) {
			System.out.println();
			System.out.println("----------------------------------------------");
			System.out.print("Performing AES encryption on plain text: ");
			System.out.println(new String(plainText));
			System.out.print("The byte representation of the plaintext is: ");
			printBytes(plainText);
			System.out.println();
		}
		
		// Create the random initialisation vector.
		SecureRandom randomGenerator = new SecureRandom();
		byte[] iv = new byte[16]; 
		randomGenerator.nextBytes(iv);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		if(verbose) {
			System.out.print("Generated AES IV: ");
			printBytes(iv);
			System.out.println();
		}
		
		// Perform the encryption.
		byte[] messageCipherText = null;
		try {
			theCipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key, ivSpec);
			messageCipherText = theCipher.doFinal(plainText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(verbose) {
			System.out.print("The encrypted bytes are: ");
			printBytes(messageCipherText);
			System.out.println();
		}
		
		// Insert the iv at the beginning of the message.
		byte[] cipherText = new byte[iv.length 
		                                 + messageCipherText.length];
		System.arraycopy(iv, 0, cipherText, 0, iv.length);
		System.arraycopy(messageCipherText, 0, cipherText, iv.length,
				messageCipherText.length);
		if(verbose) {
			System.out.print("The final message is: ");
			printBytes(cipherText);
			System.out.println();
			System.out.println("----------------------------------------------");
		}
		
		// Send the message.
		try {
			NetUtil.sendMessage(cipherText, sock);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public byte[] receiveThenDecrypt(Socket sock, boolean verbose) throws Exception {
		// Get the cipher text.
		byte[] cipherText = (byte[]) NetUtil.getMessage(sock);
		if(verbose) {
			System.out.println();
			System.out.println("----------------------------------------------");
			System.out.print("Performing AES decryption on received bytes: ");
			printBytes(cipherText);
			System.out.println();
		}
		
		// Separate the IV and the message.
		byte[] iv = Arrays.copyOfRange(cipherText, 0, 16);
		byte[] messageCipherText = Arrays.copyOfRange(cipherText, 16, 
				cipherText.length);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		if(verbose) {
			System.out.print("The IV is: ");
			printBytes(iv);
			System.out.println();
			System.out.print("The cipher text is: ");
			printBytes(messageCipherText);
			System.out.println();
		}

		// Perform the decryption.
		theCipher.init(javax.crypto.Cipher.DECRYPT_MODE, key, ivSpec);		
		byte[] plainText = theCipher.doFinal(messageCipherText);
		if(verbose) {
			System.out.print("The decrypted bytes are: ");
			printBytes(plainText);
			System.out.println();
			System.out.print("The string representation of these bytes is: ");
			System.out.println(new String(plainText));
			System.out.println("----------------------------------------------");
		}
		
		return plainText;
	}	
	
	// Method for printing out byte arrays in the verbose mode.
	private void printBytes(byte[] bytes) {
		for(byte element : bytes) {
			System.out.print(element);
		}
	}
}