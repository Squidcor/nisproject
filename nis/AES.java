package nis;

import java.net.Socket;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES
{
	SecretKeySpec key;
	Cipher theCipher;
	
	public AES(byte[] keyBytes) throws Exception{
		key = new SecretKeySpec(keyBytes, 0, 16, "AES");
		theCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

	}
	
	public void encryptThenSend(byte[] plainText, Socket sock) throws Exception {
		SecureRandom randomGenerator = new SecureRandom();
		byte[] iv = new byte[16]; 
		randomGenerator.nextBytes(iv);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		
		theCipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key, ivSpec);
		byte[] messageCipherText = theCipher.doFinal(plainText);
		
		byte[] cipherText = new byte[iv.length 
		                                 + messageCipherText.length];
		System.arraycopy(iv, 0, cipherText, 0, iv.length);
		System.arraycopy(messageCipherText, 0, cipherText, iv.length,
				messageCipherText.length);
		
		NetUtil.sendMessage(cipherText, sock);
	}
	
	public byte[] receiveThenDecrypt(Socket sock) throws Exception {
		byte[] cipherText = (byte[]) NetUtil.getMessage(sock);
		
		byte[] iv = Arrays.copyOfRange(cipherText, 0, 16);
		byte[] messageCipherText = Arrays.copyOfRange(cipherText, 16, 
				cipherText.length);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		theCipher.init(javax.crypto.Cipher.DECRYPT_MODE, key, ivSpec);		
		byte[] plainText = theCipher.doFinal(messageCipherText);
		
		return plainText;
	}
	
	
}