package ma.hps.powercard.compliance.serviceimpl.spec;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionSign {

	public static String COLD_SESSION = "+CS";

	public static SessionKeyBean getSessionKey(HttpServletRequest request) throws Exception 
	{
		int countFailedCnx = 0;
		SessionKeyBean sessionKey = new SessionKeyBean();
		RestServiceContextStore restServiceContext = new RestServiceContextStore();

		HttpSession oldsession = request.getSession(false);
		if (oldsession != null) {
			CustomSessionListener.remove(oldsession.getId() + COLD_SESSION, "old session");
			oldsession.invalidate();
		}
		HttpSession session = request.getSession(true);
		String token = session.getId() + COLD_SESSION;
		
		Key key = getSecurityKeys();
		restServiceContext.setKey(key);
		restServiceContext.setUuid(token);

		session.setAttribute("RestServiceContext", restServiceContext);
		sessionKey.setPublicKey( String.valueOf( SecurityKeysProvider.encodeBASE64(key.getPublicKey().getEncoded())) );
		sessionKey.setAlreadyLogged(false);
		sessionKey.setToken(token);
		session.setAttribute("countFailedCnx", countFailedCnx);
		
		CustomSessionListener.sessions.put(token, session);

		return sessionKey;
	}

	private static Key getSecurityKeys() throws NoSuchAlgorithmException{

		Key key = new Key();
		SecurityKeysProvider.init();
		KeyPair keyPair = SecurityKeysProvider.generateKey();

		key.setPublicKey ((RSAPublicKey)  keyPair.getPublic() );
		key.setPrivateKey((RSAPrivateKey) keyPair.getPrivate()); 

		key.setJwtAlgorithm(SecurityKeysProvider.createJwtAlgorithm(keyPair));

		return key;
	}

}