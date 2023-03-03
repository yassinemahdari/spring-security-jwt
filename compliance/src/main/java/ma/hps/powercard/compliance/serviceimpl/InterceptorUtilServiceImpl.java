package ma.hps.powercard.compliance.serviceimpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.util.ReflectionUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ma.hps.powercard.compliance.domain.BankProperties;
import ma.hps.powercard.compliance.domain.BranchProperties;
import ma.hps.powercard.compliance.serviceapi.BranchVO;
import ma.hps.powercard.compliance.serviceimpl.spec.LoadEntityServiceimpl;
import ma.hps.powercard.dto.ResultEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of InterceptorUtilService.
 */
@Lazy
@Service("interceptorUtilService")
public class InterceptorUtilServiceImpl extends InterceptorUtilServiceImplBase {
	private static Logger logger = Logger.getLogger(InterceptorUtilServiceImpl.class);

	@Autowired
	LoadEntityServiceimpl loadEntityService;

	public InterceptorUtilServiceImpl() {
	}

	public Gson getDeserializer() {

		JsonObject json = new JsonObject();
		JsonSerializer<Date> serializer = new JsonSerializer<Date>() {
			public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				return src == null ? null : new JsonPrimitive(formatter.format(src));
			}
		};
		JsonDeserializer<Date> deserializer = new JsonDeserializer<Date>() {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

			public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
				try {
					return json.getAsString() == null ? null : formatter.parse(json.getAsString());
				} catch (Exception e) {
					logger.trace(e.getMessage());
				}
				return null;
			}
		};
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, serializer);
		gsonBuilder.registerTypeAdapter(Date.class, deserializer);
		Gson gson = gsonBuilder.create();

		return gson;
	}

	public Map<String, Object> loadEntitiesService(ServiceContext ctx, JsonObject criterias) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String sessionID = null;
		String remoteAddress = null;
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:processOperations , USER :" + ctx.getUserId() + " , SessionID :"
					+ sessionID + " , RemoteAddress:" + remoteAddress);
		}

		JsonDeserializer<Date> deserializer = new JsonDeserializer<Date>() {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

			public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
				try {
					return json.getAsString() == null ? null : formatter.parse(json.getAsString());
				} catch (Exception e) {
				}
				return null;
			}
		};
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, deserializer);
		Gson gson = gsonBuilder.create();

		List<CompletableFuture<ResultEntity>> tasks = new ArrayList<>();
		for (Entry<String, JsonElement> entry : criterias.entrySet()) {
			tasks.add(loadEntityService.loadEntity(entry, gson, ctx));
		}
		
		CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
		
		for(CompletableFuture<ResultEntity> future : tasks) {
			ResultEntity result = future.get();
			if(result.getKey() != null) {
				map.put(result.getKey(), result.getListEntities());
			}
		}

		return map;

	}

	public String processOperationService(ServiceContext ctx, Map<String, Object> criterias) throws Exception {

		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("processOperationService not implemented");

	}

	public Map<String, Object> loadDependenciesService(ServiceContext ctx, Map<String, Object> criterias)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
