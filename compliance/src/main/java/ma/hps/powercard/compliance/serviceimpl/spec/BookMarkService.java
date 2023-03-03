package ma.hps.powercard.compliance.serviceimpl.spec;


import org.apache.log4j.Logger;

import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ma.hps.powercard.compliance.serviceapi.User_contextService;
import ma.hps.powercard.compliance.serviceapi.User_contextVO;
import ma.hps.powercard.compliance.serviceimpl.spec.CustomSessionListener;
import ma.hps.powercard.compliance.serviceimpl.spec.RestServiceContextStore;

@RestController
@RequestMapping("/compliance_boomarkService")
public class BookMarkService {
    private static Logger logger =
        Logger.getLogger(BookMarkService.class);
    @Autowired
    private User_contextService user_contextService;

    public BookMarkService() {
    }


    @CrossOrigin
    @ResponseBody
    @PostMapping(value = "/updateBookMark", produces = MediaType.TEXT_PLAIN_VALUE)
    public String updateUser_contextService(@RequestParam("jwt")
    String jwt, @RequestParam("bookmarks")
    String bookmarks) {
        JsonObject json = new JsonObject();
        JsonSerializer<Date> ser =
            new JsonSerializer<Date>() {
                public JsonElement serialize(Date src, Type typeOfSrc,
                    JsonSerializationContext context) {
                    SimpleDateFormat formatter =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    return src == null ? null
                                       : new JsonPrimitive(formatter.format(src));
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
        RestServiceContextStore restServiceContext =
            (RestServiceContextStore) (session.getAttribute(
                "RestServiceContext"));
        ServiceContext ctx = restServiceContext.getServiceContext();
        if (ctx == null) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", "Null ServiceContext is not allowed !");
            return gson.toJson(json);
        }

        User_contextVO user_contextVO = new User_contextVO();
        user_contextVO.setUser_id(ctx.getUser_id());
        user_contextVO.setContext_key("bookmark");
        List<User_contextVO> results;
        String result;
        try {
			results = user_contextService.searchUser_contextService(ctx,
			        user_contextVO);
		} catch (Exception e1) {
			json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e1.getMessage());
            return gson.toJson(json);
		}
        user_contextVO.setContext_value(bookmarks);
        if (results==null || results.size()==0)
        {
        		
        	try {
                gson = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
                result = user_contextService.createUser_contextService(ctx,
                        user_contextVO);
                json.addProperty("EXCEPTION", false);
                json.addProperty("RESULT", gson.toJson(result));
                return gson.toJson(json);
            } catch (Exception e) {
                json.addProperty("EXCEPTION", true);
                json.addProperty("RESULT", e.getMessage());
                return gson.toJson(json);

            }
        }
        
        try {
            gson = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            result = user_contextService.updateUser_contextService(ctx,
                    user_contextVO);
            json.addProperty("EXCEPTION", false);
            json.addProperty("RESULT", gson.toJson(result));
        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        return gson.toJson(json);

    }


    @CrossOrigin
    @ResponseBody
    @PostMapping(value = "/getBookMarks", produces = MediaType.TEXT_PLAIN_VALUE)
    public String searchUser_contextService(@RequestParam("jwt")
    String jwt) {
        JsonObject json = new JsonObject();
        JsonSerializer<Date> ser =
            new JsonSerializer<Date>() {
                public JsonElement serialize(Date src, Type typeOfSrc,
                    JsonSerializationContext context) {
                    SimpleDateFormat formatter =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    return src == null ? null
                                       : new JsonPrimitive(formatter.format(src));
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
        RestServiceContextStore restServiceContext =
            (RestServiceContextStore) (session.getAttribute(
                "RestServiceContext"));
        ServiceContext ctx = restServiceContext.getServiceContext();
        if (ctx == null) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", "Null ServiceContext is not allowed !");
            return gson.toJson(json);
        }

        User_contextVO user_contextVO = new User_contextVO();
        user_contextVO.setUser_id(ctx.getUser_id());
        user_contextVO.setContext_key("bookmark");

        List<User_contextVO> result;
        try {
            gson = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            result = user_contextService.searchUser_contextService(ctx,
                    user_contextVO);
            json.addProperty("EXCEPTION", false);
            json.addProperty("RESULT", gson.toJson(result));
        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        return gson.toJson(json);
    }
}
