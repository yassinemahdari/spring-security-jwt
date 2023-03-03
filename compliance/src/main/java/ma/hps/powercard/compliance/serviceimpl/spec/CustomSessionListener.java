package ma.hps.powercard.compliance.serviceimpl.spec;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.util.StringUtils;
import org.apache.log4j.Logger;

public class CustomSessionListener implements HttpSessionListener {

	private final static Logger logger = Logger.getLogger(CustomSessionListener.class);
	public static  Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();

	public void sessionCreated(HttpSessionEvent event) {
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		
		try {
			RestServiceContextStore restServiceContext=(RestServiceContextStore)(event.getSession().getAttribute("RestServiceContext"));
			if (restServiceContext != null) {
				sessions.remove(restServiceContext.getJwt());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (isCold(event)) {
			System.out.print("COLD "); // COLD SESSION DESTROYED
		}
		logger.info("SESSION DESTROYED");
	}

	public static HttpSession find(String sessionId) {
		return sessions.get(sessionId);
	}
	
	
	public static void remove(String token, String message) {
		
		if (!StringUtils.isEmpty(message)) {
			HttpSession session = sessions.get(token) != null ? sessions.get(token) : null ;
			if (session != null) {
				RestServiceContextStore restServiceContext = (RestServiceContextStore) (session.getAttribute("RestServiceContext"));
				if (restServiceContext.getServiceContext() != null && !StringUtils.isEmpty(restServiceContext.getServiceContext().getUserId())) {
					String userCode = restServiceContext.getServiceContext().getUserId();
					logger.info(String.format("REASON SESSION DESTROYED: %s [%s]", message, userCode));
				}
			}
		}

		sessions.remove(token);
	}
	
	boolean isCold(HttpSessionEvent event) {
		return sessions.containsKey(event.getSession().getId().concat("+CS"));
	}
	
}