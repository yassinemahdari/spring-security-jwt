package ma.hps.powercard.dto;

public class AuthorizationCodeBean {

	public String authorization_code;
	public String state;

	public AuthorizationCodeBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AuthorizationCodeBean(String authorization_code, String state) {
		super();
		this.authorization_code = authorization_code;
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAuthorization_code() {
		return authorization_code;
	}

	public void setAuthorization_code(String authorization_code) {
		this.authorization_code = authorization_code;
	}

}
