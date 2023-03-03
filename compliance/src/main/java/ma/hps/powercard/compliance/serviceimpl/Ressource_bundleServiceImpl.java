package ma.hps.powercard.compliance.serviceimpl;

import org.apache.log4j.Logger;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.domain.PagedResult;
import org.fornax.cartridges.sculptor.framework.domain.PagingParameter;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.Ressource_bundle;
import ma.hps.powercard.compliance.domain.Ressource_bundlePK;
import ma.hps.powercard.compliance.domain.Ressource_bundleProperties;
import ma.hps.powercard.compliance.exception.Ressource_bundleNotFoundException;
import ma.hps.powercard.compliance.repositoryimpl.Ressource_bundleUtils;
import ma.hps.powercard.compliance.serviceapi.Ressource_bundleVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;

/**
 * Implementation222 of Ressource_bundleService.
 */
@Lazy
@Service("ressource_bundleService")
public class Ressource_bundleServiceImpl extends Ressource_bundleServiceImplBase {
    private static Logger logger =
        Logger.getLogger(Ressource_bundleServiceImpl.class);

    public Ressource_bundleServiceImpl() {
    }

    /**
    * Persist a Ressource_bundle entity .
    *
    * @param Ressource_bundleVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0024 If ever the object already exists.
    *
    */
    public String createRessource_bundleService(ServiceContext ctx,
        Ressource_bundleVO ressource_bundleVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:createRessource_bundleService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Ressource_bundlePK ressource_bundlePK =
            new Ressource_bundlePK(ressource_bundleVO.getBundle(),
                ressource_bundleVO.getLocale_chain(),
                ressource_bundleVO.getKey_val());

        Ressource_bundle ressource_bundle = null;
        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(Ressource_bundleProperties.ressource_bundlePK()
                                                                    .bundle(),
                ressource_bundleVO.getBundle()));

        con.add(ConditionalCriteria.equal(Ressource_bundleProperties.ressource_bundlePK()
                                                                    .locale_chain(),
                ressource_bundleVO.getLocale_chain()));

        con.add(ConditionalCriteria.equal(Ressource_bundleProperties.ressource_bundlePK()
                                                                    .key_val(),
                ressource_bundleVO.getKey_val()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Ressource_bundle> pagedResult =
            this.getRessource_bundleRepository()
                .findByCondition(con, pagingParameter);
        List<Ressource_bundle> list = pagedResult.getValues();

        if (list.size() > 0) {
            throw new OurException("0024", new Exception(""));
        }
        else {

            ressource_bundle = new Ressource_bundle(ressource_bundlePK);
        }

        ressource_bundle.setValue(ressource_bundleVO.getValue());

        ressource_bundle.setShort_value(ressource_bundleVO.getShort_value());

        //verify : bank data access
        try {
            List<String> banks_access =
                new ArrayList<String>((List<String>) ctx.getProperty(
                        "bankDataAccess"));
            List<String> banks_vb = null;

        } catch (Exception e) {
            if (!e.getMessage().equals("0403")) {
                logger.error("Verifying bank data access error");
                logger.error(e.getMessage());
            } else {
                logger.error("Bank data access restriction");
                throw e;
            }
        }

        Ressource_bundle ressource_bundle1 =
            this.getRessource_bundleRepository().save(ressource_bundle);

        return "0000";

    }

    /**
    * update a Ressource_bundle entity .
    *
    * @param Ressource_bundleVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String updateRessource_bundleService(ServiceContext ctx,
        Ressource_bundleVO ressource_bundleVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:updateRessource_bundleService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Ressource_bundlePK ressource_bundlePK =
            new Ressource_bundlePK(ressource_bundleVO.getBundle(),
                ressource_bundleVO.getLocale_chain(),
                ressource_bundleVO.getKey_val());

        Ressource_bundle ressource_bundle = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(Ressource_bundleProperties.ressource_bundlePK()
                                                                    .bundle(),
                ressource_bundleVO.getBundle()));

        con.add(ConditionalCriteria.equal(Ressource_bundleProperties.ressource_bundlePK()
                                                                    .locale_chain(),
                ressource_bundleVO.getLocale_chain()));

        con.add(ConditionalCriteria.equal(Ressource_bundleProperties.ressource_bundlePK()
                                                                    .key_val(),
                ressource_bundleVO.getKey_val()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Ressource_bundle> pagedResult =
            this.getRessource_bundleRepository()
                .findByCondition(con, pagingParameter);
        List<Ressource_bundle> list = pagedResult.getValues();

        if (list.size() > 0) {
            Ressource_bundleVO oldRessource_bundleVO =
                Ressource_bundleUtils.entityToVO(ctx, list.get(0), 0, 0);


            ressource_bundle = list.get(0);
        } else {
            throw new OurException("0001",
                new Ressource_bundleNotFoundException(""));
        }

        if (ressource_bundleVO.getValue() != null) {
            ressource_bundle.setValue(ressource_bundleVO.getValue());
        }

        if (ressource_bundleVO.getShort_value() != null) {
            ressource_bundle.setShort_value(ressource_bundleVO.getShort_value());
        }

        //verify : bank data access
        try {
            List<String> banks_access =
                new ArrayList<String>((List<String>) ctx.getProperty(
                        "bankDataAccess"));
            List<String> banks_vb = null;

        } catch (Exception e) {
            if (!e.getMessage().equals("0403")) {
                logger.info("Verifying bank data access error");
                logger.error(e.getMessage());
            } else {
                logger.info("Bank data access restriction");
                throw e;
            }
        }

        Ressource_bundle ressource_bundle1 =
            this.getRessource_bundleRepository().save(ressource_bundle);

        return "0000";

    }

    /**
    * delete a Ressource_bundle entity .
    *
    * @param Ressource_bundleVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String deleteRessource_bundleService(ServiceContext ctx,
        Ressource_bundleVO ressource_bundleVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:deleteRessource_bundleService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Ressource_bundlePK ressource_bundlePK =
            new Ressource_bundlePK(ressource_bundleVO.getBundle(),
                ressource_bundleVO.getLocale_chain(),
                ressource_bundleVO.getKey_val());

        Ressource_bundle ressource_bundle = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(Ressource_bundleProperties.ressource_bundlePK()
                                                                    .bundle(),
                ressource_bundleVO.getBundle()));

        con.add(ConditionalCriteria.equal(Ressource_bundleProperties.ressource_bundlePK()
                                                                    .locale_chain(),
                ressource_bundleVO.getLocale_chain()));

        con.add(ConditionalCriteria.equal(Ressource_bundleProperties.ressource_bundlePK()
                                                                    .key_val(),
                ressource_bundleVO.getKey_val()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Ressource_bundle> pagedResult =
            this.getRessource_bundleRepository()
                .findByCondition(con, pagingParameter);
        List<Ressource_bundle> list = pagedResult.getValues();

        if (list.size() > 0) {

            ressource_bundle = list.get(0);
        } else {
            throw new OurException("0001",
                new Ressource_bundleNotFoundException(""));
        }

        //verify : bank data access
        try {
            List<String> banks_access =
                new ArrayList<String>((List<String>) ctx.getProperty(
                        "bankDataAccess"));
            List<String> banks_vb = null;

        } catch (Exception e) {
            if (!e.getMessage().equals("0403")) {
                logger.info("Verifying bank data access error");
                logger.error(e.getMessage());
            } else {
                logger.info("Bank data access restriction");
                throw e;
            }
        }

        this.getRessource_bundleRepository().delete(ressource_bundle);

        return "0000";

    }

    /**
    *  Find all entities of a specific type .
    *
    * @return List of Ressource_bundleVO
    *
    */
    public List<Ressource_bundleVO> getAllRessource_bundleService(
        ServiceContext ctx) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:getAllRessource_bundleService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        List<Ressource_bundleVO> l = new ArrayList<Ressource_bundleVO>();

        List<Ressource_bundle> l_entity = new ArrayList<Ressource_bundle>();

        l_entity = this.getRessource_bundleRepository().findAll();

        l = Ressource_bundleUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

        return l;

    }

    /**
    * Find entities by conditions
    *
     * @return List of Ressource_bundleVO
    *
    */
    public List<Ressource_bundleVO> searchRessource_bundleService(
        ServiceContext ctx, Ressource_bundleVO ressource_bundleVO)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:searchRessource_bundleService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (ctx != null) {
            Ressource_bundleUtils.dataFilter(ctx, con);
        }

        Ressource_bundleUtils.setListOfCriteria(ctx, con, ressource_bundleVO);

        List<Ressource_bundle> l_entity = new ArrayList<Ressource_bundle>();
        List<Ressource_bundleVO> l = new ArrayList<Ressource_bundleVO>();

        int page = ressource_bundleVO.getPage();
        int pageSize = ressource_bundleVO.getPageSize();
        boolean countTotalPages = true;
        if (page > 0 && pageSize > 0) {
            PagingParameter pagingParameter =
                PagingParameter.pageAccess(pageSize, page, countTotalPages);

            PagedResult<Ressource_bundle> pagedResult =
                this.getRessource_bundleRepository()
                    .findByCondition(con, pagingParameter);

            l_entity = pagedResult.getValues();

            ctx.setProperty("pagedResult", pagedResult);

        } else {
            l_entity = this.getRessource_bundleRepository().findByCondition(con);
        }

        l = Ressource_bundleUtils.mapListOfEntitiesToVO(ctx, l_entity, 0,
                ressource_bundleVO.getLazy_level_col());

        l = Ressource_bundleUtils.filterMultiLang(ressource_bundleVO, l);

        return l;

    }

    @Cacheable(value="CachedRessourcebundle", key="#bundle.concat(#locale_chain)")
    public List<Ressource_bundleVO> getCachedRessource_bundle(
        ServiceContext ctx, String bundle, String locale_chain)
        throws Exception {

    	String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:getCachedRessource_bundle , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress+
                " , Bundle:" + bundle);
        }
        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (bundle != null && !bundle.equals("")) {
        	con.add(ConditionalCriteria.equal(Ressource_bundleProperties.ressource_bundlePK().bundle(), bundle));
        }
        if (locale_chain != null && !locale_chain.equals("")) {
        	con.add(ConditionalCriteria.equal(Ressource_bundleProperties.ressource_bundlePK().locale_chain(), locale_chain));
        }

        List<Ressource_bundle> l_entity = new ArrayList<Ressource_bundle>();
        List<Ressource_bundleVO> l = new ArrayList<Ressource_bundleVO>();
        
        l_entity = this.getRessource_bundleRepository().findByCondition(con);

        l = Ressource_bundleUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

        return l;

    }

    @CacheEvict(value = "CachedRessourcebundle", allEntries = true)
    public String cacheEvictBundles(ServiceContext ctx) throws Exception {
    	if (ctx != null) {
            if (ctx.getDetails() != null) {
                logger.info(
                        "PowerCardV3 : Operation: cacheEvictBundles, USER :" +
                        ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId() +
                        " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());
            }
        }
    	return "0000";
    }
    
    @Override
	public List<Ressource_bundleVO> searchByBundleNamesAndLocaleChain(ServiceContext ctx, Collection<String> bundleNames, String locale_chain) throws Exception {

		return this.getRessource_bundleRepository().findByCondition(
			Arrays.asList(
				ConditionalCriteria.in(Ressource_bundleProperties.ressource_bundlePK().bundle(), bundleNames),
				ConditionalCriteria.equal(Ressource_bundleProperties.ressource_bundlePK().locale_chain(), locale_chain)
			)
		)
		.stream()
		.map(bundle -> {
			try {
				return Ressource_bundleUtils.entityToVO(ctx, bundle, 0, 0);
			} catch (Exception e) {
				logger.error(e.getMessage());
				return null;
			}
		})
		.filter(Objects::nonNull)
		.collect(Collectors.toList());
	}
    public String mergeRessource_bundleService(ServiceContext ctx,
            Ressource_bundleVO ressource_bundleVO) throws Exception {
            String sessionID = null;
            String remoteAddress = null;
            if (ctx != null) {
                if (ctx.getDetails() != null) {
                    sessionID = ctx.getDetails().getSessionId();
                    remoteAddress = ctx.getDetails().getRemoteAddress();
                }
                logger.info(
                    "PowerCardV3 : Operation:mergeRessource_bundleService , USER :" +
                    ctx.getUserId() + " , SessionID :" + sessionID +
                    " , RemoteAddress:" + remoteAddress);
            }

            ServiceContextStore.set(ctx);

            Ressource_bundle ressource_bundle =
                Ressource_bundleUtils.VoToEntity(ctx, ressource_bundleVO);

            Ressource_bundle ressource_bundle1 =
                this.getRessource_bundleRepository().save(ressource_bundle);

            return "0000";

        }

        public String mergeAllRessource_bundleService(ServiceContext ctx,
            List<Ressource_bundleVO> listRessource_bundleVO) throws Exception {
            String sessionID = null;
            String remoteAddress = null;
            if (ctx != null) {
                if (ctx.getDetails() != null) {
                    sessionID = ctx.getDetails().getSessionId();
                    remoteAddress = ctx.getDetails().getRemoteAddress();
                }
                logger.info(
                    "PowerCardV3 : Operation:mergeAllRessource_bundleService , USER :" +
                    ctx.getUserId() + " , SessionID :" + sessionID +
                    " , RemoteAddress:" + remoteAddress);
            }

            for (int i = 0; i < listRessource_bundleVO.size(); i++) {
                mergeRessource_bundleService(ctx, listRessource_bundleVO.get(i));
            }

            return "0000";
        }

}
