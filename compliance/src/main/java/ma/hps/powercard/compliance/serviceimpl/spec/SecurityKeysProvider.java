package ma.hps.powercard.compliance.serviceimpl.spec;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;


public class SecurityKeysProvider
{
	protected static final String ALGORITHM = "RSA";
	private   static final String ISSUER 	= "PowerCard_v3.5";
	
	public static void init()
	{
		Security.addProvider(new BouncyCastleProvider());
	}

	public static KeyPair generateKey() throws NoSuchAlgorithmException
	{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
		keyGen.initialize(1024);

		KeyPair key = keyGen.generateKeyPair();

		return key;
	}

	public static byte[] encrypt(byte[] text, PublicKey key) throws Exception
	{
		byte[] cipherText = null;
		try
		{
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			cipherText = cipher.doFinal(text);
		}
		catch (Exception e)
		{

			throw e;
		}
		return cipherText;
	}


	public static String encrypt(String text, PublicKey key) throws Exception
	{
		String encryptedText;
		try
		{
			byte[] cipherText = encrypt(text.getBytes("UTF8"),key);
			encryptedText = encodeBASE64(cipherText);
		}
		catch (Exception e)
		{           
			throw e;
		}
		return encryptedText;
	}


	public static String decrypt(byte[] text_byte, PrivateKey key) throws Exception
	{
		char[] dectyptedText_str = null;
		try
		{
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			dectyptedText_str = new String(cipher.doFinal(text_byte), "UTF8").toCharArray();
		}
		catch (Exception e)
		{
			throw e;
		}
		return new String(dectyptedText_str);

	}

	public static byte[] decryptPassword(byte[] text_byte, PrivateKey key) throws Exception
	{
		byte[] dectyptedPassword = null;

		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			dectyptedPassword = cipher.doFinal(text_byte);
		}
		catch (Exception e) {
			throw e;
		}
		return dectyptedPassword;

	}

	public static String getKeyAsString(Key key)
	{
		// Get the bytes of the key
		byte[] keyBytes = key.getEncoded();
		// Convert key to BASE64 encoded string
		Base64 b64 = new Base64();
		return b64.encode(keyBytes);
	}


	public static String encodeBASE64(byte[] bytes)
	{
		Base64 b64 = new Base64();
		return b64.encode(bytes);
	}


	public static byte[] decodeBASE64(String text) throws IOException
	{
		Base64 b64 = new Base64();
		return b64.decode(text);
	}


	public static byte[] copyBytes(byte[] arr, int length)
	{
		byte[] newArr = null;
		if (arr.length == length)
		{
			newArr = arr;
		}
		else
		{
			newArr = new byte[length];
			for (int i = 0; i < length; i++)
			{
				newArr[i] = (byte) arr[i];
			}
		}
		return newArr;
	}

	public static byte[] hexByte(final String encoded) {
		if ((encoded.length() % 2) != 0)
			throw new IllegalArgumentException("Input string must contain an even number of characters");

		final byte result[] = new byte[encoded.length()/2];
		final char enc[] = encoded.toCharArray();
		for (int i = 0; i < enc.length; i += 2) {
			StringBuilder curr = new StringBuilder(2);
			curr.append(enc[i]).append(enc[i + 1]);
			result[i/2] = (byte) Integer.parseInt(curr.toString(), 16);
		}
		return result;
	}

	public static String encryptPassword(String password,String salt) throws NoSuchAlgorithmException{

		String  passWithSalt = password+"{"+salt+"}" ;    	
		MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
		byte[] Hash =sha512.digest(passWithSalt.getBytes());
		return buffer_to_hex(Hash);

	}

	private static String buffer_to_hex(byte[] hash)
	{
		String d = "";
		for (int i = 0; i < hash.length; i++)
		{
			int v = hash[i] & 0xFF;
			if(v < 16)
				d += "0";
			d += Integer.toString(v, 16) + "";
		}
		return d;
	}

	//JWT
	public static Algorithm createJwtAlgorithm(KeyPair keyPair){
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
		return algorithm;
	}


	public static String createJwtToken(Algorithm algorithm, int jwt_time_expiration) throws JWTCreationException{
		long now = System.currentTimeMillis();
		return JWT.create().withIssuer(ISSUER).withExpiresAt(new Date(now + jwt_time_expiration)).sign(algorithm);
	}

	public static DecodedJWT verifyJwtToken(Algorithm algorithm, String token) throws JWTVerificationException{
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build(); 
		return verifier.verify(token);
	}

	public static String getClaimsFromJWT(String jwt, String claim_name) {
		return JWT.decode(jwt).getClaim(claim_name).asString();
	}
}
