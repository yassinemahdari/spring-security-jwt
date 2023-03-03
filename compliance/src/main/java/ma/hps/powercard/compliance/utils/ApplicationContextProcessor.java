package ma.hps.powercard.compliance.utils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
public class ApplicationContextProcessor {
	
	@Autowired
	WebApplicationContext context;
	static WebApplicationContext registeredContext;
	
	@PostConstruct
	public void registerContext() {
		registeredContext = this.context;
	}
	
	public static WebApplicationContext getContext() {
		return registeredContext;
	}

}
