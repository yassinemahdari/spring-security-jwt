package ma.hps.powercard.compliance.serviceimpl.spec.monitoring;

import javax.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ma.hps.powercard.compliance.utils.GsonHelper;

import org.apache.log4j.Logger;

@RestController
@RequestMapping(value = "/compliance/healthcheck")
public class HealthCheckServiceRest {
	
    private static final Logger logger = Logger.getLogger(HealthCheckServiceRest.class);

    private static final String UI_URL_PARAMNAME = "ui_url";

	private HealthCheckService healthCheckService;
	private HttpServletRequest request;
	
	public HealthCheckServiceRest(HealthCheckService healthCheckService, HttpServletRequest request) {
		super();
		this.healthCheckService = healthCheckService;
		this.request = request;
	}

	@CrossOrigin
	@ResponseBody
	@GetMapping(produces = "application/json")
	public String version(@RequestParam(value=HealthCheckServiceRest.UI_URL_PARAMNAME, required=false) String uiUrl) {
		
		logger.info("PowerCardV3 : Operation:HealthCheck, RemoteAddress:" + request.getRemoteAddr());
		
		HealthCheckStatusDTO hcs = new HealthCheckStatusDTO();
		
		hcs.setDBUp(this.healthCheckService.isDBUp());
		
		boolean isUIUp = StringUtils.hasText(uiUrl)
			? this.healthCheckService.isUIUp(uiUrl)
			: false;

		hcs.setUIUp(isUIUp);
		
		hcs.setReport(prepareReport(uiUrl));
		
		return GsonHelper.getGson().toJson(hcs);
	}
	
	public static HealthCheckStatusDTO.Report prepareReport(String uiUrl) {
		HealthCheckStatusDTO.Report report = new HealthCheckStatusDTO.Report();
		report.uiUrl = StringUtils.hasText(uiUrl)
			? uiUrl
			: "UI URL was not set. pass the GET parameter: " + HealthCheckServiceRest.UI_URL_PARAMNAME;
		return report;
	}

}


class HealthCheckStatusDTO {

	boolean serverUp = true;
	boolean DBUp     = false;
	boolean UIUp     = false;
	Report report    = null;
	
	public static class Report {
		public String uiUrl;
	}

	public boolean isDBUp() {
		return DBUp;
	}

	public void setDBUp(boolean dBUp) {
		DBUp = dBUp;
	}

	public boolean isServerUp() {
		return serverUp;
	}

	public void setServerUp(boolean serverUp) {
		this.serverUp = serverUp;
	}

	public boolean isUIUp() {
		return UIUp;
	}

	public void setUIUp(boolean uIUp) {
		UIUp = uIUp;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}
	
}
