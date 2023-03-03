package ma.hps.powercard.compliance.serviceimpl;

import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

import ma.hps.powercard.compliance.domain.Mask_config;
import ma.hps.powercard.compliance.domain.Mask_configProperties;
import ma.hps.powercard.compliance.domain.Multi_lang_values;
import ma.hps.powercard.compliance.domain.Multi_lang_valuesProperties;
import ma.hps.powercard.compliance.serviceapi.Mask_chainService;
import ma.hps.powercard.compliance.serviceapi.Mask_chainVO;
import ma.hps.powercard.compliance.serviceapi.Mask_configService;
import ma.hps.powercard.compliance.serviceapi.Mask_configSpecificVO;
import ma.hps.powercard.compliance.serviceapi.Mask_configVO;
import ma.hps.powercard.compliance.serviceapi.Mask_profileService;
import ma.hps.powercard.compliance.serviceapi.Mask_profileVO;
import ma.hps.powercard.compliance.serviceapi.Powercard_globalsService;
import ma.hps.powercard.compliance.serviceapi.Powercard_globalsVO;
import ma.hps.powercard.compliance.serviceimpl.spec.CustomSessionListener;
import ma.hps.powercard.compliance.serviceimpl.spec.RestServiceContextStore;
import ma.hps.powercard.compliance.utils.GsonHelper;
import ma.hps.powercard.compliance.utils.MultiLangHelper;
import weblogic.jdbc.wrapper.Array;

import ma.hps.powercard.annotation.ApiMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Lazy;
/**
 * Rest Implementation of Mask_configService.
 */
@RestController
@RequestMapping("/compliance/Mask_configService")
public class Mask_configServiceRest {
    private static Logger logger =
        Logger.getLogger(Mask_configServiceRest.class);
    @Lazy
    @Autowired
    private Mask_configService mask_configService;

    @Lazy
    @Autowired
    private Mask_profileService mask_profileService;
    
    @Lazy
    @Autowired
    private Mask_chainService mask_chainService;
    
    @Lazy
	@Autowired
	Powercard_globalsService powercard_globalsService;
    
    public Mask_configServiceRest() {
    }

    @ApiMapping(value="/createMask_configService")
    public String createMask_configService(@RequestParam("jwt")
    String jwt, @RequestParam("mask_configSpecificVO")
    String mask_configSpecificVOStr) {
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

        Mask_configSpecificVO mask_configSpecificVO =
                GsonHelper.getGson().fromJson(mask_configSpecificVOStr, Mask_configSpecificVO.class);

      
        try {
        	mask_profileService.createMask_profileService(ctx, mask_configSpecificVO.getMask_profile());
        	mask_chainService.createMask_chainService(ctx, mask_configSpecificVO.getMask_chain());
        	
        	mask_configSpecificVO.getMask_config().forEach(mask_config ->{
        		try {
					 mask_configService.createMask_configService(ctx,
							mask_config);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
        	});
        	
          String result= "0000";
            json.addProperty("EXCEPTION", false);
            json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        
        return GsonHelper.getGson().toJson(json);

    }

    @ApiMapping(value="/updateMask_configService")
    public String updateMask_configService(@RequestParam("jwt")
    String jwt, @RequestParam("mask_configSpecificVO")
    String mask_configSpecificVOStr) {
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

        Mask_configSpecificVO mask_configSpecificVO =
                GsonHelper.getGson().fromJson(mask_configSpecificVOStr, Mask_configSpecificVO.class);
        
        
        try {
        	
        	List<Mask_chainVO> listMaskChain = new ArrayList<Mask_chainVO>();
           	Mask_chainVO maskChain = new Mask_chainVO();
           	maskChain.setId_mask_chain(mask_configSpecificVO.getMask_chain().getId_mask_chain());
           	listMaskChain.add(maskChain);
           	mask_configSpecificVO.getMask_profile().setMask_chain_col(listMaskChain);
        
        	mask_profileService.updateMask_profileService(ctx, mask_configSpecificVO.getMask_profile());
        	mask_chainService.updateMask_chainService(ctx, mask_configSpecificVO.getMask_chain());
        
        	
        	Mask_configVO mask_config_criterias = new Mask_configVO();
        	mask_config_criterias.setMask_profile_fk( mask_configSpecificVO.getMask_profile().getId_mask_profile());
        	
        	List<Mask_configVO> searchResult = mask_configService.searchMask_configService(ctx, mask_config_criterias);
        	
        	searchResult.forEach(entity -> {
        		 try {
					mask_configService.deleteMask_configService(ctx, entity);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	});
        	
        	mask_configSpecificVO.getMask_config().forEach(mask_config ->{
        		try {
					 mask_configService.createMask_configService(ctx,
							mask_config);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
        	});

            String result ="0000";
 
            json.addProperty("EXCEPTION", false);
            json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        return GsonHelper.getGson().toJson(json);

    }

    @ApiMapping(value="/updateDefault_maskService")
    public String updateSefault_maskService(@RequestParam("jwt")
    String jwt, @RequestParam("mask_configSpecificVO")
    String mask_configSpecificVOStr) {
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

        Mask_configSpecificVO mask_configSpecificVO =
                GsonHelper.getGson().fromJson(mask_configSpecificVOStr, Mask_configSpecificVO.class);
        
        
        try {
        	
        	if(mask_configSpecificVO.getDefault_mask() != null) {
        		
        		Powercard_globalsVO powercard_globalsVO = new Powercard_globalsVO();
        		powercard_globalsVO.setVariable_name(("ENABLE_DEFAULT_MASK"));
        		powercard_globalsVO.setVariable_value(mask_configSpecificVO.getDefault_mask());
        		
        	    powercard_globalsService.updatePowercard_globalsService(ctx, powercard_globalsVO);
        		ctx.setProperty("enable_default_mask", mask_configSpecificVO.getDefault_mask());

               	mask_chainService.updateMask_chainService(ctx, mask_configSpecificVO.getMask_chain());
                String result ="0000";
                
                json.addProperty("EXCEPTION", false);
                json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
        		
        	}
   

        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        return GsonHelper.getGson().toJson(json);

    }

    @ApiMapping(value="/deleteMask_configService")
    public String deleteMask_configService(@RequestParam("jwt")
    String jwt, @RequestParam("mask_configSpecificVO")
    String mask_configSpecificVOStr) {
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

        Mask_configSpecificVO mask_configSpecificVO =
                GsonHelper.getGson().fromJson(mask_configSpecificVOStr, Mask_configSpecificVO.class);

        String result;
        try {
        	
        	mask_chainService.deleteMask_chainService(ctx, mask_configSpecificVO.getMask_chain());
        
        	mask_configSpecificVO.getMask_config().forEach(mask_config ->{
        		try {
					 mask_configService.deleteMask_configService(ctx,
							mask_config);
				} catch (Exception e) {
					
					e.printStackTrace();
				}  
        	});
        	mask_profileService.deleteMask_profileService(ctx, mask_configSpecificVO.getMask_profile());
        	
            result= "0000";
            json.addProperty("EXCEPTION", false);
            json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        return GsonHelper.getGson().toJson(json);

    }

    @ApiMapping(value="/getAllMask_configService")
    public String getAllMask_configService(@RequestParam("jwt")
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

        List<Mask_configVO> result;
        try {
            result = mask_configService.getAllMask_configService(ctx);
            json.addProperty("EXCEPTION", false);
            json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        return GsonHelper.getGson().toJson(json);

    }

    @ApiMapping(value="/searchMask_configService")
    public String searchMask_configService(@RequestParam("jwt")
    String jwt, @RequestParam("mask_configVO")
    String mask_configVOStr) {
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

        Mask_configVO mask_configVO =
            GsonHelper.getGson().fromJson(mask_configVOStr, Mask_configVO.class);

        List<Mask_configVO> result;
        try {
            result = mask_configService.searchMask_configService(ctx,
                    mask_configVO);
            json.addProperty("EXCEPTION", false);
            json.addProperty("RESULT", GsonHelper.getGson().toJson(result));
        } catch (Exception e) {
            json.addProperty("EXCEPTION", true);
            json.addProperty("RESULT", e.getMessage());

        }
        return GsonHelper.getGson().toJson(json);

    }

    public String applyMask(ServiceContext ctx, String table,
        String columnName, String columnValue) throws Exception {

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("applyMask not implemented");

    }
}
