package ma.hps.powercard.compliance.serviceimpl.spec;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;
import org.fornax.cartridges.sculptor.framework.errorhandling.WebCustomAuthenticationDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.text.NumberFormat;

import ma.hps.exception.OurException;
import ma.hps.powercard.annotation.ApiMapping;
import ma.hps.powercard.compliance.domain.Pwc_tables;
import ma.hps.powercard.compliance.domain.UsersRepository;
import ma.hps.powercard.compliance.serviceapi.AuthentificationService;
import ma.hps.powercard.compliance.serviceapi.BankVO;
import ma.hps.powercard.compliance.serviceapi.Columns_filterVO;
import ma.hps.powercard.compliance.serviceapi.Data_columns_filterVO;
import ma.hps.powercard.compliance.serviceapi.Data_columns_filter_valuesService;
import ma.hps.powercard.compliance.serviceapi.Data_columns_filter_valuesVO;
import ma.hps.powercard.compliance.serviceapi.LoginService;
import ma.hps.powercard.compliance.serviceapi.LoginVO;
import ma.hps.powercard.compliance.serviceapi.MenuVO;
import ma.hps.powercard.compliance.serviceapi.Pcrd_flex_seq_tools_1Service;
import ma.hps.powercard.compliance.serviceapi.Powercard_globalsService;
import ma.hps.powercard.compliance.serviceapi.Powercard_globalsVO;
import ma.hps.powercard.compliance.serviceapi.Pwc_columnsVO;
import ma.hps.powercard.compliance.serviceapi.Get_seq_nextvalInVO;
import ma.hps.powercard.compliance.serviceapi.Get_seq_nextvalOutVO;
import ma.hps.powercard.compliance.serviceapi.Pwc_tablesService;
import ma.hps.powercard.compliance.serviceapi.Pwc_tablesVO;
import ma.hps.powercard.compliance.serviceapi.RoleVO;
import ma.hps.powercard.compliance.serviceapi.Switch_cut_offService;
import ma.hps.powercard.compliance.serviceapi.Switch_cut_offVO;
import ma.hps.powercard.compliance.serviceapi.User_contextService;
import ma.hps.powercard.compliance.serviceapi.User_contextVO;
import ma.hps.powercard.compliance.serviceapi.GrantsVO;
import ma.hps.powercard.compliance.serviceapi.GrantsService;
import ma.hps.powercard.compliance.serviceapi.Grants2profilesService;
import ma.hps.powercard.compliance.serviceapi.Grants2profilesVO;
import ma.hps.powercard.compliance.utils.GsonHelper;
import ma.hps.powercard.constants.GlobalVars;
import ma.hps.powercard.dto.ErrorResponse;
import ma.hps.powercard.dto.SuccessResponse;
import ma.hps.powercard.compliance.serviceapi.GrantsVO;
import ma.hps.powercard.compliance.serviceapi.GrantsService;
import ma.hps.powercard.compliance.serviceapi.Grants2profilesService;
import ma.hps.powercard.compliance.serviceapi.Grants2profilesVO;
import ma.hps.powercard.compliance.serviceapi.UsersVO;
import ma.hps.powercard.compliance.serviceapi.UsersService;

@RestController
@RequestMapping("/compliance")
public class PwcAuthentification {

	private @Autowired HttpServletRequest request;

	private static Logger logger = Logger.getLogger(PwcAuthentification.class);

	@Autowired
	private LoginService service;
	@Autowired
	private User_contextService userContextService;
	@Autowired
	private AuthentificationService authentificationService;
	@Autowired
	private Switch_cut_offService switch_cut_offService;
	@Autowired
	private Data_columns_filter_valuesService data_columns_filter_valuesService;
	@Autowired
	private Pwc_tablesService pwc_tablesService;
    @Autowired
	private GrantsService grantsService;
	@Autowired
	private Grants2profilesService grants2profilesService;
	@Autowired
	UsersRepository usersRepository;
	@Autowired
	Powercard_globalsService powercard_globalsService;
	@Autowired
	private UsersService usersService;
	@Autowired
	private Pcrd_flex_seq_tools_1Service pcrd_flex_seq_tools_1Service;
    
	private static final String PASSWORD_EXPIRED = "PASSWORD_EXPIRED";
	private static final String FIRST_LOGIN_CHANGE_PASS = GlobalVars.FIRST_LOGIN_CHANGE_PASS;
	private static final String LDAP_AUTHENTIFICATION = "LDAP_AUTHENTIFICATION";
	private static final String PASSWORD_NOTIFCATION_EXPIRATION = "PASSWORD_NOTIFCATION_EXPIRATION";
	private static final String HEADER_F5 = "Client-Cert";
	private static final String SESSION_TIMEOUT_PROPERTY = "SESSION_TIMEOUT";

	//private HttpSession session;
	//public static final Map<String, HttpSession> usersSessions = new HashMap<String, HttpSession>();

	public PwcAuthentification() {
	}

	@ApiMapping(value = "/getSessionKey")
	public String getSessionKey() {

		try {
			SessionKeyBean sessionKeyBean = SessionSign.getSessionKey(request);
			return SuccessResponse.from(sessionKeyBean).toJson();
		} catch (Exception e) {
			logger.error("Could not create session key.");
			return ErrorResponse.from("0039").toJson();
		}

	}
  
  	@ApiMapping(value = "/ping")
	public String ping() {
		return SuccessResponse.from("0000").toJson();
	}

	@ApiMapping(value = "/logout")
	public String logout(@RequestParam("jwt") String jwt) {

		HttpSession session = CustomSessionListener.sessions.get(jwt);

		if (session == null) {
			return ErrorResponse.from("0039").toJson();
		}

		try {
			CustomSessionListener.remove(jwt, "LOGOUT");
			session.invalidate();
			return SuccessResponse.from("0000").toJson();
		} catch (Exception e) {
			return ErrorResponse.from("0039").toJson();
		}

	}

	//	@PostMapping("/login")
//	@ResponseBody  
//	@RequestMapping(value="/login", method=RequestMethod.POST)
//	@ResponseBody
	@CrossOrigin
	@PostMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String login(@RequestParam("authBeanStr") String authBeanStr) {

		JsonObject json = new JsonObject();
		JsonSerializer<Date> serializer = new JsonSerializer<Date>() {
			public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				return src == null ? null : new JsonPrimitive(formatter.format(src));
			}
		};
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, serializer);
		Gson gson = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

		RestServiceContextStore restServiceContextToPost = new RestServiceContextStore();
		AuthenticationBean authBean = gson.fromJson(authBeanStr, AuthenticationBean.class);

		String token = authBean.getToken();
		String userLocale = authBean.getUserLocale();
		String defaultLocale = authBean.getDefaultLocale();
		String userId = authBean.getLogin();
		String applicationId = "PowerCardV3";
		String jwt = "";
		
		/********* Session *******/
		HttpSession _session = CustomSessionListener.sessions.get(token);

		if (_session == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "0039");
			return gson.toJson(json);
		}

		if (_session.getAttribute("RestServiceContext") == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "0039");
			return gson.toJson(json);
		}

		ServiceContextStore.set(null);
		RestServiceContextStore restServiceContext = (RestServiceContextStore) (_session
				.getAttribute("RestServiceContext"));

		restServiceContextToPost.setServiceContext(restServiceContext.getServiceContext());
		restServiceContextToPost.setUuid(restServiceContext.getUuid());

		restServiceContext.setServiceContext(new ServiceContext(userId, _session.getId(), applicationId));
		restServiceContext.getServiceContext().setIsValidated(true);

		WebCustomAuthenticationDetails details = new WebCustomAuthenticationDetails(_session.getId(), getIpAddress());
		restServiceContext.getServiceContext().setDetails(details);

		/************** Authentification Process ***********************/
		LoginVO loginVO = new LoginVO();
		try {
			loginVO = processAuthentification(authBean, restServiceContext, false, null, "", null);
		} catch (OurException e) {
			if(e.getMessage() == "0017") {
				_session.setAttribute("countFailedCnx", Integer.parseInt(_session.getAttribute("countFailedCnx").toString())+1);
				int maxFailedCnx = 3;
				if(System.getProperty("MAX_FAILED_CNX") != null) {
					maxFailedCnx = Integer.parseInt(System.getProperty("MAX_FAILED_CNX"));			
				}
				if(Integer.parseInt(_session.getAttribute("countFailedCnx").toString()) > maxFailedCnx) {
					
					_session.invalidate();
					
						json.addProperty("EXCEPTION", true);
						json.addProperty("RESULT", "0039");
						return gson.toJson(json);
					
				}
				
			}
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());
			return gson.toJson(json);
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());
		}

		// if a user has an old session disconnect it
		try {
			disconnectOldSession(userId);
		} catch (Exception e) {
			logger.error("An error was triggered when server tries to disconnect the old session " + e.getMessage());
		}

		if (loginVO.getStatus().equals("X")) {
			restServiceContextToPost.setMessage(PASSWORD_EXPIRED);
			restServiceContext.getServiceContext().setProperty("passConfig", loginVO.getPassConfigs());
			restServiceContextToPost.setServiceContext(restServiceContext.getServiceContext());
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", gson.toJson(restServiceContextToPost));
			return gson.toJson(json);
		}
		if (loginVO.getStatus().equals("F")) {
			restServiceContextToPost.setMessage(FIRST_LOGIN_CHANGE_PASS);
			restServiceContext.getServiceContext().setProperty("passConfig", loginVO.getPassConfigs());
			restServiceContextToPost.setServiceContext(restServiceContext.getServiceContext());
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", gson.toJson(restServiceContextToPost));
			return gson.toJson(json);
		}

		/********* Set User context *******/
		try {
			buildServiceContext(loginVO, userLocale, defaultLocale, restServiceContext);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		}

		/************ MakerChecker ****************/
		// Long haveOpToCheck =
		// service.haveOperationToCheck(restServiceContext.getServiceContext()); TO
		// REFACTOR
		// loginVO.setHaveOperationToCheck(haveOpToCheck); TO REFACTOR
		_session.setAttribute("loginVO", loginVO);
		_session.setAttribute("status", "N");

		_session.setMaxInactiveInterval(getSessionTimeoutDuration());

		// Set jwt_time_expiration
		int jwt_time_expiration = 7200 * 1000;
		if (loginVO.getTimer_pwc_disconnection() != null) {
			jwt_time_expiration = Integer.parseInt(loginVO.getTimer_pwc_disconnection()) * 1000;
		}
		// Create JWT Token
		try {
			jwt = SecurityKeysProvider.createJwtToken(restServiceContext.getKey().getJwtAlgorithm(),
					jwt_time_expiration);
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "0039");
			logger.error(e.getMessage());
			return gson.toJson(json);
		}

		restServiceContext.setJwt(jwt);
		CustomSessionListener.sessions.remove(token);// remove the old session
		CustomSessionListener.sessions.put(jwt, _session);// Create a new one
		
		restServiceContextToPost = new RestServiceContextStore();
		try {
			buildServiceContextToPost(restServiceContextToPost, restServiceContext, loginVO);
		} catch (Exception e) {
			logger.error("Error building ServiceContextToPost: " + e.getMessage());
		}

		if ("L".equals(loginVO.getConnection_status()))// connection through LDAP
		{
			restServiceContextToPost.setMessage(LDAP_AUTHENTIFICATION);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", gson.toJson(restServiceContextToPost));
			return gson.toJson(json);
		}

		try {
			List<User_contextVO> user_contextList = getUserContextService(restServiceContext.getServiceContext());
			if (user_contextList != null && !user_contextList.isEmpty()) {
				for (User_contextVO user_contextVO : user_contextList) {
					if (user_contextVO.getContext_key().equals("language")
							&& user_contextVO.getContext_value() != null) {
						restServiceContext.getServiceContext().setProperty("userLocale",
								user_contextVO.getContext_value());
						break;
					}
				}
			}
			user_contextList.addAll(getBookmarksService(restServiceContext.getServiceContext()));
			restServiceContextToPost.setUserContexts(user_contextList);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		try {
			List<Switch_cut_offVO> switch_cut_offList = switch_cut_offService
					.getAllSwitch_cut_offService(restServiceContext.getServiceContext());
			restServiceContextToPost.setBusinessDate(switch_cut_offList.get(0).getLast_cutoff_date_bo());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		// search for data access of bank type
		List<String> liste = new ArrayList<String>();

		try {
			if(loginVO.getBankDataAccess() != null) {
				List<BankVO> listeofBanks = GsonHelper.getGson().fromJson(loginVO.getBankDataAccess(),
						new TypeToken<List<BankVO>>() {
						}.getType());
				listeofBanks.forEach(v -> {
					logger.trace(v);
					liste.add(v.getBank_code());

				});
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		restServiceContext.getServiceContext().setProperty("bankDataAccess", liste);
		restServiceContextToPost.setIsLdapLogged(false);
		restServiceContextToPost.setBankDataAccess(liste);
		restServiceContextToPost.setServerTimeZone(GlobalVars.timeZone.orElse(null));
		
		Powercard_globalsVO powercard_globalsVO = new Powercard_globalsVO();
		powercard_globalsVO.setVariable_name("ENABLE_DEFAULT_MASK");
		List<Powercard_globalsVO> searchResult = new ArrayList<Powercard_globalsVO>();
		try {
			searchResult = powercard_globalsService.searchPowercard_globalsService(restServiceContext.getServiceContext(), powercard_globalsVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(searchResult.size() > 0) {
			restServiceContext.getServiceContext().setProperty("enable_default_mask", searchResult.get(0).getVariable_value());
		}
		
		json.addProperty("EXCEPTION", false);
		String returnStr = gson.toJson(restServiceContextToPost);
		json.addProperty("RESULT", returnStr);

		// only keeping bank_code in ctx for vb_banks
		List<String> ctx_vb = ((List<JsonObject>)restServiceContext.getServiceContext().getProperty("vb_banks")).stream().map(b->b.get("bank_code").getAsString()).collect(Collectors.toList());
		restServiceContext.getServiceContext().setProperty("vb_banks", ctx_vb);
		return gson.toJson(json);
	}
	
	private void buildServiceContextToPost(RestServiceContextStore restServiceContextToPost, RestServiceContextStore restServiceContext, LoginVO loginVO) {
		// post only informations needed in UI
		ServiceContext ctx = new ServiceContext(restServiceContext.getServiceContext().getUserId(), null, null);
		ctx.setUsername(restServiceContext.getServiceContext().getUsername());
		ctx.setProfileCode(restServiceContext.getServiceContext().getProfileCode());
		ctx.setAuthentication(restServiceContext.getServiceContext().getAuthentication());
		ctx.setProperty("sysDate", restServiceContext.getServiceContext().getProperty("sysDate"));
		ctx.setProperty("defaultBank", restServiceContext.getServiceContext().getProperty("defaultBank"));
		ctx.setProperty("userLocale", restServiceContext.getServiceContext().getProperty("userLocale"));
		ctx.setProperty("listOfRoles", restServiceContext.getServiceContext().getProperty("listOfRoles"));
		// Get information admin from collection roles
		List<RoleVO> listOfRolesAdmin = getRolesAdmin(loginVO.getRole_col());
		ctx.setProperty("listOfRolesAdmin", (Serializable) listOfRolesAdmin);
		ctx.setProperty("pan_visualization", restServiceContext.getServiceContext().getProperty("pan_visualization"));
		ctx.setProperty("vb_banks", restServiceContext.getServiceContext().getProperty("vb_banks"));
		ctx.setProperty("lastLogin", restServiceContext.getServiceContext().getProperty("lastLogin"));
		ctx.setProperty("statusLogin", loginVO.getStatus());
		ctx.setProperty("password_expiration_date", loginVO.getPassword_expiration_date());
		ctx.setProperty("isAdmin", loginVO.getIsProfileAdmin());
		restServiceContextToPost.setServiceContext(ctx);
		restServiceContextToPost.setJwt(restServiceContext.getJwt());
		restServiceContextToPost.setUuid(restServiceContext.getUuid());
		restServiceContextToPost.setMenus(restServiceContext.getMenus());
		restServiceContextToPost.setMenuGroupBymodulesAndWorkspaces(getMenuGroupBymodulesAndWorkspaces(restServiceContext.getMenus()));
		restServiceContextToPost.setModulesByWorkspaces(getModuleGroupByWorkspace(restServiceContext.getMenus()));
	}

	public String getSequenceValue(ServiceContext ctx, String sequence_name, String pading_format) throws Exception {
		Get_seq_nextvalInVO get_seq_nextvalInVO = new Get_seq_nextvalInVO();
		get_seq_nextvalInVO.setP_sequence_name(sequence_name);
		Get_seq_nextvalOutVO get_seq_nextvalOutVO = pcrd_flex_seq_tools_1Service.get_seq_nextval(ctx,
				get_seq_nextvalInVO);
		if (get_seq_nextvalOutVO != null) {
			if (pading_format == null)
				return get_seq_nextvalOutVO.getP_sequence_nextval();

			String numberFormated;
			NumberFormat formatter = new DecimalFormat(pading_format);
			numberFormated = formatter.format(Long.parseLong(get_seq_nextvalOutVO.getP_sequence_nextval()));

			return numberFormated;
		}
		return null;
	}
	
	@CrossOrigin
	@RequestMapping(value="/ldapLogin", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
	public String ldapLogin(@RequestParam("authBeanStr") String authBeanStr, @RequestHeader(HEADER_F5) String clientCert,@RequestParam("silo") String silo) throws Exception{

		JsonObject json = new JsonObject();
		JsonSerializer<Date> serializer = new JsonSerializer<Date>() {
			public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				return src == null ? null : new JsonPrimitive(formatter.format(src));
			}
		};
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, serializer);
		Gson gson = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

		RestServiceContextStore restServiceContextToPost = new RestServiceContextStore();
		AuthenticationBean authBean = gson.fromJson(authBeanStr, AuthenticationBean.class);
		if (clientCert == null || !clientCert.contains(":")) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "0101");
			return gson.toJson(json);
		}
		/** clientCert format : client-cert:mail */
		String userMail = clientCert.split(":")[1];
		authBean.setLogin(userMail);
		String token = authBean.getToken();
		String userLocale = authBean.getUserLocale();
		String defaultLocale = authBean.getDefaultLocale();
		String applicationId = "PowerCardV3";
		String jwt = "";

		/********* Session *******/
		HttpSession _session = CustomSessionListener.sessions.get(token);

		if (_session == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "0039");
			return gson.toJson(json);
		}

		if (_session.getAttribute("RestServiceContext") == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "0039");
			return gson.toJson(json);
		}

		// if a user has an old session disconnect it
		try {
			disconnectOldSession(userMail);
		} catch (Exception e) {
			logger.error("An error was triggered when server tries to disconnect the old session " + e.getMessage());
		}

		ServiceContextStore.set(null);
		RestServiceContextStore restServiceContext = (RestServiceContextStore) (_session
				.getAttribute("RestServiceContext"));

		/** build temporary serviceContext for transaction (will be overridden ) */
		restServiceContext.setServiceContext(new ServiceContext(null, _session.getId(), applicationId));
		restServiceContext.getServiceContext().setIsValidated(true);
		restServiceContextToPost.setServiceContext(restServiceContext.getServiceContext());
		restServiceContextToPost.setUuid(restServiceContext.getUuid());

		UsersVO usersVO = new UsersVO();
		usersVO.setMail(userMail);
		
		List<UsersVO> usersList = null;
		try {
			usersList = usersService.searchUsersService(restServiceContext.getServiceContext(), usersVO);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		String user_code;
		if (usersList.size() >0) {
			user_code = usersList.get(0).getUser_code();
		} else {
			String sequence = this.getSequenceValue(restServiceContext.getServiceContext(), "Users_x", null);
			int sequenceLength = sequence.length();
			String splitted = userMail.split("@")[0];
			user_code = new String(sequence + splitted).length() < 15 ? sequence + splitted : sequence + splitted.substring(0, 15 - sequenceLength);
		}

		restServiceContext.setServiceContext(new ServiceContext(user_code, _session.getId(), applicationId));
		restServiceContext.getServiceContext().setIsValidated(true);

		WebCustomAuthenticationDetails details = new WebCustomAuthenticationDetails(_session.getId(), getIpAddress());
		restServiceContext.getServiceContext().setDetails(details);

		/************** Authentification Process ***********************/
		LoginVO loginVO = new LoginVO();
		try {
			loginVO = processAuthentification(authBean, restServiceContext, true, clientCert.split(":")[1], silo,
					user_code);
		} catch (OurException e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());
			return gson.toJson(json);
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());
		}

		if (loginVO.getStatus().equals("X")) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "0040");
			return gson.toJson(json);
		}
		if (loginVO.getStatus().equals("F")) {
			restServiceContextToPost.setMessage(FIRST_LOGIN_CHANGE_PASS);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", gson.toJson(restServiceContextToPost));
			return gson.toJson(json);
		} else if (loginVO.getStatus().equals("W")) {
			restServiceContextToPost.setMessage(PASSWORD_NOTIFCATION_EXPIRATION);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", gson.toJson(restServiceContextToPost));
			return gson.toJson(json);
		}

		/********* Set User context *******/
		try {
			buildServiceContext(loginVO, userLocale, defaultLocale, restServiceContext);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		}

		/************ MakerChecker ****************/
		// Long haveOpToCheck =
		// service.haveOperationToCheck(restServiceContext.getServiceContext()); TO
		// REFACTOR
		// loginVO.setHaveOperationToCheck(haveOpToCheck); TO REFACTOR
		_session.setAttribute("loginVO", loginVO);
		_session.setAttribute("status", "N");

		_session.setMaxInactiveInterval(getSessionTimeoutDuration());

		// Set jwt_time_expiration
		int jwt_time_expiration = 7200 * 1000;
		if (loginVO.getTimer_pwc_disconnection() != null) {
			jwt_time_expiration = Integer.parseInt(loginVO.getTimer_pwc_disconnection()) * 1000;
		}
		// Create JWT Token
		try {
			jwt = SecurityKeysProvider.createJwtToken(restServiceContext.getKey().getJwtAlgorithm(),
					jwt_time_expiration);
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "0039");
			logger.error(e.getMessage());
			return gson.toJson(json);
		}

		restServiceContext.setJwt(jwt);
		CustomSessionListener.sessions.remove(token);// remove the old session
		CustomSessionListener.sessions.put(jwt, _session);// Create a new one

		restServiceContextToPost = new RestServiceContextStore(jwt, null, restServiceContext.getServiceContext(),
				restServiceContext.getUuid(), restServiceContext.getMenus());
		restServiceContextToPost
				.setMenuGroupBymodulesAndWorkspaces(getMenuGroupBymodulesAndWorkspaces(restServiceContext.getMenus()));
		restServiceContextToPost.setModulesByWorkspaces(getModuleGroupByWorkspace(restServiceContext.getMenus()));

		if ("L".equals(loginVO.getConnection_status()))// connection through LDAP
		{
			restServiceContextToPost.setMessage(LDAP_AUTHENTIFICATION);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", gson.toJson(restServiceContextToPost));
			return gson.toJson(json);
		}

		try {
			List<User_contextVO> user_contextList = getUserContextService(restServiceContext.getServiceContext());
			if (user_contextList != null && !user_contextList.isEmpty()) {
				for (User_contextVO user_contextVO : user_contextList) {
					if (user_contextVO.getContext_key().equals("language")
							&& user_contextVO.getContext_value() != null) {
						restServiceContext.getServiceContext().setProperty("userLocale",
								user_contextVO.getContext_value());
						break;
					}
				}
			}
			user_contextList.addAll(getBookmarksService(restServiceContext.getServiceContext()));
			restServiceContextToPost.setUserContexts(user_contextList);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		try {
			List<Switch_cut_offVO> switch_cut_offList = switch_cut_offService
					.getAllSwitch_cut_offService(restServiceContext.getServiceContext());
			restServiceContextToPost.setBusinessDate(switch_cut_offList.get(0).getLast_cutoff_date_bo());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		// search for data access of bank type
		List<String> liste = new ArrayList<String>();

		try {
			List<BankVO> listeofBanks = GsonHelper.getGson().fromJson(loginVO.getBankDataAccess(),
					new TypeToken<List<BankVO>>() {
					}.getType());
			listeofBanks.forEach(v -> {
				logger.trace(v);
				liste.add(v.getBank_code());

			});

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		restServiceContext.getServiceContext().setProperty("bankDataAccess", liste);

		restServiceContextToPost.setBankDataAccess(liste);
		restServiceContextToPost.setIsLdapLogged(true);
		restServiceContextToPost.setServerTimeZone(GlobalVars.timeZone.orElse(null));

		//keeping only bank_code in ctx for vb_banks
		List<String> ctx_vb = ((List<JsonObject>)restServiceContext.getServiceContext().getProperty("vb_banks")).stream().map(b->b.get("bank_code").getAsString()).collect(Collectors.toList());
		restServiceContext.getServiceContext().setProperty("vb_banks", ctx_vb);

		json.addProperty("EXCEPTION", false);
		json.addProperty("RESULT", gson.toJson(restServiceContextToPost));
		return gson.toJson(json);
	}

	@CrossOrigin
	@RequestMapping(value="/ldapLoginStd", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
	public String ldapLoginStd(@RequestParam("authBeanStr") String authBeanStr) throws Exception{
	
		JsonObject json = new JsonObject();
		JsonSerializer<Date> serializer = new JsonSerializer<Date>() {
			public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				return src == null ? null : new JsonPrimitive(formatter.format(src));
			}
		};
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, serializer);
		Gson gson = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

		RestServiceContextStore restServiceContextToPost = new RestServiceContextStore();
		AuthenticationBean authBean = gson.fromJson(authBeanStr, AuthenticationBean.class);

		//login is the mail
		String userMail = authBean.getLogin();
		authBean.setLogin(userMail);
		String token = authBean.getToken();
		String userLocale = authBean.getUserLocale();
		String defaultLocale = authBean.getDefaultLocale();
		String applicationId = "PowerCardV3";
		String jwt = "";

		/********* Session *******/
		HttpSession _session = CustomSessionListener.sessions.get(token);

		if (_session == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "0039");
			return gson.toJson(json);
		}

		if (_session.getAttribute("RestServiceContext") == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "0039");
			return gson.toJson(json);
		}

		// if a user has an old session disconnect it
		try {
			disconnectOldSession(userMail);
		} catch (Exception e) {
			logger.error("An error was triggered when server tries to disconnect the old session " + e.getMessage());
		}

		ServiceContextStore.set(null);
		RestServiceContextStore restServiceContext = (RestServiceContextStore) (_session
				.getAttribute("RestServiceContext"));

		/** build temporary serviceContext for transaction (will be overridden ) */
		restServiceContext.setServiceContext(new ServiceContext(null, _session.getId(), applicationId));
		restServiceContext.getServiceContext().setIsValidated(true);
		restServiceContextToPost.setServiceContext(restServiceContext.getServiceContext());
		restServiceContextToPost.setUuid(restServiceContext.getUuid());

		UsersVO usersVO = new UsersVO();
		usersVO.setMail(userMail);
		
		List<UsersVO> usersList = null;
		try {
			usersList = usersService.searchUsersService(restServiceContext.getServiceContext(), usersVO);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		String user_code;
		if (usersList.size() >0) {
			user_code = usersList.get(0).getUser_code();
		} else {
			String sequence = this.getSequenceValue(restServiceContext.getServiceContext(), "Users_x", null);
			int sequenceLength = sequence.length();
			String splitted = userMail.split("@")[0];
			user_code = new String(sequence + splitted).length() < 15 ? sequence + splitted : sequence + splitted.substring(0, 15 - sequenceLength);
		}

		restServiceContext.setServiceContext(new ServiceContext(user_code, _session.getId(), applicationId));
		restServiceContext.getServiceContext().setIsValidated(true);

		WebCustomAuthenticationDetails details = new WebCustomAuthenticationDetails(_session.getId(), getIpAddress());
		restServiceContext.getServiceContext().setDetails(details);

		/************** Authentification Process ***********************/
		LoginVO loginVO = new LoginVO();
		try {
			loginVO = processAuthentification(authBean, restServiceContext, true, userMail, null, user_code);
		} catch (OurException e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());
			return gson.toJson(json);
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());
		}

		if (loginVO.getStatus().equals("X")) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "0040");
			return gson.toJson(json);
		}
		if (loginVO.getStatus().equals("F")) {
			restServiceContextToPost.setMessage(FIRST_LOGIN_CHANGE_PASS);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", gson.toJson(restServiceContextToPost));
			return gson.toJson(json);
		} else if (loginVO.getStatus().equals("W")) {
			restServiceContextToPost.setMessage(PASSWORD_NOTIFCATION_EXPIRATION);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", gson.toJson(restServiceContextToPost));
			return gson.toJson(json);
		}

		/********* Set User context *******/
		try {
			buildServiceContext(loginVO, userLocale, defaultLocale, restServiceContext);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		}

		/************ MakerChecker ****************/
		// Long haveOpToCheck =
		// service.haveOperationToCheck(restServiceContext.getServiceContext()); TO
		// REFACTOR
		// loginVO.setHaveOperationToCheck(haveOpToCheck); TO REFACTOR
		_session.setAttribute("loginVO", loginVO);
		_session.setAttribute("status", "N");

		_session.setMaxInactiveInterval(getSessionTimeoutDuration());

		// Set jwt_time_expiration
		int jwt_time_expiration = 7200 * 1000;
		if (loginVO.getTimer_pwc_disconnection() != null) {
			jwt_time_expiration = Integer.parseInt(loginVO.getTimer_pwc_disconnection()) * 1000;
		}
		// Create JWT Token
		try {
			jwt = SecurityKeysProvider.createJwtToken(restServiceContext.getKey().getJwtAlgorithm(),
					jwt_time_expiration);
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "0039");
			logger.error(e.getMessage());
			return gson.toJson(json);
		}

		restServiceContext.setJwt(jwt);
		CustomSessionListener.sessions.remove(token);// remove the old session
		CustomSessionListener.sessions.put(jwt, copySessionData(_session));// Create a new one

		restServiceContextToPost = new RestServiceContextStore(jwt, null, restServiceContext.getServiceContext(),
				restServiceContext.getUuid(), restServiceContext.getMenus());
		restServiceContextToPost
				.setMenuGroupBymodulesAndWorkspaces(getMenuGroupBymodulesAndWorkspaces(restServiceContext.getMenus()));
		restServiceContextToPost.setModulesByWorkspaces(getModuleGroupByWorkspace(restServiceContext.getMenus()));

		if ("L".equals(loginVO.getConnection_status()))// connection through LDAP
		{
			restServiceContextToPost.setMessage(LDAP_AUTHENTIFICATION);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", gson.toJson(restServiceContextToPost));
			return gson.toJson(json);
		}

		try {
			List<User_contextVO> user_contextList = getUserContextService(restServiceContext.getServiceContext());
			if (user_contextList != null && !user_contextList.isEmpty()) {
				for (User_contextVO user_contextVO : user_contextList) {
					if (user_contextVO.getContext_key().equals("language")
							&& user_contextVO.getContext_value() != null) {
						restServiceContext.getServiceContext().setProperty("userLocale",
								user_contextVO.getContext_value());
						break;
					}
				}
			}
			user_contextList.addAll(getBookmarksService(restServiceContext.getServiceContext()));
			restServiceContextToPost.setUserContexts(user_contextList);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		try {
			List<Switch_cut_offVO> switch_cut_offList = switch_cut_offService
					.getAllSwitch_cut_offService(restServiceContext.getServiceContext());
			restServiceContextToPost.setBusinessDate(switch_cut_offList.get(0).getLast_cutoff_date_bo());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		// search for data access of bank type
		List<String> liste = new ArrayList<String>();

		try {
			List<BankVO> listeofBanks = GsonHelper.getGson().fromJson(loginVO.getBankDataAccess(),
					new TypeToken<List<BankVO>>() {
					}.getType());
			listeofBanks.forEach(v -> {
				logger.trace(v);
				liste.add(v.getBank_code());

			});

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		restServiceContext.getServiceContext().setProperty("bankDataAccess", liste);

		restServiceContextToPost.setBankDataAccess(liste);
		restServiceContextToPost.setIsLdapLogged(true);

		//keeping only bank_code in ctx for vb_banks
		List<String> ctx_vb = ((List<JsonObject>)restServiceContext.getServiceContext().getProperty("vb_banks")).stream().map(b->b.get("bank_code").getAsString()).collect(Collectors.toList());
		restServiceContext.getServiceContext().setProperty("vb_banks", ctx_vb);

		json.addProperty("EXCEPTION", false);
		json.addProperty("RESULT", gson.toJson(restServiceContextToPost));
		return gson.toJson(json);
	}
	private List<String> getBankDataAccess(RestServiceContextStore restServiceContextToPost) {

		Collection<Data_columns_filterVO> liste = restServiceContextToPost.getServiceContext()
				.getDataColumnsFilterList();
		List<String> listeofBanks = new ArrayList<String>();

		for (Data_columns_filterVO data_columns_filterVO : liste) {

			Columns_filterVO filter = data_columns_filterVO.getRef_columns_filter();
			if (filter != null) {
				Pwc_columnsVO pwc_columnsVO = filter.getRef_pwc_columns();
				if (pwc_columnsVO != null) {
					Long pwc_tables_fk = pwc_columnsVO.getPwc_tables_fk();

					try {
						Pwc_tables pwc_tables = pwc_tablesService.findById(restServiceContextToPost.getServiceContext(),
								pwc_tables_fk);

						if (pwc_tables != null && "Bank".equals(pwc_tables.getTable_name())
								&& "bank_code".equals(pwc_columnsVO.getColumn_name())) {

							Collection<Data_columns_filter_valuesVO> filter_valueList = data_columns_filterVO
									.getData_columns_filter_values_col();
							for (Data_columns_filter_valuesVO data_columns_filter_valuesVO : filter_valueList) {
								listeofBanks.add(data_columns_filter_valuesVO.getVal());
							}

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

		return listeofBanks;

	}

	private HashMap<String, Collection<Module>> getMenuGroupBymodulesAndWorkspaces(MenuVO[] menus) {

		HashMap<String, Collection<Module>> modulesByWorkspaces = new HashMap<String, Collection<Module>>();
		Collection<String> workspaces = getWorkspaces(menus);
		Collection<String> modules = getModules(menus);
		Collection<Module> moduleList = new ArrayList<Module>();
		Collection<MenuVO> menusByModule = new ArrayList<MenuVO>();

		Module module = new Module();

		if (workspaces.size() == 0)
			return modulesByWorkspaces;

		for (String workspace : workspaces) {
			moduleList = new ArrayList<Module>();
			for (String moduleName : modules) {

				menusByModule = new ArrayList<MenuVO>();

				for (MenuVO menuVO : menus) {
					try {
						if (workspace.equals(menuVO.getWorkspace()) && moduleName.equals(menuVO.getModule())) {
							menusByModule.add(menuVO);
						}
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
				if (menusByModule.size() == 0)
					continue;

				module = new Module(moduleName, menusByModule);
				moduleList.add(module);
			}
			modulesByWorkspaces.put(workspace, moduleList);
		}
		return modulesByWorkspaces;
	}

	private HashMap<String, Collection<String>> getModuleGroupByWorkspace(MenuVO[] menus) {

		HashMap<String, Collection<String>> moduleGroupeByWorkspace = new HashMap<String, Collection<String>>();
		Collection<String> workspaces = getWorkspaces(menus);
		Collection<String> modulesTmp = new HashSet<String>();

		if (workspaces.size() == 0)
			return moduleGroupeByWorkspace;

		for (String workspace : workspaces) {
			for (MenuVO menuVO : menus) {

				try {
					if (workspace.equals(menuVO.getWorkspace())) {
						modulesTmp.add(menuVO.getModule());
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}

			List<String> sortedListOfModules = new ArrayList<String>(modulesTmp);
			Collections.sort(sortedListOfModules);
			moduleGroupeByWorkspace.put(workspace, sortedListOfModules);
			modulesTmp.clear();
		}

		return moduleGroupeByWorkspace;
	}

	private Collection<String> getWorkspaces(MenuVO[] menus) {
		Collection<String> workspaces = new HashSet<String>();
		for (MenuVO menuVO : menus) {
			workspaces.add(menuVO.getWorkspace());
		}
		return workspaces;
	}

	private Collection<String> getModules(MenuVO[] menus) {
		Collection<String> modules = new HashSet<String>();
		for (MenuVO menuVO : menus) {
			modules.add(menuVO.getModule());
		}
		return modules;
	}

	// Get all user contexts for the logged user
	private List<User_contextVO> getUserContextService(ServiceContext serviceContext) throws Exception {
		User_contextVO userContextVO = new User_contextVO();
		userContextVO.setUser_id(serviceContext.getUser_id());
		return this.userContextService.searchUser_contextService(serviceContext, userContextVO);
	}

	// Get all user contexts for the logged user
	private List<User_contextVO> getBookmarksService(ServiceContext serviceContext) throws Exception {
		User_contextVO userContextVO = new User_contextVO();
		userContextVO.setUser_id(serviceContext.getUser_id());
		userContextVO.setContext_key("bookmark");

		return this.userContextService.searchUser_contextService(serviceContext, userContextVO);
	}

	public static String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;
		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");
			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	private HttpSession copySessionData(HttpSession old_session) {
		Enumeration keys = old_session.getAttributeNames();
		HashMap<String, Object> hm = new HashMap<String, Object>();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			hm.put(key, old_session.getValue(key));
			old_session.removeAttribute(key); 
		}
		/*
		 * try { old_session.invalidate(); } catch (Exception e) {
		 * logger.info("Session already invalidate"); }
		 */
		HttpSession new_session = request.getSession(true);

		for (Map.Entry m : hm.entrySet()) {
			new_session.setAttribute((String) m.getKey(), m.getValue());
			hm.remove(m);
		}

		new_session.setMaxInactiveInterval(old_session.getMaxInactiveInterval());

		return new_session;
	}

	void disconnectOldSession(String user_code) {
		Map<String, HttpSession> sessionClone = (Map<String, HttpSession>) new HashMap<String, HttpSession>(
				CustomSessionListener.sessions).clone();

		HttpSession session;
		for (Map.Entry item : CustomSessionListener.sessions.entrySet()) {
			session = (HttpSession) item.getValue();

			if (((String) item.getKey()).indexOf(SessionSign.COLD_SESSION) > -1)
				continue;

			RestServiceContextStore restServiceContext = null;
			try {
				restServiceContext = (RestServiceContextStore) (session.getAttribute("RestServiceContext"));
			} catch (Exception e) {
				sessionClone.remove(item.getKey());
				continue;
			}

			if (restServiceContext == null)
				continue;

			if (restServiceContext.getServiceContext() == null)
				continue;

			if (restServiceContext.getServiceContext().getUserId() == null)
				continue;

			if (restServiceContext.getServiceContext().getUserId().equals(user_code)) {
				sessionClone.remove(item.getKey());
				try {
					session.invalidate();
					logger.info(String.format("REASON SESSION DESTROYED: NEW LOGIN [%s]", user_code));
				} catch (Exception e) {
					logger.error("Session already invalid");
				}

			}
		}

		CustomSessionListener.sessions = (Map<String, HttpSession>) new HashMap<String, HttpSession>(sessionClone)
				.clone();
	}

	private void buildServiceContext(LoginVO loginVO, String userLocale, String defaultLocale,
			RestServiceContextStore restServiceContext) throws NoSuchAlgorithmException {
		restServiceContext.setMenus(loginVO.getMenusGranted().toArray(new MenuVO[loginVO.getMenusGranted().size()]));
		restServiceContext.getServiceContext().setUser_id(loginVO.getUsers_id());
		restServiceContext.getServiceContext().setUsername(loginVO.getUser_name());
		restServiceContext.getServiceContext().setDataColumnsFilterList(loginVO.getDataColumnsFilter());
		restServiceContext.getServiceContext().setTimeZone("GMT");
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		restServiceContext.getServiceContext().setProfileCode(loginVO.getProfile_fk());
		List<String> listOfRoles = extract(loginVO.getRole_col(), on(RoleVO.class).getRole_id());
		restServiceContext.getServiceContext().setProperty("listOfRoles", (Serializable) listOfRoles);
		restServiceContext.getServiceContext().setProperty("userMail", loginVO.getMail());
		restServiceContext.getServiceContext().setProperty("userLocale", userLocale);
		restServiceContext.getServiceContext().setProperty("defaultLocale", defaultLocale);
		restServiceContext.getServiceContext().setProperty("defaultBank", loginVO.getInstitution_fk());
		restServiceContext.getServiceContext().setProperty("defaultBranch", loginVO.getBranch_fk());
		restServiceContext.getServiceContext().setProperty("defaultGroup", loginVO.getBranch_group_fk());
		restServiceContext.getServiceContext().setProperty("defaultDepartement", loginVO.getDepartement_fk());
		restServiceContext.getServiceContext().setProperty("defaultCountry", loginVO.getCountry_fk());
        restServiceContext.getServiceContext().setProperty("sysDate", new Date());
        restServiceContext.getServiceContext().setProperty("lastLogin", loginVO.getLoginDate());
		restServiceContext.getServiceContext().setStoredKey(generateMaskKey());
	}
	
    private List<GrantsVO> getPwcApis(ServiceContext ctx) {
		List<GrantsVO> result = null;
		try {
			result = this.grantsService.getAllGrantsService(ctx);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private List<Grants2profilesVO> getBlackListedApis(ServiceContext ctx) {
		List<Grants2profilesVO> result = null;
		try {
			result = this.grants2profilesService.getAllGrants2profilesService(ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private String generateMaskKey() throws NoSuchAlgorithmException {

		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		SecretKey key = keyGen.generateKey();
		return asHex(key.getEncoded());
	}

	private LoginVO processAuthentification(AuthenticationBean authBean, RestServiceContextStore restServiceContext,
			boolean isLdapAuthentication, String user_mail, String silo, String user_code) throws Exception {
		LoginVO l_loginVO = new LoginVO();
		LoginVO loginVO_authService;
		
		String token = authBean.getToken();
		HttpSession _session = CustomSessionListener.sessions.get(token);

		try {
			l_loginVO.setLoginDate(authBean.getLoginDate());
			if (isLdapAuthentication) {
				l_loginVO.setMail(user_mail);
				l_loginVO.setUser_code(user_code);
				if(authBean.getPass() != null && !authBean.getPass().equals("")) {
					char[] passwordDecrypted = SecurityKeysProvider.decrypt(
							SecurityKeysProvider.decodeBASE64(authBean.getPass()),
							restServiceContext.getKey().getPrivateKey()).toCharArray();
					l_loginVO.setPassword(new String(passwordDecrypted));
					java.util.Arrays.fill(passwordDecrypted, '*');
				}
			} else {
				l_loginVO.setUser_code(authBean.getLogin());
				char[] passwordDecrypted = SecurityKeysProvider.decrypt(
						SecurityKeysProvider.decodeBASE64(authBean.getPass()),
						restServiceContext.getKey().getPrivateKey()).toCharArray();
				l_loginVO.setPassword(new String(passwordDecrypted));
				java.util.Arrays.fill(passwordDecrypted, '*');
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new OurException("0039", ex);
		}

		if (isLdapAuthentication) {
			if(silo != null) {
				loginVO_authService = authentificationService
						.ldapMailAuthentificationService(restServiceContext.getServiceContext(), l_loginVO, silo);
				restServiceContext.getServiceContext().setAuthentication(loginVO_authService.getAuthentication());
				// usersSessions.put(user_mail, session);
				_session.setAttribute("userId", user_mail);
			} else {
				loginVO_authService = authentificationService.ldapAuthentificationService(restServiceContext.getServiceContext(), l_loginVO);
				restServiceContext.getServiceContext().setAuthentication(loginVO_authService.getAuthentication());
				// usersSessions.put(user_mail, session);
				_session.setAttribute("userId", user_mail);
			}
		} else {
			loginVO_authService = authentificationService
					.authentificationService(restServiceContext.getServiceContext(), l_loginVO);
			restServiceContext.getServiceContext().setAuthentication(loginVO_authService.getAuthentication());
			//usersSessions.put(l_loginVO.getUser_code(), session);
			_session.setAttribute("userId", l_loginVO.getUser_code());
		}
		//CustomSessionListener.sessions.put(authBean.getToken(), session);

		l_loginVO.setConnection_status(loginVO_authService.getConnection_status());
		l_loginVO = service.loginService(restServiceContext.getServiceContext(), l_loginVO);
		return l_loginVO;
	}

	private String getIpAddress() {
		String ipAddress = request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr()
				: request.getHeader("X-FORWARDED-FOR");
		return ipAddress == null ? "" : ipAddress;
	}

	private List<RoleVO> getRolesAdmin(Collection<RoleVO> listOfRoles) {
		List<RoleVO> list = new ArrayList<>();
		listOfRoles.stream().forEach(role -> {
			RoleVO r = new RoleVO();
			r.setRole_id(role.getRole_id());
			r.setAdmin(role.getAdmin());
			list.add(r);
		});
		return list;
	}
	
	/**
	 * @return session timeout duration in seconds.
	 */
	private int getSessionTimeoutDuration() {
		/**
		 * 15 minutes, the max allowed by PCI DSS:
		 * https://cisofy.com/compliance/pci-dss/pci-dss-8-1-8-session-idle-timeout/
		 */
		int sessionTimeoutSeconds = 15 * 60;

		if (System.getProperty(SESSION_TIMEOUT_PROPERTY) != null) {
			sessionTimeoutSeconds = Math.min(sessionTimeoutSeconds,
					Integer.parseInt(System.getProperty(SESSION_TIMEOUT_PROPERTY)));
		}
		return sessionTimeoutSeconds;
	}

}
