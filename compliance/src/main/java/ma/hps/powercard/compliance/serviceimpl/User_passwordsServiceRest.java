package ma.hps.powercard.compliance.serviceimpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;

import ma.hps.powercard.annotation.ApiMapping;
import ma.hps.powercard.compliance.serviceapi.User_passwordsService;
import ma.hps.powercard.compliance.serviceapi.User_passwordsVO;
import ma.hps.powercard.compliance.serviceimpl.spec.CustomSessionListener;
import ma.hps.powercard.compliance.serviceimpl.spec.RestServiceContextStore;
import ma.hps.powercard.compliance.serviceimpl.spec.SecurityKeysProvider;
import ma.hps.powercard.compliance.utils.GsonHelper;
import ma.hps.powercard.constants.GlobalVars;
import ma.hps.powercard.constants.PwcStatusCode;
import ma.hps.powercard.dto.ApiResponse;
import ma.hps.powercard.dto.ErrorResponse;
import ma.hps.powercard.dto.SuccessResponse;

/**
 * Rest Implementation of User_passwordsService.
 */
// @Component
// @Path("/compliance/User_passwordsService")
@RestController
@RequestMapping("/compliance/User_passwordsService")
public class User_passwordsServiceRest {
	private static Logger logger = Logger.getLogger(User_passwordsServiceRest.class);
	@Lazy
	@Autowired
	private User_passwordsService user_passwordsService;

	public User_passwordsServiceRest() {
	}

	// @javax.ws.rs.POST
	// @Path("/createUser_passwordsService")
	@CrossOrigin
	@RequestMapping(value = "/createUser_passwordsService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String createUser_passwordsService(@RequestParam("jwt") String jwt,
			@RequestParam("user_passwordsVO") String user_passwordsVOStr) {
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

		User_passwordsVO user_passwordsVO = GsonHelper.getGson().fromJson(user_passwordsVOStr, User_passwordsVO.class);

		String result;
		try {
			result = user_passwordsService.createUser_passwordsService(ctx, user_passwordsVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	// @javax.ws.rs.POST
	// @Path("/updateUser_passwordsService")
	@CrossOrigin
	@RequestMapping(value = "/updateUser_passwordsService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String updateUser_passwordsService(@RequestParam("jwt") String jwt,
			@RequestParam("user_passwordsVO") String user_passwordsVOStr) {
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

		User_passwordsVO user_passwordsVO = GsonHelper.getGson().fromJson(user_passwordsVOStr, User_passwordsVO.class);

		String result;
		try {
			result = user_passwordsService.updateUser_passwordsService(ctx, user_passwordsVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	// @javax.ws.rs.POST
	// @Path("/deleteUser_passwordsService")
	@CrossOrigin
	@RequestMapping(value = "/deleteUser_passwordsService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String deleteUser_passwordsService(@RequestParam("jwt") String jwt,
			@RequestParam("user_passwordsVO") String user_passwordsVOStr) {
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

		User_passwordsVO user_passwordsVO = GsonHelper.getGson().fromJson(user_passwordsVOStr, User_passwordsVO.class);

		String result;
		try {
			result = user_passwordsService.deleteUser_passwordsService(ctx, user_passwordsVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	// @javax.ws.rs.POST
	// @Path("/getAllUser_passwordsService")
	@CrossOrigin
	@RequestMapping(value = "/getAllUser_passwordsService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String getAllUser_passwordsService(@RequestParam("jwt") String jwt) {
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

		List<User_passwordsVO> result;
		try {
			result = user_passwordsService.getAllUser_passwordsService(ctx);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	// @javax.ws.rs.POST
	// @Path("/searchUser_passwordsService")
	@CrossOrigin
	@RequestMapping(value = "/searchUser_passwordsService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String searchUser_passwordsService(@RequestParam("jwt") String jwt,
			@RequestParam("user_passwordsVO") String user_passwordsVOStr) {
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

		User_passwordsVO user_passwordsVO = GsonHelper.getGson().fromJson(user_passwordsVOStr, User_passwordsVO.class);

		List<User_passwordsVO> result;
		try {
			result = user_passwordsService.searchUser_passwordsService(ctx, user_passwordsVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	@ApiMapping(value = "/changePassword")
	public String changePassword(@RequestParam("jwt") String jwt, @RequestParam("oldPassword") String oldpassword,
			@RequestParam("newPassword") String newPassword) {

		HttpSession session = CustomSessionListener.sessions.get(jwt);
		RestServiceContextStore restServiceContext = (RestServiceContextStore) (session
				.getAttribute("RestServiceContext"));
		ServiceContext ctx = restServiceContext.getServiceContext();

		try {
			String login = ctx.getUserId();
			char[] decryptedOldPass = SecurityKeysProvider.decrypt(SecurityKeysProvider.decodeBASE64(oldpassword),
					restServiceContext.getKey().getPrivateKey()).toCharArray();
			char[] decryptedNewPass = SecurityKeysProvider.decrypt(SecurityKeysProvider.decodeBASE64(newPassword),
					restServiceContext.getKey().getPrivateKey()).toCharArray();

			String result = user_passwordsService.changePassword(ctx, login, new String(decryptedOldPass),
					new String(decryptedNewPass));

			Arrays.fill(decryptedNewPass, '0');
			Arrays.fill(decryptedOldPass, '0');

			return SuccessResponse.from(result).toJson();

		} catch (Exception e) {
			if (e.getMessage().equals(PwcStatusCode.PASSWORD_ALREADY_USED.toString())
					|| e.getMessage().equals(PwcStatusCode.PASSWORD_IN_DICTIONARY.toString())
					|| e.getMessage().equals(PwcStatusCode.PASSWORD_LIKE_USER_ID.toString())
					|| e.getMessage().equals(PwcStatusCode.PASSWORD_LIKE_USERNAME.toString())) {

				Map<String, String> response = new HashMap<>();
				response.put("message", GlobalVars.FIRST_LOGIN_CHANGE_PASS);
				response.put("status", e.getMessage());

				return new ApiResponse(false, response).toJson();
			}
			return ErrorResponse.from(e.getMessage()).toJson();
		}

	}

	public String verifyPassword(ServiceContext ctx, String password) throws Exception {

		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("verifyPassword not implemented");

	}
}
