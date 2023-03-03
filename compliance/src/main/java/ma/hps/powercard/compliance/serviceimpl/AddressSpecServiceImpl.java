package ma.hps.powercard.compliance.serviceimpl;

import org.apache.log4j.Logger;

import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import ma.hps.powercard.compliance.serviceapi.AddressSpecVO;
import ma.hps.powercard.compliance.serviceapi.Address_list_issuingVO;
import ma.hps.powercard.compliance.serviceapi.Address_details_issuingVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**p
 * Implementation222 of AddressSpecService.
 */
@Lazy
@Service("addressSpecService")
public class AddressSpecServiceImpl extends AddressSpecServiceImplBase {
    private static Logger logger =
        Logger.getLogger(AddressSpecServiceImpl.class);
	
    private static final String UPDATE="U";
	private static final String CREATE="C";
	private static final String DELETE="D";
    
	public AddressSpecServiceImpl() {
    }

    public List<AddressSpecVO> processAddressSpecService(ServiceContext ctx,
        List<AddressSpecVO> list_addressSpecVO) throws Exception {
    	String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:createAddress_list_issuingService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }
        
        ServiceContextStore.set(ctx);
        
        for(AddressSpecVO addressSpecVO : list_addressSpecVO) {
        	 
        	Address_details_issuingVO address_details_issuingVO = addressSpecVO.getRef_address_details();
        	Address_list_issuingVO address_list_issuingVO = addressSpecVO.getRef_address_list();
        	String operation = addressSpecVO.getOperation();
        	String detailsId = null;
        	 
        	if(DELETE.equals(operation)) {
        		if(address_details_issuingVO != null) {
        			this.getAddress_details_issuingService().deleteAddress_details_issuingService(ctx, address_details_issuingVO);
        		}
        		if(address_list_issuingVO != null) {
        			this.getAddress_list_issuingService().deleteAddress_list_issuingService(ctx, address_list_issuingVO);
        		}
        	}else if(CREATE.equals(operation)) {
        		if(address_details_issuingVO != null)
        			detailsId = this.getAddress_details_issuingService().createAddress_details_issuingService(ctx, address_details_issuingVO);
					address_details_issuingVO.setDetails_id(detailsId);
        		if(address_list_issuingVO != null) {
        			address_list_issuingVO.setAddress_id(detailsId);
			 		this.getAddress_list_issuingService().createAddress_list_issuingService(ctx, address_list_issuingVO);
        		}
        	}else if(UPDATE.equals(operation)) {
        		if(address_details_issuingVO != null) {
        			this.getAddress_details_issuingService().updateAddress_details_issuingService(ctx, address_details_issuingVO);
        		}
        		if(address_list_issuingVO != null) {
        			this.getAddress_list_issuingService().updateAddress_list_issuingService(ctx, address_list_issuingVO);
        		}
        	}
        }
        return list_addressSpecVO;
    }
}