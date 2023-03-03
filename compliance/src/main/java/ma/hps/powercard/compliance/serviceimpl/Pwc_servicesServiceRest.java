package ma.hps.powercard.compliance.serviceimpl;

import java.util.List;

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

import ma.hps.powercard.compliance.serviceapi.Pwc_servicesService;
import ma.hps.powercard.compliance.serviceapi.Pwc_servicesVO;
import ma.hps.powercard.compliance.serviceimpl.spec.CustomSessionListener;
import ma.hps.powercard.compliance.serviceimpl.spec.RestServiceContextStore;
import ma.hps.powercard.compliance.utils.GsonHelper;
import org.springframework.context.annotation.Lazy;
/**
 * Rest Implementation of Pwc_servicesService.
 */
@RestController
@RequestMapping("/compliance/Pwc_servicesService")
public class Pwc_servicesServiceRest {
    private static Logger logger =
        Logger.getLogger(Pwc_servicesServiceRest.class);
    @Lazy
    @Autowired
    private Pwc_servicesService pwc_servicesService;

    public Pwc_servicesServiceRest() {
    }

    @CrossOrigin
    @RequestMapping(value = "/createPwc_servicesService", method = RequestMethod.POST, produces = "text/plain")
    @ResponseBody
    public String createPwc_servicesService(@RequestParam("jwt")
    String jwt, @RequestParam("pwc_servicesVO")
    String pwc_servicesVOStr) {
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
        }

        ;

        Pwc_servicesVO pwc_servicesVO =
            GsonHelper.getGson()
                      .fromJson(pwc_servicesVOStr, Pwc_servicesVO.class);

        String result;
        try {
            result = pwc_servicesService.createPwc_servicesService(ctx,
                    pwc_servicesVO);
            json.addProperty("EXCEPTION", false);
            json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        return GsonHelper.getGson().toJson(json);

    }

    @CrossOrigin
    @RequestMapping(value = "/updatePwc_servicesService", method = RequestMethod.POST, produces = "text/plain")
    @ResponseBody
    public String updatePwc_servicesService(@RequestParam("jwt")
    String jwt, @RequestParam("pwc_servicesVO")
    String pwc_servicesVOStr) {
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
        }

        ;

        Pwc_servicesVO pwc_servicesVO =
            GsonHelper.getGson()
                      .fromJson(pwc_servicesVOStr, Pwc_servicesVO.class);

        String result;
        try {
            result = pwc_servicesService.updatePwc_servicesService(ctx,
                    pwc_servicesVO);
            json.addProperty("EXCEPTION", false);
            json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        return GsonHelper.getGson().toJson(json);

    }

    @CrossOrigin
    @RequestMapping(value = "/deletePwc_servicesService", method = RequestMethod.POST, produces = "text/plain")
    @ResponseBody
    public String deletePwc_servicesService(@RequestParam("jwt")
    String jwt, @RequestParam("pwc_servicesVO")
    String pwc_servicesVOStr) {
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
        }

        ;

        Pwc_servicesVO pwc_servicesVO =
            GsonHelper.getGson()
                      .fromJson(pwc_servicesVOStr, Pwc_servicesVO.class);

        String result;
        try {
            result = pwc_servicesService.deletePwc_servicesService(ctx,
                    pwc_servicesVO);
            json.addProperty("EXCEPTION", false);
            json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        return GsonHelper.getGson().toJson(json);

    }

    @CrossOrigin
    @RequestMapping(value = "/getAllPwc_servicesService", method = RequestMethod.POST, produces = "text/plain")
    @ResponseBody
    public String getAllPwc_servicesService(@RequestParam("jwt")
    String jwt) {
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
        }

        List<Pwc_servicesVO> result;
        try {
            result = pwc_servicesService.getAllPwc_servicesService(ctx);
            json.addProperty("EXCEPTION", false);
            json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        return GsonHelper.getGson().toJson(json);

    }

    @CrossOrigin
    @RequestMapping(value = "/searchPwc_servicesService", method = RequestMethod.POST, produces = "text/plain")
    @ResponseBody
    public String searchPwc_servicesService(@RequestParam("jwt")
    String jwt, @RequestParam("pwc_servicesVO")
    String pwc_servicesVOStr) {
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
        }

        ;

        Pwc_servicesVO pwc_servicesVO =
            GsonHelper.getGson()
                      .fromJson(pwc_servicesVOStr, Pwc_servicesVO.class);

        List<Pwc_servicesVO> result;
        try {
            result = pwc_servicesService.searchPwc_servicesService(ctx,
                    pwc_servicesVO);
            json.addProperty("EXCEPTION", false);
            json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        return GsonHelper.getGson().toJson(json);

    }

    public List<String> getConfiguredServicesForProfile(ServiceContext ctx,
        String profile_id) throws Exception {

		return null;

    }

    public List<String> getCachedPwc_servicesService(ServiceContext ctx)
        throws Exception {

		return null;

    }

    public List<String> getCheckablePwc_servicesService(ServiceContext ctx)
        throws Exception {

		return null;

    }
}
