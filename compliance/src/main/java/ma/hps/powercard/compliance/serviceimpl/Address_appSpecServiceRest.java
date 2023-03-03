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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import ma.hps.powercard.compliance.serviceapi.Address_appSpecService;
import ma.hps.powercard.compliance.serviceapi.Address_appSpecVO;
import ma.hps.powercard.compliance.serviceimpl.spec.CustomSessionListener;
import ma.hps.powercard.compliance.serviceimpl.spec.RestServiceContextStore;

/**
 * Rest Implementation of Address_appSpecService.
 */
//@Component
//@Path("/compliance/Address_appSpecService")
@RestController
@RequestMapping("/compliance/Address_appSpecService")
public class Address_appSpecServiceRest {
    private static Logger logger =
        Logger.getLogger(Address_appSpecServiceRest.class);
    @Lazy
    @Autowired
    private Address_appSpecService address_appSpecService;

    public Address_appSpecServiceRest() {
    }

//    @javax.ws.rs.POST
//    @Path("/processAddress_appSpecService")
    @CrossOrigin
	@RequestMapping(value="/processAddress_appSpecService", method=RequestMethod.POST, produces="text/plain")
	@ResponseBody
    public String processAddress_appSpecService(
		@RequestParam("jwt")
    	String jwt, 
    	@RequestParam("list_address_appSpecVO")
    	String list_address_appSpecVOStr) {

    	JsonObject json = new JsonObject();
        JsonSerializer<Date> serializer =
            new JsonSerializer<Date>() {
                public JsonElement serialize(Date src, Type typeOfSrc,
                    JsonSerializationContext context) {
                    SimpleDateFormat formatter =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    return src == null ? null
                                       : new JsonPrimitive(formatter.format(src));
                }
            };
        JsonDeserializer<Date> deserializer =
            new JsonDeserializer<Date>() {
                SimpleDateFormat formatter =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                public Date deserialize(JsonElement json, Type typeOfT,
                    JsonDeserializationContext context) {
                    try {
                        return json.getAsString() == null ? null
                                                          : formatter.parse(json.getAsString());
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
        HttpSession session = CustomSessionListener.sessions.get(jwt);
        if (session == null) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", "Session expired !");
            return gson.toJson(json);
        }
        RestServiceContextStore restServiceContext =
            (RestServiceContextStore) (session.getAttribute(
                "RestServiceContext"));
        if (restServiceContext == null) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT",
                "Null RestServiceContextStore is not allowed !");
            return gson.toJson(json);
        }
        ServiceContext ctx = restServiceContext.getServiceContext();
        if (ctx == null) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", "Null ServiceContext is not allowed !");
            return gson.toJson(json);
        }

        ;

        List<Address_appSpecVO> list_address_appSpecVO =
            gson.fromJson(list_address_appSpecVOStr,
                new TypeToken<List<Address_appSpecVO>>() {
                }.getType());

        List<Address_appSpecVO> result;
        try {
            gson = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            result = address_appSpecService.processAddress_appSpecService(ctx, list_address_appSpecVO);
            json.addProperty("EXCEPTION", false);
            json.addProperty("RESULT", gson.toJson(result));
        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        return gson.toJson(json);

    }
}
