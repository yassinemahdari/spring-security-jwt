package ma.hps.powercard.compliance.serviceimpl;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ma.hps.powercard.compliance.serviceapi.LoginService;
import ma.hps.powercard.compliance.serviceapi.ScreenInfosVO;
import ma.hps.powercard.compliance.serviceimpl.spec.CustomSessionListener;
import ma.hps.powercard.compliance.serviceimpl.spec.RestServiceContextStore;

/**
 * Rest Implementation of LoginService.
 */
// @Component
// @Path("/compliance/LoginService")
@RestController
@RequestMapping("/compliance/LoginService")
public class LoginServiceRest {

	@Lazy
	@Autowired
	private LoginService loginService;

	public LoginServiceRest() {
	}

	private static Logger logger = Logger.getLogger(LoginServiceRest.class);

	// @javax.ws.rs.POST
	// @Path("/retrieveScreenInfos")
	@CrossOrigin
	@RequestMapping(value = "/retrieveScreenInfos", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String retrieveScreenInfos(@RequestParam("jwt") String jwt,
			@RequestParam("screenInfosVO") String screenInfosVOStr) {
		// TODO Auto-generated method stub
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

		if (restServiceContext == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null RestServiceContextStore is not allowed !");
			return gson.toJson(json);
		}

		ServiceContext ctx = restServiceContext.getServiceContext();
		if (ctx == null) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", "Null ServiceContext is not allowed !");
			return gson.toJson(json);
		}
		;

		ScreenInfosVO screenInfosVO = gson.fromJson(screenInfosVOStr, ScreenInfosVO.class);

		Map<String, Object> result;
		try {
			gson = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
			result = loginService.retrieveScreenInfos(ctx, screenInfosVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", gson.toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error("Error processing");

		}
		return gson.toJson(json);
	}

	public Long haveOperationToCheck(ServiceContext ctx) throws Exception {

		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("haveOperationToCheck not implemented");

	}
}
