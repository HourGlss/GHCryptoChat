package client;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {
	private PrivateKey priv;
	private PublicKey pub;
	public static final String ALGORITHM = "RSA";

	
	public RSA() {
		KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance(ALGORITHM);
			keyGen.initialize(1024);
			KeyPair key = keyGen.generateKeyPair();
			this.pub = key.getPublic();
			this.priv = key.getPrivate();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}


	}

	public byte[] encrypt(String text, PublicKey key) {
		byte[] cipherText = null;
		// get an RSA cipher object and print the provider
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
		// encrypt the plain text using the public key
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		try {
			cipherText = cipher.doFinal(text.getBytes());
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		return cipherText;
	}

	public String decrypt(byte[] text, PrivateKey key) {
		byte[] dectyptedText = null;

		// get an RSA cipher object and print the provider
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}

		// decrypt the text using the private key
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		try {
			dectyptedText = cipher.doFinal(text);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return new String(dectyptedText);
	}
	
	public String decrypt(byte[] text){
		return decrypt(text,this.priv);
	}

	public PublicKey getPublicKey() {
		return pub;
	}
	
	public static void main(String[] args){
		RSA rsa = new RSA();
		System.out.println(rsa.getPublicKey());
	}
}