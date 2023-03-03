package ma.hps.powercard.compliance.serviceimpl;

import org.apache.log4j.Logger;

import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import ma.hps.powercard.compliance.serviceapi.Accounts_linkSpecVO;
import ma.hps.powercard.compliance.serviceapi.Accounts_linkVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Implementation222 of Accounts_linkSpecService.
 */
@Lazy
@Service("accounts_linkSpecService")
public class Accounts_linkSpecServiceImpl
    extends Accounts_linkSpecServiceImplBase {
        private static Logger logger = Logger.getLogger(Accounts_linkSpecServiceImpl.class);

        private static final String UPDATE = "U";
        private static final String CREATE = "C";
        private static final String DELETE = "D";
    
        public Accounts_linkSpecServiceImpl() {
        }
    
        public String processAccounts_linkSpecService(ServiceContext ctx,
                List<Accounts_linkSpecVO> list_accounts_linkSpecVO) throws Exception {
    
            String sessionID = null;
            String remoteAddress = null;
            if (ctx != null) {
                if (ctx.getDetails() != null) {
                    sessionID = ctx.getDetails().getSessionId();
                    remoteAddress = ctx.getDetails().getRemoteAddress();
                }
    
                logger.info("PowerCardV3 : Operation:processAccounts_linkSpecService , USER :" + ctx.getUserId()
                        + " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
            }
    
            ServiceContextStore.set(ctx);
    
            for (Accounts_linkSpecVO additional_fields_specVO : list_accounts_linkSpecVO) {
    
                Accounts_linkVO accounts_linkVOVO = additional_fields_specVO
                        .getRef_accounts_link();
                
                String operation = additional_fields_specVO.getOperation();
    
                if (DELETE.equals(operation)) {
                    if (accounts_linkVOVO != null)
                        this.getAccounts_linkService()
                            .deleteAccounts_linkService(ctx, accounts_linkVOVO);
    
                } else if (CREATE.equals(operation)) {
                    if (accounts_linkVOVO != null)
                        this.getAccounts_linkService()
                            .createAccounts_linkService(ctx, accounts_linkVOVO);
    
                } else if (UPDATE.equals(operation)) {
                    if (accounts_linkVOVO != null)
                        this.getAccounts_linkService()
                            .updateAccounts_linkService(ctx, accounts_linkVOVO);
    
                }
            }
            return "0000";
    
        }
}
