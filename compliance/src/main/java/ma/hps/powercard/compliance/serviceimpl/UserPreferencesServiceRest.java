package ma.hps.powercard.compliance.serviceimpl;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import ma.hps.powercard.compliance.serviceapi.UserPreferencesService;
import ma.hps.powercard.compliance.serviceapi.User_contextService;
import ma.hps.powercard.compliance.serviceapi.User_contextVO;
import ma.hps.powercard.compliance.serviceimpl.spec.CustomSessionListener;
import ma.hps.powercard.compliance.serviceimpl.spec.RestServiceContextStore;

@RestController
@RequestMapping("/compliance/UserPreferencesService")
public class UserPreferencesServiceRest {

	private static Logger logger = Logger.getLogger(UserPreferencesServiceRest.class);

	@Lazy
	@Autowired
	private UserPreferencesService userPreferencesService;

	@Lazy
	@Autowired
	private User_contextService user_contextService;

	public UserPreferencesServiceRest() {
	}

	@CrossOrigin
	@ResponseBody
	@PostMapping(value = "/save", produces = MediaType.TEXT_PLAIN_VALUE)
	public String save(@RequestParam("jwt") String jwt, @RequestParam("userPreferences") String userPreferences) {
		JsonObject json = new JsonObject();
		JsonSerializer<Date> ser = new JsonSerializer<Date>() {
			public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				return src == null ? null : new JsonPrimitive(formatter.format(src));
			}
		};

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, ser);
		Gson gson = gsonBuilder.create();

		HttpSession session = CustomSessionListener.sessions.get(jwt);

		if (session == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Session expired !");
			return gson.toJson(json);
		}

		RestServiceContextStore restServiceContext = (RestServiceContextStore) (session
				.getAttribute("RestServiceContext"));
		ServiceContext ctx = restServiceContext.getServiceContext();

		if (ctx == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null ServiceContext is not allowed !");
			return gson.toJson(json);
		}

		Type listType = new TypeToken<List<User_contextVO>>() {
		}.getType();
		@SuppressWarnings("unchecked")
		List<User_contextVO> entities = (List<User_contextVO>) gson.fromJson(userPreferences, listType);

		try {
			gson = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
			userPreferencesService.saveUserPreferences(ctx, entities);
			json.addProperty("Exception", false);
			json.addProperty("RESULT", gson.toJson(entities));
		} catch (Exception e) {
			logger.error("An error occured while saving the user preferences.", e);
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", gson.toJson(entities));

		}
		return gson.toJson(json);

	}

	@CrossOrigin
	@ResponseBody
	@PostMapping(value = "/getAll", produces = MediaType.TEXT_PLAIN_VALUE)
	public String searchUser_contextService(@RequestParam("jwt") String jwt) {
		JsonObject json = new JsonObject();
		JsonSerializer<Date> ser = new JsonSerializer<Date>() {
			public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				return src == null ? null : new JsonPrimitive(formatter.format(src));
			}
		};

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, ser);
		Gson gson = gsonBuilder.create();

		HttpSession session = CustomSessionListener.sessions.get(jwt);

		if (session == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Session expired !");
			return gson.toJson(json);
		}

		RestServiceContextStore restServiceContext = (RestServiceContextStore) (session
				.getAttribute("RestServiceContext"));
		ServiceContext ctx = restServiceContext.getServiceContext();

		if (ctx == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null ServiceContext is not allowed !");
			return gson.toJson(json);
		}

		User_contextVO user_contextVO = new User_contextVO();
		user_contextVO.setUser_id(ctx.getUser_id());

		List<User_contextVO> result;

		try {
			gson = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
			result = userPreferencesService.searchUserPreferences(ctx, user_contextVO);
			json.addProperty("Exception", false);
			json.addProperty("RESULT", gson.toJson(result));
		} catch (Exception e) {
			logger.error(e.getMessage());
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}

		return gson.toJson(json);
	}

	@CrossOrigin
	@ResponseBody
	@PostMapping(value = "/updateDashboard", produces = MediaType.TEXT_PLAIN_VALUE)
	public String updateUser_contextService(@RequestParam("jwt") String jwt,
			@RequestParam("dashboard") String dashboard) {
		JsonObject json = new JsonObject();
		JsonSerializer<Date> ser = new JsonSerializer<Date>() {
			public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				return src == null ? null : new JsonPrimitive(formatter.format(src));
			}
		};
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, ser);
		Gson gson = gsonBuilder.create();
		HttpSession session = CustomSessionListener.sessions.get(jwt);
		if (session == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Session expired !");
			return gson.toJson(json);
		}
		RestServiceContextStore restServiceContext = (RestServiceContextStore) (session
				.getAttribute("RestServiceContext"));
		ServiceContext ctx = restServiceContext.getServiceContext();
		if (ctx == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null ServiceContext is not allowed !");
			return gson.toJson(json);
		}

		User_contextVO user_contextVO = new User_contextVO();
		user_contextVO.setUser_id(ctx.getUser_id());
		user_contextVO.setContext_key("dashboard");

		try {
			gson = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
			String result;
			// Search based on user id and context key
			List<User_contextVO> dashContext = user_contextService.searchUser_contextService(ctx, user_contextVO);
			// Then, assign value
			user_contextVO.setContext_value(dashboard);
			// If not already exists, create, else update
			if (dashContext == null || dashContext.isEmpty()) {
				result = user_contextService.createUser_contextService(ctx, user_contextVO);
			} else {
				result = user_contextService.updateUser_contextService(ctx, user_contextVO);
			}
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", gson.toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return gson.toJson(json);

	}

}