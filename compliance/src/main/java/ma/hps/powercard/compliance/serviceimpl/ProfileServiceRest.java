package ma.hps.powercard.compliance.serviceimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import ma.hps.powercard.compliance.serviceapi.BankVO;
import ma.hps.powercard.compliance.serviceapi.ProfileService;
import ma.hps.powercard.compliance.serviceapi.ProfileSpecificVO;
import ma.hps.powercard.compliance.serviceapi.ProfileVO;
import ma.hps.powercard.compliance.serviceimpl.spec.CustomSessionListener;
import ma.hps.powercard.compliance.serviceimpl.spec.RestServiceContextStore;
import ma.hps.powercard.compliance.utils.GsonHelper;

/**
 * Rest Implementation of ProfileService.
 */
// @Component
// @Path("/compliance/ProfileService")
@RestController
@RequestMapping("/compliance/ProfileService")
public class ProfileServiceRest {
	private static Logger logger = Logger.getLogger(ProfileServiceRest.class);

	@Lazy
	@Autowired
	private ProfileService profileService;

	public ProfileServiceRest() {
	}

	// @javax.ws.rs.POST
	// @Path("/createProfileService")
	@CrossOrigin
	@RequestMapping(value = "/createProfileService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String createProfileService(@RequestParam("jwt") String jwt,
			@RequestParam("profileVO") String profileVOStr) {
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

		ProfileVO profileVO = GsonHelper.getGson().fromJson(profileVOStr, ProfileVO.class);

		String result = null;
		try {

			ProfileSpecificVO profileSpecificVOIter = GsonHelper.getGson().fromJson(profileVOStr,
					ProfileSpecificVO.class);

			Collection<BankVO> listofBanks = profileSpecificVOIter.getListofBanks();

			if (listofBanks != null) {
				List<JsonObject> bank_codes = listofBanks.stream().map(x -> {
					JsonObject object = new JsonObject();
					object.addProperty("bank_code", x.getBank_code());
					return object;
				}).collect(Collectors.toList());

				profileVO.setBank_data_access(GsonHelper.getGson().toJson(bank_codes));
			}

			result = profileService.createProfileService(ctx, profileVO);

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
	// @Path("/updateProfileService")
	@CrossOrigin
	@RequestMapping(value = "/updateProfileService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String updateProfileService(@RequestParam("jwt") String jwt,
			@RequestParam("profileVO") String profileVOStr) {
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

		ProfileVO profileVO = GsonHelper.getGson().fromJson(profileVOStr, ProfileVO.class);

		String result = null;
		try {

			ProfileSpecificVO profileSpecificVOIter = GsonHelper.getGson().fromJson(profileVOStr,
					ProfileSpecificVO.class);

			Collection<BankVO> listofBanks = profileSpecificVOIter.getListofBanks();

			if (listofBanks != null) {

				List<JsonObject> bank_codes = listofBanks.stream().map(x -> {
					JsonObject object = new JsonObject();
					object.addProperty("bank_code", x.getBank_code());
					return object;
				}).collect(Collectors.toList());

				profileVO.setBank_data_access(GsonHelper.getGson().toJson(bank_codes));
			}

			result = profileService.updateProfileService(ctx, profileVO);

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
	// @Path("/deleteProfileService")
	@CrossOrigin
	@RequestMapping(value = "/deleteProfileService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String deleteProfileService(@RequestParam("jwt") String jwt,
			@RequestParam("profileVO") String profileVOStr) {
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

		ProfileVO profileVO = GsonHelper.getGson().fromJson(profileVOStr, ProfileVO.class);

		String result;
		try {
			result = profileService.deleteProfileService(ctx, profileVO);
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
	// @Path("/searchProfileService")
	@CrossOrigin
	@RequestMapping(value = "/searchProfileService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String searchProfileService(@RequestParam("jwt") String jwt,
			@RequestParam("profileVO") String profileVOStr) {
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
		ProfileVO profileVO = GsonHelper.getGson().fromJson(profileVOStr, ProfileVO.class);
		List<ProfileVO> result;
		List<ProfileSpecificVO> resultSpec = new ArrayList<>();
		try {
			result = profileService.searchProfileService(ctx, profileVO);

			for (ProfileVO profileIter : result) {

				ProfileSpecificVO profileSpecificVOIter = new ProfileSpecificVO();

				BeanUtils.copyProperties(profileIter, profileSpecificVOIter);

				List<BankVO> tmp = GsonHelper.getGson().fromJson(profileIter.getBank_data_access(),
						new TypeToken<List<BankVO>>() {
						}.getType());

				profileSpecificVOIter.setListofBanks(tmp);

				resultSpec.add(profileSpecificVOIter);
			}
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(resultSpec));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
			logger.error(e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	// @javax.ws.rs.POST
	// @Path("/getAllProfileService")
	// public String getAllProfileService(@FormParam("jwt") String jwt) {
	@CrossOrigin
	@RequestMapping(value = "/getAllProfileService", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String getAllProfileService(@RequestParam("jwt") String jwt) {
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

		List<ProfileVO> result;
		try {
			result = profileService.getAllProfileService(ctx);
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
	@RequestMapping(value = "/updateDataAccessProfile", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String updateDataAccessProfile(@RequestParam("jwt") String jwt,
			@RequestParam("profileVO") String profileVOStr) {
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

		ProfileVO profileVO = GsonHelper.getGson().fromJson(profileVOStr, ProfileVO.class);

		String result = null;
		try {

			ProfileSpecificVO profileSpecificVOIter = GsonHelper.getGson().fromJson(profileVOStr,
					ProfileSpecificVO.class);

			Collection<BankVO> listofBanks = profileSpecificVOIter.getListofBanks();

			if (listofBanks != null) {

				List<JsonObject> bank_codes = listofBanks.stream().map(x -> {
					JsonObject object = new JsonObject();
					object.addProperty("bank_code", x.getBank_code());
					return object;
				}).collect(Collectors.toList());

				profileVO.setBank_data_access(GsonHelper.getGson().toJson(bank_codes));
			}

			result = profileService.updateDataAccessProfile(ctx, profileVO);

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
