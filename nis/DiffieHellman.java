package nis;

import java.math.BigInteger;
import java.net.*;
import java.security.*;

public class DiffieHellman {

	private Socket sock;
	private BigInteger privateKey;
	private BigInteger publicKey;
	private BigInteger sharedSecretKey;
	private BigInteger base = null;
	private BigInteger prime = null;
	private SecureRandom random;
	private int keyBitLength;

	public DiffieHellman(Socket sock, int keyBitLength) {// Creates a new DH
															// that with
															// 'keyBitLength'
															// key size. Needs a
															// socket connection
															// to the other
															// party
		this.sock = sock;
		this.keyBitLength = keyBitLength;
		random = new SecureRandom();
	}

	public void initialise() {// Generates a prime and base which will the be
								// shared with the other party, only one part
								// must initialise
		prime = generatePrime();
		base = generateBase();
	}

	public void generateKeyPair() throws Exception {// Communicates with the
													// other party and generates
													// a public and private key
													// pair accodring to the DH
													// protocol

		if (prime != null) {
			NetUtil.sendMessage(prime, sock);
			NetUtil.sendMessage(base, sock);
		} else {
			prime = (BigInteger) NetUtil.getMessage(sock);
			base = (BigInteger) NetUtil.getMessage(sock);
		}

		privateKey = generatePrivateKey();
		publicKey = base.modPow(privateKey, prime);

		NetUtil.sendMessage(publicKey, sock);
		BigInteger otherPublicKey = (BigInteger) NetUtil.getMessage(sock);
		sharedSecretKey = otherPublicKey.modPow(privateKey, prime);

	}

	public BigInteger getPrivateKey() {
		return privateKey;
	}

	public BigInteger getPublicKey() {
		return publicKey;
	}

	public void printKeys() {
		System.out.println("\n" + keyBitLength + "bit Diffie Hellman");
		System.out.println("Private Key: " + privateKey);
		System.out.println("Public Key: " + publicKey);
		System.out.println("Shared Secret Key: " + sharedSecretKey);
	}

	private BigInteger generatePrime() {
		return BigInteger.probablePrime(keyBitLength, random);
	}

	private BigInteger generateBase() {
		return BigInteger.valueOf(2);
	}

	private BigInteger generatePrivateKey() {
		return new BigInteger(keyBitLength, random);
	}
}
