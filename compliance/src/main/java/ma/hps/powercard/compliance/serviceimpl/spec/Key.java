package ma.hps.powercard.compliance.serviceimpl.spec;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.auth0.jwt.algorithms.Algorithm;


public class Key
{
	private RSAPublicKey  publicKey;
	private RSAPrivateKey privateKey;
	private Algorithm jwtAlgorithm; 
	
	public Key() {
	}
		
	public RSAPublicKey getPublicKey() {
		return publicKey;
	}
	
	public void setPublicKey(RSAPublicKey rsaPublicKey) {
		this.publicKey = rsaPublicKey;
	}
	
	public RSAPrivateKey getPrivateKey() {
		return privateKey;
	}
	
	public void setPrivateKey(RSAPrivateKey rsaPrivateKey) {
		this.privateKey = rsaPrivateKey;
	}

	public Algorithm getJwtAlgorithm() {
		return jwtAlgorithm;
	}

	public void setJwtAlgorithm(Algorithm jwtAlgorithm) {
		this.jwtAlgorithm = jwtAlgorithm;
	}
}