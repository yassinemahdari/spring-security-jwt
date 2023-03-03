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

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ma.hps.powercard.compliance.serviceapi.InterceptorUtilService;
import ma.hps.powercard.compliance.serviceapi.UsersVO;
import ma.hps.powercard.compliance.serviceimpl.spec.CustomSessionListener;
import ma.hps.powercard.compliance.serviceimpl.spec.RestServiceContextStore;
import ma.hps.powercard.compliance.utils.CustomExclusionStrategy;
import ma.hps.powercard.compliance.utils.GsonHelper;

/**
* Rest Implementation of InterceptorUtilService.
*/
/*******/
// @Component
// @Path("/compliance/InterceptorUtilService")
@RestController
@RequestMapping("/compliance/InterceptorUtilService")
public class InterceptorUtilServiceRest {
	private static Logger logger = Logger.getLogger(InterceptorUtilServiceRest.class);

	@Lazy
	@Autowired
	private InterceptorUtilService interceptorUtilService;

	public InterceptorUtilServiceRest() {
	}

	// @javax.ws.rs.POST
	// @Path("/loadDependencies")
	@CrossOrigin
	@RequestMapping(value = "/loadDependencies", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String loadEntitiesService(@RequestParam("jwt") String jwt, @RequestParam("criterias") String criteriasStr)
			throws Exception {

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

		JsonObject criterias = GsonHelper.getGson().fromJson(criteriasStr, JsonObject.class);
		Map<String, Object> result;
		try {
			result = interceptorUtilService.loadEntitiesService(ctx, criterias);

			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	// @javax.ws.rs.POST
	// @Path("/loadLightDependencies")
	@CrossOrigin
	@RequestMapping(value = "/loadLightDependencies", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String loadLightEntitiesService(@RequestParam("jwt") String jwt,
			@RequestParam("criterias") String criteriasStr, @RequestParam("fields") String fieldsStr) throws Exception {

		JsonObject json = new JsonObject();
		JsonObject jsonResult = new JsonObject();
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

		Map<String, String> fields;
		Map<String, Object> result;
		ExclusionStrategy strategy = new ExclusionStrategy() {
			@Override
			public boolean shouldSkipField(FieldAttributes field) {
				if (field.getDeclaringClass() == UsersVO.class && field.getName().equals("password")) {
					return true;
				}
				return false;
			}

			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				return false;
			}
		};

		try {
			JsonObject criterias = GsonHelper.getGson().fromJson(criteriasStr, JsonObject.class);
			fields = gson.fromJson(fieldsStr, Map.class);
			result = interceptorUtilService.loadEntitiesService(ctx, criterias);
			result.keySet().stream().forEach(key -> {

				Gson gsn = new GsonBuilder().registerTypeAdapter(Date.class, ser)
						.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
						.setExclusionStrategies(
								new CustomExclusionStrategy(fields.containsKey(key) ? fields.get(key) : ""), strategy)
						.create();

				jsonResult.addProperty(key, gsn.toJson(result.get(key)));

			});
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", gson.toJson(jsonResult));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}
		return gson.toJson(json);

	}

	public String processOperationService(ServiceContext ctx, Map<String, Object> criterias) throws Exception {

		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("processOperationService not implemented");

	}
}
