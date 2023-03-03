package ma.hps.powercard.compliance.serviceimpl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;

import ma.hps.powercard.annotation.ApiMapping;
import ma.hps.powercard.compliance.serviceapi.McOperationParVO;
import ma.hps.powercard.compliance.serviceapi.McOperationService;
import ma.hps.powercard.compliance.serviceapi.McOperationVO;
import ma.hps.powercard.compliance.serviceimpl.spec.CustomSessionListener;
import ma.hps.powercard.compliance.serviceimpl.spec.RestServiceContextStore;
import ma.hps.powercard.compliance.utils.GsonHelper;
import org.springframework.context.annotation.Lazy;
/**
 * Rest Implementation of McOperationService.
 */
//@Component
//@Path("/compliance/McOperationService")
@RestController
@RequestMapping("/compliance/McOperationService")
public class McOperationServiceRest {
	private static Logger logger = Logger.getLogger(McOperationServiceRest.class);
	@Lazy
	@Autowired
	private McOperationService mcOperationService;
	
	@Autowired HttpServletRequest request;

	public McOperationServiceRest() {
	}

	//	@javax.ws.rs.POST
	//	@Path("/createMcOperationService")
	@CrossOrigin
	@RequestMapping(value="/createMcOperationService", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
	public String createMcOperationService(@RequestParam("jwt") String jwt,
			@RequestParam("mcOperationVO") String mcOperationVOStr) {
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

		McOperationVO mcOperationVO = GsonHelper.getGson().fromJson(mcOperationVOStr, McOperationVO.class);

		String result;
		try {
			result = mcOperationService.createMcOperationService(ctx, mcOperationVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

  	@CrossOrigin
	@RequestMapping(value="/checkMcParDependencyService", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
	public String checkMcParDependencyService(@RequestParam("jwt") String jwt,
			@RequestParam("mcOperationVO") String mcOperationVOStr) {
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

		McOperationVO mcOperationVO = GsonHelper.getGson().fromJson(mcOperationVOStr, McOperationVO.class);

		String result;
		try {
			result = mcOperationService.checkMcParDependencyService(ctx, mcOperationVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}
	//	@javax.ws.rs.POST
	//	@Path("/updateMcOperationService")
	@CrossOrigin
	@RequestMapping(value="/updateMcOperationService", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
	public String updateMcOperationService(@RequestParam("jwt") String jwt,
			@RequestParam("mcOperationVO") String mcOperationVOStr) {
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

		McOperationVO mcOperationVO = GsonHelper.getGson().fromJson(mcOperationVOStr, McOperationVO.class);

		String result;
		try {
			result = mcOperationService.updateMcOperationService(ctx, mcOperationVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	//	@javax.ws.rs.POST
	//	@Path("/declineMcOperationService")
	@CrossOrigin
	@RequestMapping(value="/declineMcOperationService", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
	public String declineMcOperationService(@RequestParam("jwt") String jwt,
			@RequestParam("mcOperationVO") String mcOperationVOStr) {
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

		McOperationVO mcOperationVO = GsonHelper.getGson().fromJson(mcOperationVOStr, McOperationVO.class);

		String result;
		try {
			result = mcOperationService.declineMcOperationService(ctx, mcOperationVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	//	@javax.ws.rs.POST
	//	@Path("/acceptMcOperationService")
	@CrossOrigin
	@RequestMapping(value="/acceptMcOperationService", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
	public String acceptMcOperationService(@RequestParam("jwt") String jwt,
			@RequestParam("mcOperationVO") String mcOperationVOStr) {
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

		McOperationVO mcOperationVO = GsonHelper.getGson().fromJson(mcOperationVOStr, McOperationVO.class);

		String result;
		try {
			result = mcOperationService.acceptMcOperationService(ctx, mcOperationVO, jwt, getURLWithContextPath(request));
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());
		}
		return GsonHelper.getGson().toJson(json);

	}

	//	@javax.ws.rs.POST
	//	@Path("/checkMcOperationService")
	@CrossOrigin
	@RequestMapping(value="/checkMcOperationService", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
	public String checkMcOperationService(@RequestParam("jwt") String jwt,
			@RequestParam("mcOperationVO") String mcOperationVOStr) {
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

		McOperationVO mcOperationVO = GsonHelper.getGson().fromJson(mcOperationVOStr, McOperationVO.class);

		String result;
		try {
			result = mcOperationService.checkMcOperationService(ctx, mcOperationVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	//	@javax.ws.rs.POST
	//	@Path("/deleteMcOperationService")
	@CrossOrigin
	@RequestMapping(value="/deleteMcOperationService", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
	public String deleteMcOperationService(@RequestParam("jwt") String jwt,
			@RequestParam("mcOperationVO") String mcOperationVOStr) {
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

		McOperationVO mcOperationVO = GsonHelper.getGson().fromJson(mcOperationVOStr, McOperationVO.class);

		String result;
		try {
			result = mcOperationService.deleteMcOperationService(ctx, mcOperationVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	//	@javax.ws.rs.POST
	//	@Path("/getAllMcOperationService")
	@CrossOrigin
	@RequestMapping(value="/getAllMcOperationService", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
	public String getAllMcOperationService(@RequestParam("jwt") String jwt) {
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

		List<McOperationVO> result;
		try {
			result = mcOperationService.getAllMcOperationService(ctx);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	//	@javax.ws.rs.POST
	//	@Path("/searchMcOperationService")
	@CrossOrigin
	@RequestMapping(value="/searchMcOperationService", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
	public String searchMcOperationService(@RequestParam("jwt") String jwt,
			@RequestParam("mcOperationVO") String mcOperationVOStr) {
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

		McOperationVO mcOperationVO = GsonHelper.getGson().fromJson(mcOperationVOStr, McOperationVO.class);

		List<McOperationVO> result;
		try {
			result = mcOperationService.searchMcOperationService(ctx, mcOperationVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}

	//	@javax.ws.rs.POST
	//	@Path("/getCheckableOperationService")
	@CrossOrigin
	@RequestMapping(value="/getCheckableOperationService", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
	public String getCheckableOperationService(@RequestParam("jwt") String jwt,
			@RequestParam("mcOperationVO") String mcOperationVOStr) {

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

		McOperationVO mcOperationVO = GsonHelper.getGson().fromJson(mcOperationVOStr, McOperationVO.class);

		List<McOperationVO> result;
		try {
			result = mcOperationService.getCheckableOperationService(ctx, mcOperationVO);
			json.addProperty("EXCEPTION", false);
			json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
		} catch (Exception e) {
			json.addProperty("EXCEPTION", true);
			json.addProperty("RESULT", e.getMessage());

		}
		return GsonHelper.getGson().toJson(json);

	}
	
	
	@ApiMapping(value="/getObjMcOperationService")
    public String getObjMcOperationService(@RequestParam("jwt") String jwt, @RequestParam("obj") String obj) throws Exception {    	
	
    	JsonObject json = new JsonObject();

        HttpSession session = CustomSessionListener.sessions.get(jwt);
        if (session == null) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", "Session expired !");
            return GsonHelper.getGson().toJson(json);
        }
        RestServiceContextStore restServiceContext =
            (RestServiceContextStore) (session.getAttribute(
                "RestServiceContext"));
        if (restServiceContext == null) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT",
                "Null RestServiceContextStore is not allowed !");
            return GsonHelper.getGson().toJson(json);
        }
        ServiceContext ctx = restServiceContext.getServiceContext();
        if (ctx == null) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", "Null ServiceContext is not allowed !");
            return GsonHelper.getGson().toJson(json);
        };

        List<McOperationVO> result;
        try {
            result = mcOperationService.getObjMcOperationService(ctx, obj);
            json.addProperty("EXCEPTION", false);
            json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        return GsonHelper.getGson().toJson(json);
    }
	
	public static String getURLWithContextPath(HttpServletRequest request) {
	   return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + request.getServletPath() + "/";
	}
	
}
