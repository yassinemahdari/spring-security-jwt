package ma.hps.powercard.compliance.serviceimpl.spec;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.WebCustomAuthenticationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.serviceapi.BankVO;
import ma.hps.powercard.compliance.serviceapi.LoginService;
import ma.hps.powercard.compliance.serviceapi.LoginVO;
import ma.hps.powercard.compliance.serviceapi.MenuVO;
import ma.hps.powercard.compliance.serviceapi.Powercard_globalsService;
import ma.hps.powercard.compliance.serviceapi.Powercard_globalsVO;
import ma.hps.powercard.compliance.serviceapi.Pwc_oauth2_profileService;
import ma.hps.powercard.compliance.serviceapi.Pwc_oauth2_profileVO;
import ma.hps.powercard.compliance.serviceapi.Pwc_oauth2_configService;
import ma.hps.powercard.compliance.serviceapi.Pwc_oauth2_configVO;
import ma.hps.powercard.compliance.serviceapi.RoleVO;
import ma.hps.powercard.compliance.serviceapi.Switch_cut_offService;
import ma.hps.powercard.compliance.serviceapi.Switch_cut_offVO;
import ma.hps.powercard.compliance.serviceapi.User_contextService;
import ma.hps.powercard.compliance.serviceapi.User_contextVO;
import ma.hps.powercard.compliance.utils.GsonHelper;
import ma.hps.powercard.constants.GlobalVars;
import ma.hps.powercard.dto.AuthorizationCodeBean;
import ma.hps.powercard.dto.ErrorResponse;
import ma.hps.powercard.dto.SuccessResponse;

@RestController
public class PwcOAuth2 {

	private static Logger logger = Logger.getLogger(PwcOAuth2.class);
	private static Map<String, String> usersSessions = new HashMap<String, String>();
	@Autowired
	private LoginService loginService;
	@Autowired
	private User_contextService userContextService;
	@Autowired
	private Switch_cut_offService switch_cut_offService;
	@Autowired
	Powercard_globalsService powercard_globalsService;
	@Autowired
	Pwc_oauth2_configService pwc_oauth2_configService;
	@Autowired
	Pwc_oauth2_profileService pwc_oauth2_profileService;

	@CrossOrigin
	@PostMapping(value = "/oauth2/authorization", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String oauth2(HttpServletRequest request, HttpServletResponse response) {
		String oauth2_server_api;
		String client_id;
		String response_type = "code";
		String scope = null;
		SessionKeyBean sessionKeyBean;
		List<Pwc_oauth2_configVO> list_oauth_config;
		Pwc_oauth2_configVO pwc_oauth2_configVO;
		try {
			sessionKeyBean = SessionSign.getSessionKey(request);
		} catch (Exception e) {
			return ErrorResponse.from("oauth2 redirection error").toJson();
		}
		String state = sessionKeyBean.getToken();
		
		try {
			ServiceContext ctx = new ServiceContext("OAuth2User", state, "PowerCardV3");
			ctx.setDetails(new WebCustomAuthenticationDetails(state, getIpAddress(request)));
			list_oauth_config = this.pwc_oauth2_configService.getAllPwc_oauth2_configService(ctx);
			if(list_oauth_config != null && list_oauth_config.size() > 0) {
				pwc_oauth2_configVO = list_oauth_config.get(0);
			} else {
				return ErrorResponse.from("Error get oauth2 config").toJson();
			}
			if(pwc_oauth2_configVO.getOauth2_server_api() != null) {
				oauth2_server_api = pwc_oauth2_configVO.getOauth2_server_api();
			} else {
				return ErrorResponse.from("oauth2_server_api not found").toJson();
			}
			if(pwc_oauth2_configVO.getClient_id() != null) {
				client_id = pwc_oauth2_configVO.getClient_id();
			} else {
				return ErrorResponse.from("client_id not found").toJson();
			}
			if(pwc_oauth2_configVO.getScope() != null) {
				scope = pwc_oauth2_configVO.getScope();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ErrorResponse.from("Error get oauth2 config").toJson();
		}

		if (oauth2_server_api != null && !oauth2_server_api.equals("")) {
			oauth2_server_api += "?response_type=" + response_type;
			if (client_id != null && !client_id.equals("")) {
				oauth2_server_api += "&client_id=" + client_id;
			} else {
				return ErrorResponse.from("parameter client_id not found").toJson();
			}
			if (scope != null && !scope.equals("")) {
				oauth2_server_api += "&scope=" + scope;
			}
			if (state != null && !state.equals("")) {
				state = state.replace(SessionSign.COLD_SESSION, "");
				oauth2_server_api += "&state=" + state;
			}
			return SuccessResponse.from(oauth2_server_api).toJson();
		} else {
			return ErrorResponse.from("oauth2_server_api not found").toJson();
		}
	}

	@CrossOrigin
	@PostMapping(value = "/oauth2_login", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String oauth2_login(@RequestParam("authorizationCode") String authorizationCodeStr,
			HttpServletRequest request) {
		String userLocale = "en_US";
		String defaultLocale = "en_US";
		String userId = null;
		String userMail = null;
		String applicationId = "PowerCardV3";
		String jwt = "";
		String oauth2_userinfo_api = null;
		String oauth2_accessToken_api = null;
		String client_id = null;
		String client_secret = null;
		String redirect_uri = null;
		String access_token = null;
		String userinfo_parameter_name = null;
		String user_profile_id = null;
		String user_bank_code = null;
		String token_contains_info = null;
		String profile_parameter_name = null;
		List<Pwc_oauth2_configVO> list_oauth_config;
		Pwc_oauth2_configVO pwc_oauth2_configVO;
		AuthorizationCodeBean authorizationCodeBean = GsonHelper.getGson().fromJson(authorizationCodeStr,
				AuthorizationCodeBean.class);
		String authorization_code = authorizationCodeBean.getAuthorization_code();
		String state = authorizationCodeBean.getState();
		if (authorization_code == null) {
			return ErrorResponse.from("Null authorization code parameter !").toJson();
		}
		if (state == null) {
			return ErrorResponse.from("Null state parameter !").toJson();
		}
		state = state.concat(SessionSign.COLD_SESSION);
		HttpSession session = CustomSessionListener.sessions.get(state);
		if (session == null) {
			return ErrorResponse.from("Null session, please refreh and try again !").toJson();
		}
		if (session.getAttribute("RestServiceContext") == null) {
			return ErrorResponse.from("Null restServiceContext, please refreh and try again !").toJson();
		}
		try {
			ServiceContext ctx = new ServiceContext("OAuth2User", session.getId(), applicationId);
			ctx.setDetails(new WebCustomAuthenticationDetails(session.getId(), getIpAddress(request)));
			list_oauth_config = this.pwc_oauth2_configService.getAllPwc_oauth2_configService(ctx);
			if(list_oauth_config != null && list_oauth_config.size() > 0) {
				pwc_oauth2_configVO = list_oauth_config.get(0);
			} else {
				return ErrorResponse.from("Error get oauth2 config").toJson();
			}
			if(pwc_oauth2_configVO.getOauth2_accesstoken_api() != null) {
				oauth2_accessToken_api = pwc_oauth2_configVO.getOauth2_accesstoken_api();
			} else {
				return ErrorResponse.from("oauth2_accessToken_api not found").toJson();
			}
			if(pwc_oauth2_configVO.getOauth2_userinfo_api() != null) {
				oauth2_userinfo_api = pwc_oauth2_configVO.getOauth2_userinfo_api();
			}
			if(pwc_oauth2_configVO.getClient_id() != null) {
				client_id = pwc_oauth2_configVO.getClient_id();
			} else {
				return ErrorResponse.from("client_id not found").toJson();
			}
			if(pwc_oauth2_configVO.getClient_secret() != null) {
				client_secret = pwc_oauth2_configVO.getClient_secret();
			} else {
				return ErrorResponse.from("client_secret not found").toJson();
			}
			if(pwc_oauth2_configVO.getRedirect_uri() != null) {
				redirect_uri = pwc_oauth2_configVO.getRedirect_uri();
			} else {
				return ErrorResponse.from("redirect_uri not found").toJson();
			}
			if(pwc_oauth2_configVO.getUserinfo_parameter_name() != null) {
				userinfo_parameter_name = pwc_oauth2_configVO.getUserinfo_parameter_name();
			} else {
				return ErrorResponse.from("userinfo_parameter_name not found").toJson();
			}
			if(pwc_oauth2_configVO.getUser_bank_code() != null) {
				user_bank_code = pwc_oauth2_configVO.getUser_bank_code();
			} else {
				return ErrorResponse.from("user_bank_code not found").toJson();
			}
			if(pwc_oauth2_configVO.getToken_contains_info() != null) {
				token_contains_info = pwc_oauth2_configVO.getToken_contains_info();
			}
			if(pwc_oauth2_configVO.getProfile_parameter_name() != null) {
				profile_parameter_name = pwc_oauth2_configVO.getProfile_parameter_name();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ErrorResponse.from("Error get oauth2 config").toJson();
		}
		try {
			access_token = getOAuth2AccessToken(authorization_code, oauth2_accessToken_api, client_id, client_secret, redirect_uri);
			if(token_contains_info != null && token_contains_info.equalsIgnoreCase("Y")) {
				user_profile_id = getMappedProfile(access_token, profile_parameter_name);
			} else {
				user_profile_id = pwc_oauth2_configVO.getUser_profile_id();
			}
			userMail = getOAuthUserInfo(token_contains_info, oauth2_userinfo_api, access_token, userinfo_parameter_name);
			userId = manageUserId(userMail, userinfo_parameter_name);
		} catch(Exception e) {
			return ErrorResponse.from(e.getMessage()).toJson();
		}
		RestServiceContextStore restServiceContext = (RestServiceContextStore) (session
				.getAttribute("RestServiceContext"));

		restServiceContext.setServiceContext(new ServiceContext(userId, session.getId(), applicationId));
		restServiceContext.getServiceContext().setIsValidated(true);

		WebCustomAuthenticationDetails details = new WebCustomAuthenticationDetails(session.getId(),
				getIpAddress(request));
		restServiceContext.getServiceContext().setDetails(details);
		RestServiceContextStore responseLogin = new RestServiceContextStore();

		/************** Authentification Process ***********************/
		LoginVO loginVO = new LoginVO();
		try {
			loginVO.setUser_code(userId);
			loginVO.setMail(userMail);
			Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
			restServiceContext.getServiceContext().setAuthentication(authentication);
			session.setAttribute("userId", loginVO.getUser_code());
			loginVO.setConnection_status("O");
			loginVO.setProfile_code(user_profile_id);
			loginVO.setInstitution_fk(user_bank_code);
			loginVO = loginService.loginOAuth2Service(restServiceContext.getServiceContext(), loginVO);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ErrorResponse.from(e.getMessage()).toJson();
		}

		// if a user has an old session disconnect it
		try {
			disconnectOldSession(userId);
		} catch (Exception e) {
			logger.error("An error was triggered when server tries to disconnect the old session " + e.getMessage());
		}

		/********* Set User context *******/
		try {
			buildServiceContext(loginVO, userLocale, defaultLocale, restServiceContext);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		session.setAttribute("loginVO", loginVO);
		session.setAttribute("status", "N");
		session.setMaxInactiveInterval(15*60);

		// Set jwt_time_expiration
		int jwt_time_expiration = 7200 * 1000;
		// Create JWT Token
		try {
			jwt = SecurityKeysProvider.createJwtToken(restServiceContext.getKey().getJwtAlgorithm(),
					jwt_time_expiration);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ErrorResponse.from("0039").toJson();
		}

		restServiceContext.setJwt(jwt);
		CustomSessionListener.sessions.remove(state);// remove the old session
		CustomSessionListener.sessions.put(jwt, session);// Create a new one

		responseLogin = new RestServiceContextStore();
		try {
			buildServiceContextToPost(responseLogin, restServiceContext, loginVO);
		} catch (Exception e) {
			logger.error("Error building ServiceContextToPost: " + e.getMessage());
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
			responseLogin.setUserContexts(user_contextList);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		try {
			List<Switch_cut_offVO> switch_cut_offList = switch_cut_offService
					.getAllSwitch_cut_offService(restServiceContext.getServiceContext());
			responseLogin.setBusinessDate(switch_cut_offList.get(0).getLast_cutoff_date_bo());
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
		responseLogin.setIsLdapLogged(false);
		responseLogin.setBankDataAccess(liste);
		responseLogin.setServerTimeZone(GlobalVars.timeZone.orElse(null));

		Powercard_globalsVO powercard_globalsVO = new Powercard_globalsVO();
		powercard_globalsVO.setVariable_name("ENABLE_DEFAULT_MASK");
		List<Powercard_globalsVO> searchResult = new ArrayList<Powercard_globalsVO>();
		try {
			searchResult = powercard_globalsService
					.searchPowercard_globalsService(restServiceContext.getServiceContext(), powercard_globalsVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (searchResult.size() > 0) {
			restServiceContext.getServiceContext().setProperty("enable_default_mask",
					searchResult.get(0).getVariable_value());
		}

		// only keeping bank_code in ctx for vb_banks
		List<String> ctx_vb = ((List<JsonObject>) restServiceContext.getServiceContext().getProperty("vb_banks"))
				.stream().map(b -> b.get("bank_code").getAsString()).collect(Collectors.toList());
		restServiceContext.getServiceContext().setProperty("vb_banks", ctx_vb);

		return SuccessResponse.from(responseLogin).toJson();
	}
	
	private String getOAuthUserInfo(String token_contains_info, String oauth2_userinfo_api, String access_token, String userinfo_parameter_name) throws Exception {
		if(token_contains_info != null && token_contains_info.equalsIgnoreCase("Y")) {
			try {
				return SecurityKeysProvider.getClaimsFromJWT(access_token, userinfo_parameter_name);
			} catch(Exception e) {
				throw new OurException("Error get user info", null);
			}
		} else {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.AUTHORIZATION, "Bearer "+access_token);
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<String> response = restTemplate.exchange(oauth2_userinfo_api, HttpMethod.GET, entity, String.class);
			if(response.getStatusCode() == HttpStatus.OK) {
				JsonObject json = GsonHelper.getGson().fromJson(response.getBody(), JsonObject.class);
				return json.get(userinfo_parameter_name).getAsString();
			} else {
				throw new OurException("Error get user info", null);
			}
		}
	}

	private String getMappedProfile(String access_token, String profile_parameter_name) throws Exception {
		try {
			String oauth2_profile_code = SecurityKeysProvider.getClaimsFromJWT(access_token, profile_parameter_name);
			Pwc_oauth2_profileVO profileMapVO = new Pwc_oauth2_profileVO();
			profileMapVO.setOauth2_profile_code(oauth2_profile_code);
			List<Pwc_oauth2_profileVO> list = pwc_oauth2_profileService.searchPwc_oauth2_profileService(new ServiceContext(), profileMapVO);
			if(list != null && list.size() > 0) {
				return list.get(0).getProfile_fk();
			} else {
				throw new OurException("mapping profile not found", null);
			}
		} catch(Exception e) {
			throw new OurException("Error get user info", null);
		}
	}
	
	private String getOAuth2AccessToken(String authorization_code,
			String oauth2_accessToken_api,
			String client_id,
			String client_secret,
			String redirect_uri) throws Exception {
		String access_token;
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
		MultiValueMap<String, String> forms = new LinkedMultiValueMap<>();
		forms.add("client_id", client_id);
		forms.add("client_secret", client_secret);
		forms.add("code", authorization_code);
		forms.add("grant_type", "authorization_code");
		forms.add("redirect_uri", redirect_uri);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(forms, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(oauth2_accessToken_api, request, String.class);
		if(response.getStatusCode() == HttpStatus.OK) {
			try {
				JsonObject json = GsonHelper.getGson().fromJson(response.getBody(), JsonObject.class);
				access_token = json.get("access_token").getAsString();
			} catch(Exception e) {
				throw new OurException("Error getting access token from json value", null);
			}
		} else {
			throw new OurException("Error access token", null);
		}
		return access_token;
	}
	
	private String manageUserId(String userInfo, String userinfo_parameter_name) throws Exception {
		String trc_userId = null;
		if(userinfo_parameter_name.equalsIgnoreCase("email")) {
			trc_userId = userInfo.split("@")[0];
		} else {
			trc_userId = userInfo;
		}
		if (trc_userId != null && trc_userId.length() > 15) {
			trc_userId = trc_userId.substring(0, 15);
		}
		return trc_userId;
	}

	private String getIpAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr()
				: request.getHeader("X-FORWARDED-FOR");
		return ipAddress == null ? "" : ipAddress;
	}

	private void disconnectOldSession(String user_code) {
		String token = usersSessions.get(user_code);
		HttpSession session = CustomSessionListener.sessions.get(token);
		if (session != null) {
			CustomSessionListener.sessions.remove(token);
			session.invalidate();
			logger.info(String.format("REASON SESSION DESTROYED: NEW LOGIN [%s]", user_code));
		}
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
		restServiceContextToPost.setServiceContext(ctx);
		restServiceContextToPost.setJwt(restServiceContext.getJwt());
		restServiceContextToPost.setUuid(restServiceContext.getUuid());
		restServiceContextToPost.setMenus(restServiceContext.getMenus());
		restServiceContextToPost.setMenuGroupBymodulesAndWorkspaces(getMenuGroupBymodulesAndWorkspaces(restServiceContext.getMenus()));
		restServiceContextToPost.setModulesByWorkspaces(getModuleGroupByWorkspace(restServiceContext.getMenus()));
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

			moduleGroupeByWorkspace.put(workspace, new HashSet<String>(modulesTmp));
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
	
	private String generateMaskKey() throws NoSuchAlgorithmException {

		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		SecretKey key = keyGen.generateKey();
		return asHex(key.getEncoded());
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

}
