package ma.hps.powercard.compliance.serviceimpl;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

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
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ma.hps.powercard.annotation.ApiMapping;
import ma.hps.powercard.compliance.domain.Ressource_bundleRepository;
import ma.hps.powercard.compliance.serviceapi.Multi_lang_valuesService;
import ma.hps.powercard.compliance.serviceapi.Ressource_bundleService;
import ma.hps.powercard.compliance.serviceapi.Ressource_bundleVO;
import ma.hps.powercard.compliance.serviceapi.StaticListType;
import ma.hps.powercard.compliance.serviceapi.Static_listService;
import ma.hps.powercard.compliance.serviceapi.Static_listVO;
import ma.hps.powercard.compliance.serviceapi.TranslationType;
import ma.hps.powercard.compliance.serviceimpl.spec.CustomSessionListener;
import ma.hps.powercard.compliance.serviceimpl.spec.RestServiceContextStore;
import ma.hps.powercard.compliance.utils.GsonHelper;

/**
 * Rest Implementation of TranslationServiceRest.
 */
@RestController
@RequestMapping("/compliance/TranslationServiceRest")
public class TranslationServiceRest {
	private static Logger logger = Logger.getLogger(TranslationServiceRest.class);
	@Lazy
	@Autowired
	private Ressource_bundleService ressource_bundleService;

	@Lazy
	@Autowired
	private Ressource_bundleRepository ressource_bundleRepository;

	@Lazy
	@Autowired
	private Multi_lang_valuesService multi_lang_valuesService;

	@Lazy
	@Autowired
	private Static_listService static_listService;

	private List<String> cachedRessourceBundle = Arrays.asList("Common", "Menu_lang", "Exceptions", "Component",
			"Error", "Category_list");

	public TranslationServiceRest() {
	}

	@CrossOrigin
	@RequestMapping(value = "/translateBundles", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String translateBundles(@RequestParam("jwt") String jwt,
			@RequestParam("translationVO") String translationVOStr) throws Exception {

		JsonObject json = new JsonObject();
		Gson gson = getDeserializer();
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

		try {
			JsonObject criterias = gson.fromJson(translationVOStr, JsonObject.class);
			Map<String, TranslationType> hashMap = translateBundlesImpl(ctx, criterias);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", gson.toJson(hashMap));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return gson.toJson(json);

	}

	@ApiMapping(value = "/cacheEvictBundles")
	public String cacheEvictBundles(@RequestParam("jwt") String jwt) {
		JsonObject json = new JsonObject();
		HttpSession session = CustomSessionListener.sessions.get(jwt);
		RestServiceContextStore restServiceContext = (RestServiceContextStore) (session
				.getAttribute("RestServiceContext"));
		ServiceContext ctx = restServiceContext.getServiceContext();
		String result = null;
		try {
			result = ressource_bundleService.cacheEvictBundles(ctx);

			result = multi_lang_valuesService.cacheEvict(ctx);

			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", result);
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());
		}
		return GsonHelper.getGson().toJson(json);
	}

	@Transactional
	public Map<String, TranslationType> translateBundlesImpl(ServiceContext ctx, JsonObject criterias)
			throws Exception {

		String locale_chain = criterias.get("locale_chain").getAsString();

		Set<String> bundles = StreamSupport.stream(criterias.getAsJsonArray("bundles").spliterator(), false)
				.map(JsonElement::getAsString).collect(Collectors.toSet());

		if (bundles.size() == 0)
			return Collections.emptyMap();

		return this.ressource_bundleService.searchByBundleNamesAndLocaleChain(ctx, bundles, locale_chain).stream()
				.collect(Collectors.toMap(Ressource_bundleVO::getKey_val,
						bundle -> new TranslationType(bundle.getShort_value(), bundle.getValue())));
	}

	@CrossOrigin
	@RequestMapping(value = "/translateStaticList", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String retreiveStaticList(@RequestParam("jwt") String jwt,
			@RequestParam("staticlistVO") String translationVOStr) throws Exception {
		JsonObject json = new JsonObject();
		Gson gson = getDeserializer();
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

		try {

			JsonObject criterias = gson.fromJson(translationVOStr, JsonObject.class);
			HashMap<String, List<StaticListType>> hashMap = retreiveStaticListImpl(ctx, criterias);

			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", gson.toJson(hashMap));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return gson.toJson(json);

	}

	public HashMap<String, List<StaticListType>> retreiveStaticListImpl(ServiceContext ctx, JsonObject criterias)
			throws Exception {

		JsonArray o = criterias.getAsJsonArray("collections");
		String locale_chain = criterias.get("locale_chain").getAsString();
		String bundle = "Static_List";// criterias.get("bundle").getAsString();
		HashMap<String, List<StaticListType>> hashMap = new HashMap<String, List<StaticListType>>();
		Static_listVO static_listVO = new Static_listVO();
		Ressource_bundleVO ressource_bundleVO = new Ressource_bundleVO();
		ressource_bundleVO.setBundle(bundle);
		ressource_bundleVO.setLocale_chain(locale_chain);

		List<String> inStaticList = new ArrayList<String>();

		for (JsonElement jsonElement : o) {
			inStaticList.add(jsonElement.getAsString());
		}
		static_listVO.setInNameCollection(inStaticList);

		List<Static_listVO> slList = static_listService.searchStatic_listService(ctx, static_listVO);

		List<String> inKeys = new ArrayList<String>();
		for (Static_listVO slVO : slList) {

			inKeys.add(slVO.getValue_label());

		}
		ressource_bundleVO.setInKeys(inKeys);

		List<Ressource_bundleVO> rbList = ressource_bundleService.searchRessource_bundleService(ctx,
				ressource_bundleVO);

		for (JsonElement jsonElement : o) {

			List<StaticListType> translatedRes = new ArrayList<StaticListType>();

			List<Static_listVO> staticListEntries = searchStatic_list(jsonElement.getAsString(), slList);

			for (Static_listVO slVO : staticListEntries) {

				Ressource_bundleVO rbVO = searchRB(slVO.getValue_label(), rbList);

				if (rbVO != null)

					translatedRes.add(new StaticListType(slVO.getValue_id(), rbVO.getValue(), rbVO.getShort_value(),
							slVO.getValue_order()));
				else
					translatedRes.add(new StaticListType(slVO.getValue_id(), slVO.getValue_label(),
							slVO.getValue_label(), slVO.getValue_order()));

			}
			Collections.sort(translatedRes, new Comparator<StaticListType>() {
				@Override
				public int compare(StaticListType elm1, StaticListType elm2) {
					return (Integer.parseInt(elm1.getValue_order()) - Integer.parseInt(elm2.getValue_order()));
				}
			});
			hashMap.put(jsonElement.getAsString(), translatedRes);
		}
		return hashMap;
	}

	private Ressource_bundleVO searchRB(String value_label, List<Ressource_bundleVO> rbList) {

		if (rbList != null && !rbList.isEmpty()) {

			for (Ressource_bundleVO vo : rbList) {

				if (vo.getKey_val().equals(value_label))
					return vo;

			}

		}
		return null;
	}

	private List<Static_listVO> searchStatic_list(String collectionName, List<Static_listVO> res) {
		List<Static_listVO> result = new ArrayList<Static_listVO>();
		if (res != null && !res.isEmpty()) {

			for (Static_listVO static_listVO : res) {

				if (static_listVO.getCollection_name().equals(collectionName))
					result.add(static_listVO);

			}

		}
		return result;
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

}
