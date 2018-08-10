package com.ibm.security.resilient.common;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ibm.security.resilient.exception.ResilientBrokerException;

/**
 * AESEncryption class is for String object encryption and decryption. This
 * class is using 128 bit PBEWithMD5AndDES algorigh to encrypt and decrypt
 * String objects.
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 31st, 2017
 */
@Component
public class AESEncryption {
	private static Logger logger = LoggerFactory.getLogger(AESEncryption.class);
	/**
	 * Using 128 bit PBEWithMD5AndDES algorigh to encrypt and decrypt String objects
	 */
	private static String algorithm = "PBEWithMD5AndDES";// 128 bit key
	/**
	 * Secrect key to be used for encryption and decryption
	 */
	private static String secretKey = "ezeon8547";
	/**
	 * the iteration count
	 */
	int iterationCount = 19;// Iteration count

	/**
	 * 8-byte Salt
	 */
	byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35, (byte) 0xE3,
			(byte) 0x03 };

	Cipher ecipher;
	Cipher dcipher;

	/**
	 * Default constructor of the AESEncryption class
	 */
	public AESEncryption() {
		// default AESEncryption class constructor
	}

	/**
	 * Encrypt the passed plain text String object, and returns encrypted String
	 * object.
	 * 
	 * @param plainText
	 *            plain text to be encrypted
	 * @return encrypted String object
	 * @throws ResilientBrokerException
	 *             if encryption/decryption exception occures
	 */
	public String encrypt(String plainText) throws ResilientBrokerException {

		byte[] out = null;
		try {
			// Key generation for enc and desc
			KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
			SecretKey key = SecretKeyFactory.getInstance(algorithm).generateSecret(keySpec);
			// Prepare the parameter to the ciphers
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

			/**
			 * Encryption process
			 */
			ecipher = Cipher.getInstance(key.getAlgorithm());
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			byte[] in = plainText.getBytes(RBConstants.CHARSET_UTF8);
			out = ecipher.doFinal(in);
		} catch (Exception e) {
			logger.error("Exception occured in encrypt() function. Context: {}", e);
			throw new ResilientBrokerException(e.getMessage());
		}

		return Base64.encodeBase64String(out);
	}

	/**
	 * Decrypt the encrypted text String object, and returns plain text String
	 * object.
	 * 
	 * @param encryptedText
	 *            encrypted text to be decrypted
	 * @return decrypted plain text String object
	 * @throws ResilientBrokerException
	 *             if encryption/decryption exception occures
	 */
	public String decrypt(String encryptedText) throws ResilientBrokerException {
		byte[] utf8 = null;
		String decryptText = null;
		try {
			// Key generation for enc and desc
			KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
			SecretKey key = SecretKeyFactory.getInstance(algorithm).generateSecret(keySpec);
			// Prepare the parameter to the ciphers
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

			/**
			 * Decryption process; same key will be used for decr
			 */
			dcipher = Cipher.getInstance(key.getAlgorithm());
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			byte[] enc = Base64.decodeBase64(encryptedText);
			utf8 = dcipher.doFinal(enc);
			decryptText = new String(utf8, RBConstants.CHARSET_UTF8);
			//System.out.println("+++++++++++++++++++++ decryptText:" + decryptText);
		} catch (Exception e) {
			logger.error("Exception occured in decrypt() function. Context: {}", e);
			throw new ResilientBrokerException(e.getMessage());
		}
		return decryptText;
	}
}