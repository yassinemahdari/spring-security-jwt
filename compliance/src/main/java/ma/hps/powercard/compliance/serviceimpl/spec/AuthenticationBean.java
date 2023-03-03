package ma.hps.powercard.compliance.serviceimpl.spec;

import java.io.Serializable;
import java.util.Date;




public class AuthenticationBean implements Serializable  {

	private static final long serialVersionUID = 1L;
	private String token;
	private String login;
	private String pass;
    private String userLocale;
	private String defaultLocale;
	private Date loginDate;
    
    
    public String getToken() {
		return token;
	}
	public void setToken(String uuid) {
		this.token = uuid;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getUserLocale() {
		return userLocale;
	}
	public void setUserLocale(String userLocale) {
		this.userLocale = userLocale;
	}
	public String getDefaultLocale() {
		return defaultLocale;
	}
	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}
	public Date getLoginDate() {
		return loginDate;
	}
	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

}
