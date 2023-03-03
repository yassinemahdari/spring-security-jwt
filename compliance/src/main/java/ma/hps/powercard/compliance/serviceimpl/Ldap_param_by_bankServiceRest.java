package ma.hps.powercard.compliance.serviceimpl;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ma.hps.powercard.compliance.serviceapi.Ldap_param_by_bankService;
import ma.hps.powercard.compliance.serviceapi.Ldap_param_by_bankVO;
import ma.hps.powercard.compliance.serviceimpl.spec.CustomSessionListener;
import ma.hps.powercard.compliance.serviceimpl.spec.RestServiceContextStore;
import ma.hps.powercard.compliance.utils.GsonHelper;

/**
 * Rest Implementation of Ldap_param_by_bankService.
 */
@RestController
@RequestMapping("/compliance/Ldap_param_by_bankService")
public class Ldap_param_by_bankServiceRest {
	private static Logger logger = Logger.getLogger(Ldap_param_by_bankServiceRest.class);

	@Lazy
	@Autowired
	private Ldap_param_by_bankService ldap_param_by_bankService;

	public Ldap_param_by_bankServiceRest() {
	}

	@CrossOrigin
	@ResponseBody
	@PostMapping(value = "/createLdap_param_by_bankService", produces = MediaType.TEXT_PLAIN_VALUE)
	public String createLdap_param_by_bankService(@RequestParam("jwt") String jwt,
			@RequestParam("ldap_param_by_bankVO") String ldap_param_by_bankVOStr) {
		JsonObject json = new JsonObject();

		HttpSession session = CustomSessionListener.sessions.get(jwt);
		if (session == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Session expired !");
			return GsonHelper.getGson().toJson(json);
		}
		RestServiceContextStore restServiceContext = (RestServiceContextStore) (session
				.getAttribute("RestServiceContext"));
		if (restServiceContext == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null RestServiceContextStore is not allowed !");
			return GsonHelper.getGson().toJson(json);
		}
		ServiceContext ctx = restServiceContext.getServiceContext();
		if (ctx == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null ServiceContext is not allowed !");
			return GsonHelper.getGson().toJson(json);
		}

		;

		Ldap_param_by_bankVO ldap_param_by_bankVO = GsonHelper.getGson().fromJson(ldap_param_by_bankVOStr,
				Ldap_param_by_bankVO.class);

		String result;
		try {
			result = ldap_param_by_bankService.createLdap_param_by_bankService(ctx, ldap_param_by_bankVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	@CrossOrigin
	@ResponseBody
	@PostMapping(value = "/updateLdap_param_by_bankService", produces = MediaType.TEXT_PLAIN_VALUE)
	public String updateLdap_param_by_bankService(@RequestParam("jwt") String jwt,
			@RequestParam("ldap_param_by_bankVO") String ldap_param_by_bankVOStr) {
		JsonObject json = new JsonObject();

		HttpSession session = CustomSessionListener.sessions.get(jwt);
		if (session == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Session expired !");
			return GsonHelper.getGson().toJson(json);
		}
		RestServiceContextStore restServiceContext = (RestServiceContextStore) (session
				.getAttribute("RestServiceContext"));
		if (restServiceContext == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null RestServiceContextStore is not allowed !");
			return GsonHelper.getGson().toJson(json);
		}
		ServiceContext ctx = restServiceContext.getServiceContext();
		if (ctx == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null ServiceContext is not allowed !");
			return GsonHelper.getGson().toJson(json);
		}

		;

		Ldap_param_by_bankVO ldap_param_by_bankVO = GsonHelper.getGson().fromJson(ldap_param_by_bankVOStr,
				Ldap_param_by_bankVO.class);

		String result;
		try {
			result = ldap_param_by_bankService.updateLdap_param_by_bankService(ctx, ldap_param_by_bankVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	@CrossOrigin
	@ResponseBody
	@PostMapping(value = "/deleteLdap_param_by_bankService", produces = MediaType.TEXT_PLAIN_VALUE)
	public String deleteLdap_param_by_bankService(@RequestParam("jwt") String jwt,
			@RequestParam("ldap_param_by_bankVO") String ldap_param_by_bankVOStr) {
		JsonObject json = new JsonObject();

		HttpSession session = CustomSessionListener.sessions.get(jwt);
		if (session == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Session expired !");
			return GsonHelper.getGson().toJson(json);
		}
		RestServiceContextStore restServiceContext = (RestServiceContextStore) (session
				.getAttribute("RestServiceContext"));
		if (restServiceContext == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null RestServiceContextStore is not allowed !");
			return GsonHelper.getGson().toJson(json);
		}
		ServiceContext ctx = restServiceContext.getServiceContext();
		if (ctx == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null ServiceContext is not allowed !");
			return GsonHelper.getGson().toJson(json);
		}

		;

		Ldap_param_by_bankVO ldap_param_by_bankVO = GsonHelper.getGson().fromJson(ldap_param_by_bankVOStr,
				Ldap_param_by_bankVO.class);

		String result;
		try {
			result = ldap_param_by_bankService.deleteLdap_param_by_bankService(ctx, ldap_param_by_bankVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	@CrossOrigin
	@ResponseBody
	@PostMapping(value = "/getAllLdap_param_by_bankService", produces = MediaType.TEXT_PLAIN_VALUE)
	public String getAllLdap_param_by_bankService(@RequestParam("jwt") String jwt) {
		JsonObject json = new JsonObject();

		HttpSession session = CustomSessionListener.sessions.get(jwt);
		if (session == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Session expired !");
			return GsonHelper.getGson().toJson(json);
		}
		RestServiceContextStore restServiceContext = (RestServiceContextStore) (session
				.getAttribute("RestServiceContext"));
		if (restServiceContext == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null RestServiceContextStore is not allowed !");
			return GsonHelper.getGson().toJson(json);
		}
		ServiceContext ctx = restServiceContext.getServiceContext();
		if (ctx == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null ServiceContext is not allowed !");
			return GsonHelper.getGson().toJson(json);
		}

		List<Ldap_param_by_bankVO> result;
		try {
			result = ldap_param_by_bankService.getAllLdap_param_by_bankService(ctx);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	@CrossOrigin
	@ResponseBody
	@PostMapping(value = "/findAllLdapProfilesService", produces = MediaType.TEXT_PLAIN_VALUE)
	public String findAllLdapProfilesService(@RequestParam("jwt") String jwt,
			// @FormParam("manager_id")String manager_id,
			// @FormParam("manager_password")String manager_password,
			// @FormParam("ldap_url")String ldap_url,
			// @FormParam("ldap_base")String ldap_base,
			@RequestParam("ldap_connectionVO") String ldap_connectionVOStr) {
		JsonObject json = new JsonObject();

		HttpSession session = CustomSessionListener.sessions.get(jwt);
		if (session == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Session expired !");
			return GsonHelper.getGson().toJson(json);
		}
		RestServiceContextStore restServiceContext = (RestServiceContextStore) (session
				.getAttribute("RestServiceContext"));
		if (restServiceContext == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null RestServiceContextStore is not allowed !");
			return GsonHelper.getGson().toJson(json);
		}
		ServiceContext ctx = restServiceContext.getServiceContext();
		if (ctx == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null ServiceContext is not allowed !");
			return GsonHelper.getGson().toJson(json);
		}

		JsonObject jsonLdap_connection = new Gson().fromJson(ldap_connectionVOStr, JsonObject.class);

		List<String> result;
		try {
			result = ldap_param_by_bankService.findAllLdapProfilesService(ctx,
					jsonLdap_connection.get("manager_id").getAsString(),
					jsonLdap_connection.get("manager_password").getAsString(),
					jsonLdap_connection.get("ldap_url").getAsString(),
					jsonLdap_connection.get("ldap_base").getAsString());
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	@CrossOrigin
	@ResponseBody
	@PostMapping(value = "/searchLdap_param_by_bankService", produces = MediaType.TEXT_PLAIN_VALUE)
	public String searchLdap_param_by_bankService(@RequestParam("jwt") String jwt,
			@RequestParam("ldap_param_by_bankVO") String ldap_param_by_bankVOStr) {
		JsonObject json = new JsonObject();

		HttpSession session = CustomSessionListener.sessions.get(jwt);
		if (session == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Session expired !");
			return GsonHelper.getGson().toJson(json);
		}
		RestServiceContextStore restServiceContext = (RestServiceContextStore) (session
				.getAttribute("RestServiceContext"));
		if (restServiceContext == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null RestServiceContextStore is not allowed !");
			return GsonHelper.getGson().toJson(json);
		}
		ServiceContext ctx = restServiceContext.getServiceContext();
		if (ctx == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null ServiceContext is not allowed !");
			return GsonHelper.getGson().toJson(json);
		}

		;

		Ldap_param_by_bankVO ldap_param_by_bankVO = GsonHelper.getGson().fromJson(ldap_param_by_bankVOStr,
				Ldap_param_by_bankVO.class);

		List<Ldap_param_by_bankVO> result;
		try {
			result = ldap_param_by_bankService.searchLdap_param_by_bankService(ctx, ldap_param_by_bankVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}
}