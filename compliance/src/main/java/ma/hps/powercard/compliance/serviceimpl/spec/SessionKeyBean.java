package ma.hps.powercard.compliance.serviceimpl.spec;
import java.io.Serializable;

public class SessionKeyBean implements Serializable  
{
	private static final long serialVersionUID = 1L;
	
	private String token;
	private String publicKey;
	private Boolean alreadyLogged;
    
    public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public Boolean isAlreadyLogged() {
		return alreadyLogged;
	}
	public void setAlreadyLogged(Boolean alreadyLogged) {
		this.alreadyLogged = alreadyLogged;
	}
	
}
