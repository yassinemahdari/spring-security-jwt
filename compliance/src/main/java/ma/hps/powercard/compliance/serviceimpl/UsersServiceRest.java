package ma.hps.powercard.compliance.serviceimpl;

import java.util.List;

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

import ma.hps.powercard.compliance.serviceapi.UsersService;
import ma.hps.powercard.compliance.serviceapi.UsersVO;
import ma.hps.powercard.compliance.serviceimpl.spec.CustomSessionListener;
import ma.hps.powercard.compliance.serviceimpl.spec.RestServiceContextStore;
import ma.hps.powercard.compliance.utils.EncryptionUtils;
import ma.hps.powercard.compliance.utils.GsonHelper;

/**
 * Rest Implementation of UsersService.
 */
// @Component
// @Path("/compliance/UsersService")
@RestController
@RequestMapping("/compliance/UsersService")
public class UsersServiceRest {
	private static Logger logger = Logger.getLogger(UsersServiceRest.class);
	private static String key = "powercard";
	@Lazy
	@Autowired
	private UsersService usersService;

	public UsersServiceRest() {
	}

	// @javax.ws.rs.POST
	// @Path("/createUsersService")
	@CrossOrigin
	@RequestMapping(value = "/createUsersService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String createUsersService(@RequestParam("jwt") String jwt, @RequestParam("usersVO") String usersVOStr) {
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

		UsersVO usersVO = GsonHelper.getGson().fromJson(usersVOStr, UsersVO.class);

		String result;
		try {
			result = usersService.createUsersService(ctx, usersVO);
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
	// @Path("/updateUsersService")
	@CrossOrigin
	@RequestMapping(value = "/updateUsersService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String updateUsersService(@RequestParam("jwt") String jwt, @RequestParam("usersVO") String usersVOStr) {
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

		UsersVO usersVO = GsonHelper.getGson().fromJson(usersVOStr, UsersVO.class);

		String result;
		try {
			result = usersService.updateUsersService(ctx, usersVO);
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
	// @Path("/deleteUsersService")
	@CrossOrigin
	@RequestMapping(value = "/deleteUsersService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String deleteUsersService(@RequestParam("jwt") String jwt, @RequestParam("usersVO") String usersVOStr) {
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

		UsersVO usersVO = GsonHelper.getGson().fromJson(usersVOStr, UsersVO.class);

		String result;
		try {
			result = usersService.deleteUsersService(ctx, usersVO);
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
	// @Path("/getAllUsersService")
	@CrossOrigin
	@RequestMapping(value = "/getAllUsersService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String getAllUsersService(@RequestParam("jwt") String jwt) {
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

		List<UsersVO> result;
		try {
			result = usersService.getAllUsersService(ctx);
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
	// @Path("/searchUsersService")
	@CrossOrigin
	@RequestMapping(value = "/searchUsersService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String searchUsersService(@RequestParam("jwt") String jwt, @RequestParam("usersVO") String usersVOStr) {
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

		UsersVO usersVO = GsonHelper.getGson().fromJson(usersVOStr, UsersVO.class);

		List<UsersVO> result;
		try {
			result = usersService.searchUsersService(ctx, usersVO);
			for (UsersVO user : result) {
				if (user.getUsers_id() != null) {
					user.setHash_user_id(EncryptionUtils.encrypt(user.getUsers_id().toString(), key));
				}
			}
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
	// @Path("/initPasswordService")
	@CrossOrigin
	@RequestMapping(value = "/initPasswordService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String initPasswordService(@RequestParam("jwt") String jwt, @RequestParam("usersVO") String usersVOStr) {
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

		UsersVO usersVO = GsonHelper.getGson().fromJson(usersVOStr, UsersVO.class);

		String result;
		try {
			String hash_user_id = usersVO.getHash_user_id();
			if (hash_user_id == null) {
				json.addProperty("EXCEPTION", true);
				json.addProperty("RESULT", "Null hash user id !");
				return GsonHelper.getGson().toJson(json);
			}
			usersVO.setUsers_id(Long.valueOf(EncryptionUtils.decrypt(hash_user_id, key)));
			result = usersService.initPasswordService(ctx, usersVO);
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
	// @Path("/reopenAccountService")
	@CrossOrigin
	@RequestMapping(value = "/reopenAccountService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String reopenAccountService(@RequestParam("jwt") String jwt, @RequestParam("usersVO") String usersVOStr) {
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

		UsersVO usersVO = GsonHelper.getGson().fromJson(usersVOStr, UsersVO.class);

		String result;
		try {
			String hash_user_id = usersVO.getHash_user_id();
			if (hash_user_id == null) {
				json.addProperty("EXCEPTION", true);
				json.addProperty("RESULT", "Null hash user id !");
				return GsonHelper.getGson().toJson(json);
			}
			usersVO.setUsers_id(Long.valueOf(EncryptionUtils.decrypt(hash_user_id, key)));
			result = usersService.reopenAccountService(ctx, usersVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	// @javax.ws.rs.POST
	// @Path("/closeAccountService")
	@CrossOrigin
	@RequestMapping(value = "/closeAccountService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String closeAccountService(@RequestParam("jwt") String jwt, @RequestParam("usersVO") String usersVOStr) {
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

		UsersVO usersVO = GsonHelper.getGson().fromJson(usersVOStr, UsersVO.class);

		String result;
		try {
			String hash_user_id = usersVO.getHash_user_id();
			if (hash_user_id == null) {
				json.addProperty("EXCEPTION", true);
				json.addProperty("RESULT", "Null hash user id !");
				return GsonHelper.getGson().toJson(json);
			}
			usersVO.setUsers_id(Long.valueOf(EncryptionUtils.decrypt(hash_user_id, key)));
			result = usersService.closeAccountService(ctx, usersVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	// @javax.ws.rs.POST
	// @Path("/blockUsersService")
	@CrossOrigin
	@RequestMapping(value = "/blockUsersService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String blockUsersService(@RequestParam("jwt") String jwt, @RequestParam("usersVO") String usersVOStr) {
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

		UsersVO usersVO = GsonHelper.getGson().fromJson(usersVOStr, UsersVO.class);

		String result;
		try {
			String hash_user_id = usersVO.getHash_user_id();
			if (hash_user_id == null) {
				json.addProperty("EXCEPTION", true);
				json.addProperty("RESULT", "Null hash user id !");
				return GsonHelper.getGson().toJson(json);
			}
			usersVO.setUsers_id(Long.valueOf(EncryptionUtils.decrypt(hash_user_id, key)));
			result = usersService.blockUsersService(ctx, usersVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	// @javax.ws.rs.POST
	// @Path("/unblockUsersService")
	@CrossOrigin
	@RequestMapping(value = "/unblockUsersService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String unblockUsersService(@RequestParam("jwt") String jwt, @RequestParam("usersVO") String usersVOStr) {
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

		UsersVO usersVO = GsonHelper.getGson().fromJson(usersVOStr, UsersVO.class);

		String result;
		try {
			String hash_user_id = usersVO.getHash_user_id();
			if (hash_user_id == null) {
				json.addProperty("EXCEPTION", true);
				json.addProperty("RESULT", "Null hash user id !");
				return GsonHelper.getGson().toJson(json);
			}
			usersVO.setUsers_id(Long.valueOf(EncryptionUtils.decrypt(hash_user_id, key)));
			result = usersService.unblockUsersService(ctx, usersVO);
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