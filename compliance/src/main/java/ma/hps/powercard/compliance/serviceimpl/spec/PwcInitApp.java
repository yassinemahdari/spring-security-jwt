package ma.hps.powercard.compliance.serviceimpl.spec;
import javax.servlet.ServletContextListener;

import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ma.hps.powercard.compliance.serviceapi.Get_time_zoneOutVO;
import ma.hps.powercard.compliance.serviceapi.Pwc_time_zoneService;
import ma.hps.powercard.constants.GlobalVars;
import java.util.Optional;

import org.apache.log4j.Logger;
import javax.servlet.ServletContextEvent;

public class PwcInitApp  implements ServletContextListener {

	private final static Logger logger = Logger.getLogger(PwcInitApp.class);

	@Autowired
	private Pwc_time_zoneService timeZoneService;

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

		GlobalVars.timeZone = getTimezone();
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		/* Do Shutdown stuff. */
	}

	/**
	 * @return server's time zone from powercard_globals
	 */
	public Optional<String> getTimezone() {
		
		try {

			Get_time_zoneOutVO timeZoneVO = this.timeZoneService.get_time_zone(new ServiceContext());

			if (timeZoneVO.getP_time_zone() != null)
				logger.info("Database's time zone: " + timeZoneVO.getP_time_zone());

			return Optional.of(timeZoneVO.getP_time_zone());

		} catch (Exception e) {
			logger.error("Could not fetch database's time zone.");
			return Optional.empty();
		}
		
	}
	
}
