package ma.hps.powercard.compliance.serviceimpl.spec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.ArrayList;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig; 
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.google.gson.JsonObject;

import ma.hps.powercard.compliance.serviceapi.Pwc_servicesService;
import ma.hps.powercard.compliance.serviceapi.Pwc_servicesVO;
import ma.hps.powercard.compliance.serviceapi.Pwc_screen_servicesService;
import ma.hps.powercard.compliance.serviceapi.Pwc_screen_servicesVO;
import ma.hps.powercard.compliance.serviceapi.Grant_permissionService;
import ma.hps.powercard.compliance.serviceapi.Grant_permissionVO;
import ma.hps.powercard.compliance.serviceapi.Assigned_roles2profilesService;
import ma.hps.powercard.compliance.serviceapi.Assigned_roles2profilesVO;

import ma.hps.powercard.constants.GlobalVars;
import ma.hps.powercard.compliance.serviceapi.Grants2profilesVO;
import ma.hps.powercard.compliance.serviceapi.GrantsVO;

public class CORSFilter implements Filter {
	
	private static final Logger logger = Logger.getLogger(CORSFilter.class);

	private static final String ACCESS_REFRESH = "Token is not valid. Please authenticate again!";
	private static final String ACCESS_FORBIDDEN = "Access forbidden!";
	private static final String SECURE_PREFIX = GlobalVars.SECURE_PREFIX;
	private static final String MATCHES_PATTERN = "Malicious parameter caught!";

	//will be injected in init call
	private Pwc_servicesService pwc_servicesService;
	private Pwc_screen_servicesService pwc_screen_servicesService;
	private Grant_permissionService grant_permissionService;
	private Assigned_roles2profilesService assigned_roles2profilesService;

	private static String[] SERVICE_ACCESS_WHITE_ROUTES = new String[] {
		// alerts
		"alert/Alert_msgService/searchAlert_msgService",
		"caseManagement/Queue_profileService/searchQueue_profileService",
		// maker/checker
		"compliance/McOperationParService/searchMcOperationParService",
		"compliance/McParDependencyService/searchMcParDependencyService",
		// application
		"compliance/TranslationServiceRest/translateStaticList",
		"compliance/TranslationServiceRest/translateBundles",
		"compliance/InterceptorUtilService/loadLightDependencies",
		"compliance/LoginService/retrieveScreenInfos",
		"compliance/ping"
	};

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Map<String, String[]> additionalParams = new HashMap<String, String[]>();

		String allowed_origins_property = System.getProperty("ALLOWED_ORIGINS");
		String[] allowed_origins = allowed_origins_property != null ? allowed_origins_property.split(",")
				: new String[0];

		HttpServletResponse res = (HttpServletResponse)response;
		HttpServletRequest  req  = (HttpServletRequest)request;

		String req_origin = req.getHeader("Origin");

		if (allowed_origins.length > 0 && !Arrays.asList(allowed_origins).contains("*")) {
			if (Arrays.asList(allowed_origins).contains(req_origin)) {
				res.addHeader("Access-Control-Allow-Origin", req_origin);
			} else {
				res.addHeader("Access-Control-Allow-Origin", allowed_origins[0]);
			}
		} else {
			res.addHeader("Access-Control-Allow-Origin", "*");
		}

		res.addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
      	res.addHeader("Content-Type", "application/json");

		 if (req.getMethod().equals("OPTIONS")) {
        	 String headers = req.getHeader("Access-Control-Request-Headers");
        	 if(headers != null) {
        		 res.addHeader("Access-Control-Allow-Headers", headers);        		 
        	 }
             chain.doFilter(request, response);
 			return;
        }

		if (req.getPathInfo().contains("compliance/getSessionKey") || req.getPathInfo().contains("compliance/login")
				|| req.getPathInfo().contains("compliance/logout") || req.getPathInfo().contains("compliance/ldapLoginStd")
				|| req.getPathInfo().contains("compliance/User_passwordsService/changePassword")
				|| req.getPathInfo().contains("compliance/healthcheck")
				|| req.getPathInfo().contains("oauth2/authorization")
				|| req.getPathInfo().contains("oauth2_login")
				|| req.getPathInfo().contains("forgotpassword")
				|| req.getPathInfo().contains("verify_otp")
				|| req.getPathInfo().contains("change_forgotten_password")) {

			if (!isParamValidJson(request)) {
				res.sendError(HttpServletResponse.SC_BAD_REQUEST, MATCHES_PATTERN);
				return;
			}

			try {
				chain.doFilter(req, res);
				return;
			} catch (Exception e) {
				logger.error("COULD NOT COMPLETE REQUEST: " + e.getMessage());
				logger.error(e);
				logger.debug(getRequestParamsStr(request));
				res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		}

	
		// get request headers to extract jwt token
		String authorization_str = req.getHeader(HttpHeaders.AUTHORIZATION);

		// block access if no authorization information is provided
		if (StringUtils.isEmpty(authorization_str)) {
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED, ACCESS_REFRESH);
			return;
		}
		
		String jwt = authorization_str.substring("Bearer ".length());

		if (StringUtils.isEmpty(jwt)) {
			// return HTTP 401 if jwt is not provider
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED, ACCESS_REFRESH);
			return;
		}

		HttpSession session = CustomSessionListener.sessions.get(jwt);

		if (session == null) {
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED, ACCESS_REFRESH);
			return;
		}

		RestServiceContextStore restServiceContext = (RestServiceContextStore) (session
				.getAttribute("RestServiceContext"));
		
		if (restServiceContext == null || restServiceContext.getServiceContext() == null) {
			JsonObject json = new JsonObject();
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null RestServiceContextStore is not allowed !");
			res.getWriter().write(json.toString());
            return;
		}
		
		/*
		String path = req.getRequestURI().substring(req.getContextPath().length());

		if (false == this.checkOperationGrant(path, restServiceContext)) {
			JsonObject json = new JsonObject();
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "5806");
            res.sendError(HttpServletResponse.SC_OK, json.toString());
		}*/
		
		// try to decode the jwt - deny access if no valid token provided
		try {
			SecurityKeysProvider.verifyJwtToken(restServiceContext.getKey().getJwtAlgorithm(), jwt);
		} catch (JWTVerificationException e) {
			// return HTTP 403 if jwt is not provider
			CustomSessionListener.remove(jwt, "EXPIRED JWT TOKEN");
			session.invalidate();
			res.sendError(HttpServletResponse.SC_FORBIDDEN, ACCESS_FORBIDDEN);
			return;
		}
	
		//Checking Service Access
		try {
			String path = req.getRequestURI().substring(req.getContextPath().length());
			if(!this.checkServiceAccess(restServiceContext, path.replace("/rest/", ""))) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN, ACCESS_FORBIDDEN);
	            return;
			}
		} catch(Exception e) {
			JsonObject json = new JsonObject();
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "5806");
            res.setHeader("Content-Type", "application/json");
			res.getWriter().write(json.toString());
			return;
		}

	// check SQL injection for the specific EndPoints  (Event rule definition)
		if(req.getPathInfo().contains("Frd_event_rule_def_valService/updateFrd_event_rule_def_valService") || req.getPathInfo().contains("Frd_event_rule_def_valService/createFrd_event_rule_def_valService")) {
			if(!isSqlInjectionSafe(request)) {
				
				logger.error("Invalid parameters (SQL injection)");
				CustomSessionListener.sessions.remove(jwt);
				session.invalidate();
				res.sendError(HttpServletResponse.SC_BAD_REQUEST, MATCHES_PATTERN);
				return;
			}
		}
		
		
		
		
		if (!isParamValidJson(request)) {
			CustomSessionListener.sessions.remove(jwt);
			session.invalidate();
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, MATCHES_PATTERN);
			return;
		}

		request.setAttribute("jwt", jwt);
		request.setAttribute("session", session);
		request.setAttribute("serviceContext", restServiceContext.getServiceContext());
		
		additionalParams.put("jwt", new String[] { jwt });
		
		RequestWrapper enhancedHttpRequest = new RequestWrapper((HttpServletRequest) request, additionalParams);
		
		chain.doFilter(enhancedHttpRequest, res);
	}

	private boolean isSqlInjectionSafe(ServletRequest request) {
		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			return SQLInjectionSafeConstraintValidator.isSqlInjectionSafe(request.getParameter(params.nextElement()));
		}
		return true;
	}

	private static boolean isRouteWhite(String path) {
		
		if (StringUtils.isEmpty(path))
			return true;

		return Stream.of(SERVICE_ACCESS_WHITE_ROUTES)
				.anyMatch(path::contains);
	}

	/**
	* 0 - cached service : allowed
	* 1 - configured for profile : allowed
	* 2 - checkeable : should verify
	*/
	private boolean checkServiceAccess(RestServiceContextStore ctx, String url) {
		
		boolean skipRoute = isRouteWhite(url);
		
		if (skipRoute)
			return true;

		try {

			String[] urlParts = url.split("/");
			if (urlParts != null && urlParts.length > 2) {
				
				String module = urlParts[0];
				String serviceName = urlParts[1];
				String service = urlParts[2];

				Pwc_servicesVO pwc_servicesVO = new Pwc_servicesVO();
				pwc_servicesVO.setCheckable("Y");
				pwc_servicesVO.setModule(module);
				pwc_servicesVO.setService(serviceName);
				pwc_servicesVO.setMethod(service);

				// check if service is declared in pwc_services and is checkable
				List<Pwc_servicesVO> pwc_services_list = this.pwc_servicesService
						.searchPwc_servicesService(ctx.getServiceContext(), pwc_servicesVO);

				if (pwc_services_list == null || pwc_services_list.isEmpty()) {
					return true;
				}

				else {

					String service_type = pwc_services_list.get(0).getType_service();
					List<Pwc_screen_servicesVO> pwc_screen_services_list = new ArrayList<Pwc_screen_servicesVO>();
					for (int i=0; i < pwc_services_list.size(); i++) {
						Long service_id = pwc_services_list.get(i).getPwc_services_id();
						Pwc_screen_servicesVO pwc_screen_servicesVO = new Pwc_screen_servicesVO();
						pwc_screen_servicesVO.setPwc_services_fk(service_id);
						pwc_screen_services_list.addAll(this.pwc_screen_servicesService
								.searchPwc_screen_servicesService(ctx.getServiceContext(), pwc_screen_servicesVO));
					}

					// list of screens calling the service
					List<String> screensList = pwc_screen_services_list.stream()
							.map(Pwc_screen_servicesVO::getScreen_fk).collect(Collectors.toList());
					// list of user grants
					List<Grant_permissionVO> grants = new ArrayList<Grant_permissionVO>();
					Assigned_roles2profilesVO assigned_roles2profileVO = new Assigned_roles2profilesVO();
					assigned_roles2profileVO.setProfiles_fk(ctx.getServiceContext().getProfileCode());
					List<Assigned_roles2profilesVO> assignedRoles2profiles = this.assigned_roles2profilesService
							.searchAssigned_roles2profilesService(ctx.getServiceContext(), assigned_roles2profileVO);
					// list of roles assigned to the user profile
					List<String> roles = assignedRoles2profiles.stream()
							.map(Assigned_roles2profilesVO::getAssigned_roles_fk).collect(Collectors.toList());

					List<Grant_permissionVO> grantsList = new ArrayList<Grant_permissionVO>();
					for (String screen : screensList) {
						grantsList.clear();
						Grant_permissionVO grant_permissionVO = new Grant_permissionVO();
						grant_permissionVO.setScreen_fk(screen);
						grantsList = this.grant_permissionService.searchGrant_permissionService(ctx.getServiceContext(),
								grant_permissionVO);
						grants.addAll(grantsList);
					}

					// if there is no grants => prohibit access
					if (grants.isEmpty()) {
						return false;
					}
					// if grants list is not empty
					else {
						for (String role : roles) {
							for (Grant_permissionVO grant : grants) {
								if (grant.getRole_fk().equals(role)) {
									// if the service is meant to read data => allow
									if (service_type.equals("R")) {
										return true;
									}
									//if the service is R/W and there is at least one role that give R/W access => allow
									if ((grant.getType_grant() == 3 || grant.getType_grant() == 4 || grant.getType_grant() == 5)) {
										return true;
									}
								}
							}
						}
						//if node of the conditions above => block the access
						return false;
					}
				}
			} else
				return true;

		} catch (Exception e) {// shouldIntercept?
			e.printStackTrace();
			return true;
		}
	}

	private static class ByteArrayServletStream extends ServletOutputStream {
		ByteArrayOutputStream baos;

		ByteArrayServletStream(ByteArrayOutputStream baos) {
			this.baos = baos;
		}

		public void write(int param) throws IOException {
			baos.write(param);
		}
	}

	private static class ByteArrayPrintWriter {

		private ByteArrayOutputStream baos = new ByteArrayOutputStream();

		private PrintWriter pw = new PrintWriter(baos);

		private ServletOutputStream sos = new ByteArrayServletStream(baos);

		public PrintWriter getWriter() {
			return pw;
		}

		public ServletOutputStream getStream() {
			return sos;
		}

		byte[] toByteArray() {
			return baos.toByteArray();
		}
	}

	/*
	boolean validateParameters(ServletRequest request) {
		Enumeration<String> params = request.getParameterNames();
		String paramName;
		while (params.hasMoreElements()) {
			paramName = params.nextElement();
			if (isMaliciousParameterValue(request.getParameter(paramName))) {
				return false;
			}
		}
		return true;
	}
	*/
	
	private static String getRequestParamsStr(ServletRequest request) {
		StringBuilder result = new StringBuilder();
		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String paramName = params.nextElement();
			result.append(paramName + "= " + request.getParameter(paramName) + "\n");
		}
		return result.toString();
	}

	boolean isMaliciousParameterValue(String value) {

		for (Pattern pattern : RequestWrapper.patterns) {
			Matcher match = pattern.matcher(value);
			if (match.matches()) {
				return true;
			}
		}
		return false;

	}

	private boolean isParamValidJson(ServletRequest request) {	
		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			return !isMaliciousParameterValue(request.getParameter(params.nextElement()));
		}
		return true;
	}
	
	private boolean checkOperationGrant(String url, RestServiceContextStore ctx) {

		String[] urlParts;
		String urlService, urlMethod, connectedProfile, profile;
		Long grant_id = null;
		List<GrantsVO> query = new ArrayList<GrantsVO>();
		List<Grants2profilesVO> g2pquery = new ArrayList<Grants2profilesVO>();
		GrantsVO grantsVO = new GrantsVO();
		Grants2profilesVO grants2profilesVO = new Grants2profilesVO();
		connectedProfile = ctx.getServiceContext().getProfileCode();

		try {
			urlParts = url.split("/");
			urlService = "/" + urlParts[0] + "/" + urlParts[1];
			urlMethod = "/" + urlParts[2];
			grantsVO.setMethod(urlMethod);
			grantsVO.setService(urlService);
			List<GrantsVO> grants = (List<GrantsVO>) ctx.getServiceContext().getProperty("pwcApis");
			if (grants.size() == 0) {
				return true;
			}
			query = grants.stream().filter(item -> item.getMethod().equals(grantsVO.getMethod())
					&& item.getService().equals(grantsVO.getService())).collect(Collectors.toList());
			if (query.size() > 0) {
				grant_id = query.get(0).getGrant_id();
			}
			grants2profilesVO.setGrants_fk(grant_id);
			List<Grants2profilesVO> g2p = (List<Grants2profilesVO>) ctx.getServiceContext()
					.getProperty("blackListedApis");

			g2pquery = g2p.stream().filter(item -> item.getGrants_fk().equals(grants2profilesVO.getGrants_fk())
					&& item.getProfile_fk().equals(connectedProfile)).collect(Collectors.toList());
			if (g2pquery.size() == 1) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}

	}
	
	/*
	private boolean isJsonFieldsValid(JsonElement jsonElement) {
		
		try {
			
			if (jsonElement.isJsonPrimitive()) {
				return !isMaliciousParameterValue(jsonElement.getAsString());
			}
			else if (jsonElement.isJsonArray()) {
				// loop over array and call isJsonFieldsValid(...)
				// return isJsonFieldsValid(...);
				for (JsonElement element: jsonElement.getAsJsonArray()) {
					//return isJsonFieldsValid(element);
					if (!isJsonFieldsValid(element)) {
						return false;
					}
				}
			}
			else if (jsonElement.isJsonObject()) {
				for (Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
					//System.out.println("Key = " + entry.getKey() + " Value = " + entry.getValue().getAsString());
					if (!isJsonFieldsValid(GsonHelper.getGson().fromJson(entry.getValue(), JsonElement.class))) {
						return false;
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	*/
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ApplicationContext ctx = WebApplicationContextUtils
			      .getRequiredWebApplicationContext(filterConfig.getServletContext());
	    this.pwc_servicesService = ctx.getBean(Pwc_servicesService.class);
	    this.pwc_screen_servicesService = ctx.getBean(Pwc_screen_servicesService.class);
		this.grant_permissionService = ctx.getBean(Grant_permissionService.class);
		this.assigned_roles2profilesService = ctx.getBean(Assigned_roles2profilesService.class);
	}

	@Override
	public void destroy() {
	}
}
