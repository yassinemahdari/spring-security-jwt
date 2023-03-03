package ma.hps.powercard.compliance.serviceimpl;

import org.apache.log4j.Logger;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.domain.PagedResult;
import org.fornax.cartridges.sculptor.framework.domain.PagingParameter;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.Menu;
import ma.hps.powercard.compliance.domain.Profile;
import ma.hps.powercard.compliance.domain.ProfileRepository;
import ma.hps.powercard.compliance.domain.Pwc_screen_servicesProperties;
import ma.hps.powercard.compliance.domain.Pwc_services;
import ma.hps.powercard.compliance.domain.Pwc_servicesProperties;
import ma.hps.powercard.compliance.domain.Screen;
import ma.hps.powercard.compliance.exception.ProfileNotFoundException;
import ma.hps.powercard.compliance.exception.Pwc_servicesNotFoundException;
import ma.hps.powercard.compliance.repositoryimpl.Pwc_servicesUtils;
import ma.hps.powercard.compliance.serviceapi.ProfileService;
import ma.hps.powercard.compliance.serviceapi.Pwc_servicesVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation222 of Pwc_servicesService.
 */
@Lazy
@Service("pwc_servicesService")
public class Pwc_servicesServiceImpl extends Pwc_servicesServiceImplBase {
    private static Logger logger =
        Logger.getLogger(Pwc_servicesServiceImpl.class);


//    @Autowired
//    ProfileService profileService;
//    
    @Autowired
	ProfileRepository profileRepository;
	
    public Pwc_servicesServiceImpl() {
    }

    /**
    * Persist a Pwc_services entity .
    *
    * @param Pwc_servicesVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0024 If ever the object already exists.
    *
    */
    public String createPwc_servicesService(ServiceContext ctx,
        Pwc_servicesVO pwc_servicesVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:createPwc_servicesService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Pwc_services pwc_services = new Pwc_services();

        pwc_services.setModule(pwc_servicesVO.getModule());

        pwc_services.setService(pwc_servicesVO.getService());

        pwc_services.setMethod(pwc_servicesVO.getMethod());

        pwc_services.setCheckable(pwc_servicesVO.getCheckable());

        pwc_services.setCached(pwc_servicesVO.getCached());

        Pwc_services pwc_services1 =
            this.getPwc_servicesRepository().save(pwc_services);

        return "0000";

    }

    /**
    * update a Pwc_services entity .
    *
    * @param Pwc_servicesVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String updatePwc_servicesService(ServiceContext ctx,
        Pwc_servicesVO pwc_servicesVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:updatePwc_servicesService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Pwc_services pwc_services = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(
                Pwc_servicesProperties.pwc_services_id(),
                pwc_servicesVO.getPwc_services_id()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Pwc_services> pagedResult =
            this.getPwc_servicesRepository()
                .findByCondition(con, pagingParameter);
        List<Pwc_services> list = pagedResult.getValues();

        if (list.size() > 0) {
            Pwc_servicesVO oldPwc_servicesVO =
                Pwc_servicesUtils.entityToVO(ctx, list.get(0), 0, 0);

            pwc_services = list.get(0);
        } else {
            throw new OurException("0001", new Pwc_servicesNotFoundException(""));
        }

        if (pwc_servicesVO.getModule() != null) {
            pwc_services.setModule(pwc_servicesVO.getModule());
        }

        if (pwc_servicesVO.getService() != null) {
            pwc_services.setService(pwc_servicesVO.getService());
        }

        if (pwc_servicesVO.getMethod() != null) {
            pwc_services.setMethod(pwc_servicesVO.getMethod());
        }

        if (pwc_servicesVO.getCheckable() != null) {
            pwc_services.setCheckable(pwc_servicesVO.getCheckable());
        }

        if (pwc_servicesVO.getCached() != null) {
            pwc_services.setCached(pwc_servicesVO.getCached());
        }

        Pwc_services pwc_services1 =
            this.getPwc_servicesRepository().save(pwc_services);

        return "0000";

    }

    /**
    * delete a Pwc_services entity .
    *
    * @param Pwc_servicesVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String deletePwc_servicesService(ServiceContext ctx,
        Pwc_servicesVO pwc_servicesVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:deletePwc_servicesService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Pwc_services pwc_services = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(
                Pwc_servicesProperties.pwc_services_id(),
                pwc_servicesVO.getPwc_services_id()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Pwc_services> pagedResult =
            this.getPwc_servicesRepository()
                .findByCondition(con, pagingParameter);
        List<Pwc_services> list = pagedResult.getValues();

        if (list.size() > 0) {
            pwc_services = list.get(0);
        } else {
            throw new OurException("0001", new Pwc_servicesNotFoundException(""));
        }

        this.getPwc_servicesRepository().delete(pwc_services);

        return "0000";

    }

    /**
    *  Find all entities of a specific type .
    *
    * @return List of Pwc_servicesVO
    *
    */
    public List<Pwc_servicesVO> getAllPwc_servicesService(ServiceContext ctx)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:getAllPwc_servicesService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        List<Pwc_servicesVO> l = new ArrayList<Pwc_servicesVO>();

        List<Pwc_services> l_entity = new ArrayList<Pwc_services>();

        l_entity = this.getPwc_servicesRepository().findAll();

        l = Pwc_servicesUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

        return l;

    }

    /**
    * Find entities by conditions
    *
     * @return List of Pwc_servicesVO
    *
    */
    public List<Pwc_servicesVO> searchPwc_servicesService(ServiceContext ctx,
        Pwc_servicesVO pwc_servicesVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:searchPwc_servicesService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (ctx != null) {
            Pwc_servicesUtils.dataFilter(ctx, con);
        }

        Pwc_servicesUtils.setListOfCriteria(ctx, con, pwc_servicesVO);

        List<Pwc_services> l_entity = new ArrayList<Pwc_services>();
        List<Pwc_servicesVO> l = new ArrayList<Pwc_servicesVO>();

        int page = pwc_servicesVO.getPage();
        int pageSize = pwc_servicesVO.getPageSize();
        boolean countTotalPages = true;
        if (page > 0 && pageSize > 0) {
            PagingParameter pagingParameter =
                PagingParameter.pageAccess(pageSize, page, countTotalPages);

            PagedResult<Pwc_services> pagedResult =
                this.getPwc_servicesRepository()
                    .findByCondition(con, pagingParameter);

            l_entity = pagedResult.getValues();

            ctx.setProperty("pagedResult", pagedResult);

        } else {
            l_entity = this.getPwc_servicesRepository().findByCondition(con);
        }

        l = Pwc_servicesUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

        l = Pwc_servicesUtils.filterMultiLang(pwc_servicesVO, l);

        return l;

    }

}