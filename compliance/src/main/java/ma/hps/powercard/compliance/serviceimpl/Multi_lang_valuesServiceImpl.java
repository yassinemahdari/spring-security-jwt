package ma.hps.powercard.compliance.serviceimpl;

import org.apache.log4j.Logger;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.domain.PagedResult;
import org.fornax.cartridges.sculptor.framework.domain.PagingParameter;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Type;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.Locale;
import ma.hps.powercard.compliance.domain.Multi_lang_tables;
import ma.hps.powercard.compliance.domain.Multi_lang_values;
import ma.hps.powercard.compliance.domain.Multi_lang_valuesPK;
import ma.hps.powercard.compliance.domain.Multi_lang_valuesProperties;
import ma.hps.powercard.compliance.exception.Multi_lang_valuesNotFoundException;
import ma.hps.powercard.compliance.repositoryimpl.Multi_lang_valuesUtils;
import ma.hps.powercard.compliance.serviceapi.Multi_lang_valuesVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation222 of Multi_lang_valuesService.
 */
@Lazy
@Service("multi_lang_valuesService")
public class Multi_lang_valuesServiceImpl
    extends Multi_lang_valuesServiceImplBase {
    private static Logger logger =
        Logger.getLogger(Multi_lang_valuesServiceImpl.class);

    public Multi_lang_valuesServiceImpl() {
    }

    /**
    * Persist a Multi_lang_values entity .
    *
    * @param Multi_lang_valuesVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0024 If ever the object already exists.
    *
    */
    public String createMulti_lang_valuesService(ServiceContext ctx,
        Multi_lang_valuesVO multi_lang_valuesVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:createMulti_lang_valuesService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Multi_lang_valuesPK multi_lang_valuesPK =
            new Multi_lang_valuesPK(multi_lang_valuesVO.getColumn_name(),
                multi_lang_valuesVO.getColumn_value(),
                multi_lang_valuesVO.getLocale(),
                multi_lang_valuesVO.getTable_name());

        Multi_lang_values multi_lang_values = null;
        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(Multi_lang_valuesProperties.multi_lang_valuesPK()
                                                                     .column_name(),
                multi_lang_valuesVO.getColumn_name()));

        con.add(ConditionalCriteria.equal(Multi_lang_valuesProperties.multi_lang_valuesPK()
                                                                     .column_value(),
                multi_lang_valuesVO.getColumn_value()));

        con.add(ConditionalCriteria.equal(Multi_lang_valuesProperties.multi_lang_valuesPK()
                                                                     .locale(),
                multi_lang_valuesVO.getLocale()));

        con.add(ConditionalCriteria.equal(Multi_lang_valuesProperties.multi_lang_valuesPK()
                                                                     .table_name(),
                multi_lang_valuesVO.getTable_name()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Multi_lang_values> pagedResult =
            this.getMulti_lang_valuesRepository()
                .findByCondition(con, pagingParameter);
        List<Multi_lang_values> list = pagedResult.getValues();

        if (list.size() > 0) {
            throw new OurException("0024", new Exception(""));
        }
        else {
           
            multi_lang_values = new Multi_lang_values(multi_lang_valuesPK);
        }

        if (multi_lang_valuesVO.getLocale() != null &&
              !multi_lang_valuesVO.getLocale().equals("")) {
            multi_lang_values.setFk_multi_lang_values_01(new Locale(
                    multi_lang_valuesVO.getLocale()));

        } else {
            if ("".equals(multi_lang_valuesVO.getLocale())) {
                multi_lang_values.setFk_multi_lang_values_01(null);
            }
        }

        if (multi_lang_valuesVO.getTable_name() != null &&
              !multi_lang_valuesVO.getTable_name().equals("")) {
            multi_lang_values.setFk_multi_lang_values_02(new Multi_lang_tables(
                    multi_lang_valuesVO.getTable_name()));

        } else {
            if ("".equals(multi_lang_valuesVO.getTable_name())) {
                multi_lang_values.setFk_multi_lang_values_02(null);
            }
        }

        multi_lang_values.setMulti_lang_value(multi_lang_valuesVO.getMulti_lang_value());

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

        Multi_lang_values multi_lang_values1 =
            this.getMulti_lang_valuesRepository().save(multi_lang_values);

        return "0000";

    }

    public String mergeMulti_lang_valuesService(ServiceContext ctx,
        Multi_lang_valuesVO multi_lang_valuesVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:mergeMulti_lang_valuesService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Multi_lang_values multi_lang_values =
            Multi_lang_valuesUtils.VoToEntity(ctx, multi_lang_valuesVO);

     
        this.getMulti_lang_valuesRepository().save(multi_lang_values);

        return "0000";

    }

    /**
    * update a Multi_lang_values entity .
    *
    * @param Multi_lang_valuesVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String updateMulti_lang_valuesService(ServiceContext ctx,
        Multi_lang_valuesVO multi_lang_valuesVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:updateMulti_lang_valuesService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Multi_lang_valuesPK multi_lang_valuesPK =
            new Multi_lang_valuesPK(multi_lang_valuesVO.getColumn_name(),
                multi_lang_valuesVO.getColumn_value(),
                multi_lang_valuesVO.getLocale(),
                multi_lang_valuesVO.getTable_name());

        Multi_lang_values multi_lang_values = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(Multi_lang_valuesProperties.multi_lang_valuesPK()
                                                                     .column_name(),
                multi_lang_valuesVO.getColumn_name()));

        con.add(ConditionalCriteria.equal(Multi_lang_valuesProperties.multi_lang_valuesPK()
                                                                     .column_value(),
                multi_lang_valuesVO.getColumn_value()));

        con.add(ConditionalCriteria.equal(Multi_lang_valuesProperties.multi_lang_valuesPK()
                                                                     .locale(),
                multi_lang_valuesVO.getLocale()));

        con.add(ConditionalCriteria.equal(Multi_lang_valuesProperties.multi_lang_valuesPK()
                                                                     .table_name(),
                multi_lang_valuesVO.getTable_name()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Multi_lang_values> pagedResult =
            this.getMulti_lang_valuesRepository()
                .findByCondition(con, pagingParameter);
        List<Multi_lang_values> list = pagedResult.getValues();

        if (list.size() > 0) {
            Multi_lang_valuesVO oldMulti_lang_valuesVO =
                Multi_lang_valuesUtils.entityToVO(ctx, list.get(0), 0, 0);

            
            multi_lang_values = list.get(0);
        } else {
            throw new OurException("0001",
                new Multi_lang_valuesNotFoundException(""));
        }

        if (multi_lang_valuesVO.getLocale() != null &&
              !multi_lang_valuesVO.getLocale().equals("")) {
            multi_lang_values.setFk_multi_lang_values_01(new Locale(
                    multi_lang_valuesVO.getLocale()));

        } else {
            if ("".equals(multi_lang_valuesVO.getLocale())) {
                multi_lang_values.setFk_multi_lang_values_01(null);
            }
        }

        if (multi_lang_valuesVO.getTable_name() != null &&
              !multi_lang_valuesVO.getTable_name().equals("")) {
            multi_lang_values.setFk_multi_lang_values_02(new Multi_lang_tables(
                    multi_lang_valuesVO.getTable_name()));

        } else {
            if ("".equals(multi_lang_valuesVO.getTable_name())) {
                multi_lang_values.setFk_multi_lang_values_02(null);
            }
        }

        if (multi_lang_valuesVO.getMulti_lang_value() != null) {
            multi_lang_values.setMulti_lang_value(multi_lang_valuesVO.getMulti_lang_value());
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

        Multi_lang_values multi_lang_values1 =
            this.getMulti_lang_valuesRepository().save(multi_lang_values);

        return "0000";

    }

    /**
    * delete a Multi_lang_values entity .
    *
    * @param Multi_lang_valuesVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String deleteMulti_lang_valuesService(ServiceContext ctx,
        Multi_lang_valuesVO multi_lang_valuesVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:deleteMulti_lang_valuesService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Multi_lang_valuesPK multi_lang_valuesPK =
            new Multi_lang_valuesPK(multi_lang_valuesVO.getColumn_name(),
                multi_lang_valuesVO.getColumn_value(),
                multi_lang_valuesVO.getLocale(),
                multi_lang_valuesVO.getTable_name());

        Multi_lang_values multi_lang_values = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(Multi_lang_valuesProperties.multi_lang_valuesPK()
                                                                     .column_name(),
                multi_lang_valuesVO.getColumn_name()));

        con.add(ConditionalCriteria.equal(Multi_lang_valuesProperties.multi_lang_valuesPK()
                                                                     .column_value(),
                multi_lang_valuesVO.getColumn_value()));

        con.add(ConditionalCriteria.equal(Multi_lang_valuesProperties.multi_lang_valuesPK()
                                                                     .locale(),
                multi_lang_valuesVO.getLocale()));

        con.add(ConditionalCriteria.equal(Multi_lang_valuesProperties.multi_lang_valuesPK()
                                                                     .table_name(),
                multi_lang_valuesVO.getTable_name()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Multi_lang_values> pagedResult =
            this.getMulti_lang_valuesRepository()
                .findByCondition(con, pagingParameter);
        List<Multi_lang_values> list = pagedResult.getValues();

        if (list.size() > 0) {
           
            multi_lang_values = list.get(0);
        } else {
            throw new OurException("0001",
                new Multi_lang_valuesNotFoundException(""));
        }

        if (multi_lang_valuesVO.getLocale() != null &&
              !multi_lang_valuesVO.getLocale().equals("")) {
            multi_lang_values.setFk_multi_lang_values_01(new Locale(
                    multi_lang_valuesVO.getLocale()));

        } else {
            if ("".equals(multi_lang_valuesVO.getLocale())) {
                multi_lang_values.setFk_multi_lang_values_01(null);
            }
        }

        if (multi_lang_valuesVO.getTable_name() != null &&
              !multi_lang_valuesVO.getTable_name().equals("")) {
            multi_lang_values.setFk_multi_lang_values_02(new Multi_lang_tables(
                    multi_lang_valuesVO.getTable_name()));

        } else {
            if ("".equals(multi_lang_valuesVO.getTable_name())) {
                multi_lang_values.setFk_multi_lang_values_02(null);
            }
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

        this.getMulti_lang_valuesRepository().delete(multi_lang_values);

        return "0000";

    }

    /**
    *  Find all entities of a specific type .
    *
    * @return List of Multi_lang_valuesVO
    *
    */
    public List<Multi_lang_valuesVO> getAllMulti_lang_valuesService(
        ServiceContext ctx, Multi_lang_valuesVO multi_lang_valuesVO)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:getAllMulti_lang_valuesService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        List<Multi_lang_valuesVO> l = new ArrayList<Multi_lang_valuesVO>();

        List<Multi_lang_values> l_entity = new ArrayList<Multi_lang_values>();

        l_entity = this.getMulti_lang_valuesRepository().findAll();

        l = Multi_lang_valuesUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

        return l;

    }

    /**
    * Find entities by conditions
    *
     * @return List of Multi_lang_valuesVO
    *
    */
    public List<Multi_lang_valuesVO> searchMulti_lang_valuesService(
        ServiceContext ctx, Multi_lang_valuesVO multi_lang_valuesVO)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:searchMulti_lang_valuesService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (ctx != null) {
            Multi_lang_valuesUtils.dataFilter(ctx, con);
        }

        Multi_lang_valuesUtils.setListOfCriteria(ctx, con, multi_lang_valuesVO);

        List<Multi_lang_values> l_entity = new ArrayList<Multi_lang_values>();
        List<Multi_lang_valuesVO> l = new ArrayList<Multi_lang_valuesVO>();

        int page = multi_lang_valuesVO.getPage();
        int pageSize = multi_lang_valuesVO.getPageSize();
        boolean countTotalPages = true;
        if (page > 0 && pageSize > 0) {
            PagingParameter pagingParameter =
                PagingParameter.pageAccess(pageSize, page, countTotalPages);

            PagedResult<Multi_lang_values> pagedResult =
                this.getMulti_lang_valuesRepository()
                    .findByCondition(con, pagingParameter);

            l_entity = pagedResult.getValues();

            ctx.setProperty("pagedResult", pagedResult);

        } else {
            l_entity = this.getMulti_lang_valuesRepository().findByCondition(con);
        }

        l = Multi_lang_valuesUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

        l = Multi_lang_valuesUtils.filterMultiLang(multi_lang_valuesVO, l);

        return l;

    }

    public String processOperations(ServiceContext ctx, JsonObject mapVO)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:processOperations , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

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
                    }
                    return null;
                }
            };
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, deserializer);
        Gson gson = gsonBuilder.create();
        String returnedMsg = "";
        Boolean isMakerChecker = false;

        for (Entry<String, JsonElement> entry : mapVO.entrySet()) {
            String cle = entry.getKey();
            Object valeurTmp = entry.getValue();
            JsonObject valJson =
                gson.fromJson("" + valeurTmp, JsonObject.class);
            String entityName = valJson.get("entity").getAsString();

            if ("Multi_lang_values".equals(entityName)) {
                if (cle.contains("create" + entityName)) {
                    Multi_lang_valuesVO valeur =
                        gson.fromJson(valJson.get("vo"),
                            Multi_lang_valuesVO.class);
                    returnedMsg = this.createMulti_lang_valuesService(ctx,
                            valeur);
                    if ("2121".equals(returnedMsg)) {
                        isMakerChecker = true;
                    }
                    continue;
                }
                if (cle.contains("update" + entityName)) {
                    Multi_lang_valuesVO valeur =
                        gson.fromJson(valJson.get("vo"),
                            Multi_lang_valuesVO.class);
                    returnedMsg = this.updateMulti_lang_valuesService(ctx,
                            valeur);
                    if ("2121".equals(returnedMsg)) {
                        isMakerChecker = true;
                    }
                    continue;
                }
                if (cle.contains("delete" + entityName)) {
                    Multi_lang_valuesVO valeur =
                        gson.fromJson(valJson.get("vo"),
                            Multi_lang_valuesVO.class);
                    returnedMsg = this.deleteMulti_lang_valuesService(ctx,
                            valeur);
                    if ("2121".equals(returnedMsg)) {
                        isMakerChecker = true;
                    }
                    continue;
                }

                if (cle.contains("merge" + entityName)) {
                    Multi_lang_valuesVO valeur =
                        gson.fromJson(valJson.get("vo"),
                            Multi_lang_valuesVO.class);
                    returnedMsg = this.mergeMulti_lang_valuesService(ctx, valeur);
                    if ("2121".equals(returnedMsg)) {
                        isMakerChecker = true;
                    }
                    continue;
                }

                if (cle.contains("addAllMulti_lang_values")) {
                    Type listType =
                        TypeToken.getParameterized(java.util.ArrayList.class,
                            Multi_lang_valuesVO.class).getType();
                    List<Multi_lang_valuesVO> l =
                        (List<Multi_lang_valuesVO>) gson.fromJson(valJson.get(
                                "vo"), listType);
                    for (int i = 0; i < l.size(); i++) {
                        returnedMsg = createMulti_lang_valuesService(ctx,
                                l.get(i));
                        if ("2121".equals(returnedMsg)) {
                            isMakerChecker = true;
                        }
                    }
                    continue;
                }

                if (cle.contains("modifyAllMulti_lang_values")) {
                    Type listType =
                        TypeToken.getParameterized(java.util.ArrayList.class,
                            Multi_lang_valuesVO.class).getType();
                    List<Multi_lang_valuesVO> l =
                        (List<Multi_lang_valuesVO>) gson.fromJson(valJson.get(
                                "vo"), listType);
                    for (int i = 0; i < l.size(); i++) {
                        returnedMsg = updateMulti_lang_valuesService(ctx,
                                l.get(i));
                        if ("2121".equals(returnedMsg)) {
                            isMakerChecker = true;
                        }
                    }
                    continue;
                }

                if (cle.contains("removeAllMulti_lang_values")) {
                    Type listType =
                        TypeToken.getParameterized(java.util.ArrayList.class,
                            Multi_lang_valuesVO.class).getType();
                    List<Multi_lang_valuesVO> l =
                        (List<Multi_lang_valuesVO>) gson.fromJson(valJson.get(
                                "vo"), listType);
                    for (int i = 0; i < l.size(); i++) {
                        returnedMsg = this.deleteMulti_lang_valuesService(ctx,
                                l.get(i));
                        if ("2121".equals(returnedMsg)) {
                            isMakerChecker = true;
                        }
                    }
                    continue;
                }

                if (cle.contains("mergeAllMulti_lang_values")) {
                    Type listType =
                        TypeToken.getParameterized(java.util.ArrayList.class,
                            Multi_lang_valuesVO.class).getType();
                    List<Multi_lang_valuesVO> l =
                        (List<Multi_lang_valuesVO>) gson.fromJson(valJson.get(
                                "vo"), listType);
                    for (int i = 0; i < l.size(); i++) {
                        returnedMsg = this.mergeMulti_lang_valuesService(ctx,
                                l.get(i));
                        if ("2121".equals(returnedMsg)) {
                            isMakerChecker = true;
                        }
                    }
                    continue;
                }
            }
        }

        return "0000";
    }

    public String mergeAllMulti_lang_valuesService(ServiceContext ctx,
        List<Multi_lang_valuesVO> listMulti_lang_valuesVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:mergeAllMulti_lang_valuesService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        for (int i = 0; i < listMulti_lang_valuesVO.size(); i++) {
            mergeMulti_lang_valuesService(ctx, listMulti_lang_valuesVO.get(i));
        }
      	
        cacheEvict(ctx); //Clean cach after save

        return "0000";
    }

    public String createAllMulti_lang_valuesService(ServiceContext ctx,
        List<Multi_lang_valuesVO> listMulti_lang_valuesVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:createAllMulti_lang_valuesService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        for (int i = 0; i < listMulti_lang_valuesVO.size(); i++) {
            createMulti_lang_valuesService(ctx, listMulti_lang_valuesVO.get(i));
        }

        return "0000";
    }

    public String updateAllMulti_lang_valuesService(ServiceContext ctx,
        List<Multi_lang_valuesVO> listMulti_lang_valuesVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:updateAllMulti_lang_valuesService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        for (int i = 0; i < listMulti_lang_valuesVO.size(); i++) {
            updateMulti_lang_valuesService(ctx, listMulti_lang_valuesVO.get(i));
        }

        return "0000";
    }

    public String deletAlleMulti_lang_valuesService(ServiceContext ctx,
        List<Multi_lang_valuesVO> listMulti_lang_valuesVO) throws Exception {

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
            "deletAlleMulti_lang_valuesService not implemented");

    }

    @Cacheable(value="cachedMultiLangValues", key="#tableName.concat(#columnName).concat(#locale)")
    public List<Multi_lang_values> getCachedMultiLangValues(
        ServiceContext ctx, String tableName, String columnName, String locale)
        throws Exception {

    	String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:getCachedMultiLangValues , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (tableName != null && !tableName.equals("")) {
        	con.add(ConditionalCriteria.like(Multi_lang_valuesProperties.multi_lang_valuesPK().table_name(), tableName));
        }
        
        if (columnName != null && !columnName.equals("")) {
        	con.add(ConditionalCriteria.like(Multi_lang_valuesProperties.multi_lang_valuesPK().column_name(), columnName));
        }
        
        if (locale != null && !locale.equals("")) {
        	con.add(ConditionalCriteria.like(Multi_lang_valuesProperties.multi_lang_valuesPK().locale(), locale));
        }

        return this.getMulti_lang_valuesRepository().findByCondition(con);

    }
    
    @CachePut(value="cachedMultiLangValues", key="#tableName.concat(#columnName).concat(#locale)")
    public List<Multi_lang_values> updateCachedMultiLangValues(ServiceContext ctx, String tableName, String columnName,
    		String locale) throws Exception {
    	String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:updateCachedMultiLangValues , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (tableName != null && !tableName.equals("")) {
        	con.add(ConditionalCriteria.like(Multi_lang_valuesProperties.multi_lang_valuesPK().table_name(), tableName));
        }
        
        if (columnName != null && !columnName.equals("")) {
        	con.add(ConditionalCriteria.like(Multi_lang_valuesProperties.multi_lang_valuesPK().column_name(), columnName));
        }
        
        if (locale != null && !locale.equals("")) {
        	con.add(ConditionalCriteria.like(Multi_lang_valuesProperties.multi_lang_valuesPK().locale(), locale));
        }

        return this.getMulti_lang_valuesRepository().findByCondition(con);
    }

    @CacheEvict(value = "cachedMultiLangValues", allEntries = true)
	public String cacheEvict(ServiceContext ctx) throws Exception {
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

   
}
