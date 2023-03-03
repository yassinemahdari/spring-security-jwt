package ma.hps.powercard.compliance.serviceimpl;

import org.apache.log4j.Logger;

import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;


import ma.hps.powercard.compliance.serviceapi.Address_appSpecVO;
import ma.hps.powercard.compliance.serviceapi.Address_details_iss_appVO;
import ma.hps.powercard.compliance.serviceapi.Address_list_iss_appVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Implementation222 of Address_appSpecService.
 */
@Lazy
@Service("address_appSpecService")
public class Address_appSpecServiceImpl extends Address_appSpecServiceImplBase {
    private static Logger logger =
        Logger.getLogger(Address_appSpecServiceImpl.class);
	
    private static final String UPDATE="U";
	private static final String CREATE="C";
	private static final String DELETE="D";
    
	public Address_appSpecServiceImpl() {
    }

    public List<Address_appSpecVO> processAddress_appSpecService(ServiceContext ctx,
        List<Address_appSpecVO> list_address_appSpecVO) throws Exception {
    	String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:createAddress_list_iss_appService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }
        
        ServiceContextStore.set(ctx);
        
        for(Address_appSpecVO address_appSpecVO : list_address_appSpecVO) {
        	 
        	Address_details_iss_appVO address_details_iss_appVO = address_appSpecVO.getRef_address_details_app();
        	Address_list_iss_appVO address_list_iss_appVO = address_appSpecVO.getRef_address_list_app();
        	String operation = address_appSpecVO.getOperation();
        	String detailsId = null;
        	 
        	if(DELETE.equals(operation)) {
        		if(address_details_iss_appVO != null) {
        			this.getAddress_details_iss_appService().deleteAddress_details_iss_appService(ctx, address_details_iss_appVO);
        		}
        		if(address_list_iss_appVO != null) {
        			this.getAddress_list_iss_appService().deleteAddress_list_iss_appService(ctx, address_list_iss_appVO);
        		}
        	}else if(CREATE.equals(operation)) {
        		if(address_details_iss_appVO != null)
        			detailsId = this.getAddress_details_iss_appService().createAddress_details_iss_appService(ctx, address_details_iss_appVO);
					address_details_iss_appVO.setDetails_id(detailsId);
        		if(address_list_iss_appVO != null) {
        			address_list_iss_appVO.setAddress_id(detailsId);
			 		this.getAddress_list_iss_appService().createAddress_list_iss_appService(ctx, address_list_iss_appVO);
        		}
        	}else if(UPDATE.equals(operation)) {
        		if(address_details_iss_appVO != null) {
        			this.getAddress_details_iss_appService().updateAddress_details_iss_appService(ctx, address_details_iss_appVO);
        		}
        		if(address_list_iss_appVO != null) {
        			this.getAddress_list_iss_appService().updateAddress_list_iss_appService(ctx, address_list_iss_appVO);
        		}
        	}
        }
        return list_address_appSpecVO;
    }
}
