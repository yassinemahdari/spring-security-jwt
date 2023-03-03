package ma.hps.powercard.compliance.serviceimpl;

import org.apache.log4j.Logger;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.Mail_config;
import ma.hps.powercard.compliance.domain.Mail_configProperties;
import ma.hps.powercard.compliance.exception.Mail_configNotFoundException;
import ma.hps.powercard.compliance.serviceapi.Data_columns_filterVO;
import ma.hps.powercard.compliance.serviceapi.Data_columns_filter_valuesVO;
import ma.hps.powercard.compliance.serviceapi.Mail_configVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of Mail_configService.
 */
@Lazy
@Service("mail_configService")
public class Mail_configServiceImpl extends Mail_configServiceImplBase {
    private static Logger logger;

    public Mail_configServiceImpl() {
        logger = Logger.getLogger(Mail_configServiceImpl.class);

    }

    /**
    * Persist a Mail_config entity .
    *
    * @param Mail_configVO ValueObject.
    *
    * @return the {@code id} of the saved {@code Mail_config} record.
    *
    * @throws  OurException No.0024 If ever the object already exists.
    *
    */
    public String createMail_configService(ServiceContext ctx,
        Mail_configVO mail_configVO) throws Exception {
        ServiceContextStore.set(ctx);

        Mail_config mail_config = new Mail_config();

        mail_config.setMail_code(mail_configVO.getMail_code());

        mail_config.setName(mail_configVO.getName());

        mail_config.setWording(mail_configVO.getWording());

        mail_config.setEmail_server(mail_configVO.getEmail_server());

        mail_config.setPort_number(mail_configVO.getPort_number());

        mail_config.setEmail_sender(mail_configVO.getEmail_sender());

        mail_config.setEmail_adress(mail_configVO.getEmail_adress());
        
        mail_config.setEnable_smtps(mail_configVO.getEnable_smtps());

        Mail_config mail_config1 =
            this.getMail_configRepository().save(mail_config);

        return String.valueOf(mail_config1.getMail_config_id());
    }

    /**
    * update a Mail_config entity .
    *
    * @param Mail_configVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String updateMail_configService(ServiceContext ctx,
        Mail_configVO mail_configVO) throws Exception {
        ServiceContextStore.set(ctx);

        Mail_config mail_config = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(
                Mail_configProperties.mail_config_id(),
                mail_configVO.getMail_config_id()));

        List<Mail_config> list =
            this.getMail_configRepository().findByCondition(con);

        if (list.size() > 0) {
            mail_config = list.get(0);
        } else {
            throw new OurException("0001", new Mail_configNotFoundException(""));
        }

        if (mail_configVO.getMail_code() != null) {
            mail_config.setMail_code(mail_configVO.getMail_code());
        }

        if (mail_configVO.getName() != null) {
            mail_config.setName(mail_configVO.getName());
        }

        if (mail_configVO.getWording() != null) {
            mail_config.setWording(mail_configVO.getWording());
        }

        if (mail_configVO.getEmail_server() != null) {
            mail_config.setEmail_server(mail_configVO.getEmail_server());
        }

        if (mail_configVO.getPort_number() != null) {
            mail_config.setPort_number(mail_configVO.getPort_number());
        }

        if (mail_configVO.getEmail_sender() != null) {
            mail_config.setEmail_sender(mail_configVO.getEmail_sender());
        }

        if (mail_configVO.getEmail_adress() != null) {
            mail_config.setEmail_adress(mail_configVO.getEmail_adress());
        }
        
        if(mail_configVO.getEnable_smtps() != null) {
        	mail_config.setEnable_smtps(mail_configVO.getEnable_smtps());
        }
        
        Mail_config mail_config1 =
            this.getMail_configRepository().save(mail_config);

        return "0000";
    }

    /**
    * delete a Mail_config entity .
    *
    * @param Mail_configVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String deleteMail_configService(ServiceContext ctx,
        Mail_configVO mail_configVO) throws Exception {
        ServiceContextStore.set(ctx);

        Mail_config mail_config = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(
                Mail_configProperties.mail_config_id(),
                mail_configVO.getMail_config_id()));

        List<Mail_config> list =
            this.getMail_configRepository().findByCondition(con);

        if (list.size() > 0) {
            mail_config = list.get(0);
        } else {
            throw new OurException("0001", new Mail_configNotFoundException(""));
        }

        this.getMail_configRepository().delete(mail_config);

        return "0000";
    }

    /**
    *  Find all entities of a specific type .
    *
    * @return List of Mail_configVO
    *
    */
    public List<Mail_configVO> getAllMail_configService(ServiceContext ctx)
        throws Exception {
        List<Mail_configVO> l = new ArrayList<Mail_configVO>();

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (ctx != null) {
            dataFilter(ctx, con);
        }

        List<Mail_config> l_entity = new ArrayList<Mail_config>();

        l_entity = this.getMail_configRepository().findByCondition(con);

        for (int i = 0; i < l_entity.size(); i++) {
            l.add(entityToVO(ctx, l_entity.get(i), 0, 0));

        }

        return l;

    }

    /**
    * Find entities by conditions
    *
     * @return List of Mail_configVO
    *
    */
    public List<Mail_configVO> searchMail_configService(ServiceContext ctx,
        Mail_configVO mail_configVO) throws Exception {
        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (ctx != null) {
            dataFilter(ctx, con);
        }

        if (mail_configVO.getMail_config_id() != null) {
            con.add(ConditionalCriteria.equal(
                    Mail_configProperties.mail_config_id(),
                    mail_configVO.getMail_config_id()));
        }

        if (mail_configVO.getMail_code() != null &&
              !mail_configVO.getMail_code().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Mail_configProperties.mail_code(),
                    mail_configVO.getMail_code()));
        }

        if (mail_configVO.getName() != null &&
              !mail_configVO.getName().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(Mail_configProperties.name(),
                    mail_configVO.getName()));
        }

        if (mail_configVO.getWording() != null &&
              !mail_configVO.getWording().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(Mail_configProperties.wording(),
                    mail_configVO.getWording()));
        }

        if (mail_configVO.getEmail_server() != null &&
              !mail_configVO.getEmail_server().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Mail_configProperties.email_server(),
                    mail_configVO.getEmail_server()));
        }

        if (mail_configVO.getPort_number() != null &&
              !mail_configVO.getPort_number().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Mail_configProperties.port_number(),
                    mail_configVO.getPort_number()));
        }

        if (mail_configVO.getEmail_sender() != null &&
              !mail_configVO.getEmail_sender().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Mail_configProperties.email_sender(),
                    mail_configVO.getEmail_sender()));
        }

        if (mail_configVO.getEmail_adress() != null &&
              !mail_configVO.getEmail_adress().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Mail_configProperties.email_adress(),
                    mail_configVO.getEmail_adress()));
        }

        List<Mail_config> l_entity = new ArrayList<Mail_config>();
        List<Mail_configVO> l = new ArrayList<Mail_configVO>();

        l_entity = this.getMail_configRepository().findByCondition(con);

        for (int i = 0; i < l_entity.size(); i++) {
            Mail_configVO mail_configVoTmp =
                entityToVO(ctx, l_entity.get(i), 0, 0);

            l.add(mail_configVoTmp);

        }

        return l;

    }

    /**
    * Convert an entity to ValueObject.
    *
    * @param Mail_config Entity.
    *
    * @return Mail_configVO ValueObject.
    *
    */
    public Mail_configVO entityToVO(ServiceContext ctx,
        Mail_config mail_config, int lazy_level, int lazy_level_col)
        throws Exception {
        Mail_configVO e = new Mail_configVO();

        e.setMail_config_id(mail_config.getMail_config_id());

        e.setMail_code(mail_config.getMail_code());

        e.setName(mail_config.getName());

        e.setWording(mail_config.getWording());

        e.setEmail_server(mail_config.getEmail_server());

        e.setPort_number(mail_config.getPort_number());

        e.setEmail_sender(mail_config.getEmail_sender());

        e.setEmail_adress(mail_config.getEmail_adress());
        
        e.setEnable_smtps(mail_config.getEnable_smtps());

        return e;
    }

    public Mail_config VoToEntity(ServiceContext ctx,
        Mail_configVO mail_configVO) throws Exception {
        Mail_config mail_config =
            new Mail_config(mail_configVO.getMail_config_id());

        mail_config.setMail_code(mail_configVO.getMail_code());

        mail_config.setName(mail_configVO.getName());

        mail_config.setWording(mail_configVO.getWording());

        mail_config.setEmail_server(mail_configVO.getEmail_server());

        mail_config.setPort_number(mail_configVO.getPort_number());

        mail_config.setEmail_sender(mail_configVO.getEmail_sender());

        mail_config.setEmail_adress(mail_configVO.getEmail_adress());
        
        mail_config.setEnable_smtps(mail_configVO.getEnable_smtps());

        return mail_config;
    }

    private void dataFilter(ServiceContext ctx, List<ConditionalCriteria> con)
        throws Exception {
        for (Data_columns_filterVO vo : ctx.getDataColumnsFilterList()) {
            String entity_name =
                vo.getRef_filter().getRef_pwc_tables().getEntity_name();
            String column_name =
                vo.getRef_columns_filter().getRef_pwc_columns().getColumn_name();
            String column_type =
                vo.getRef_columns_filter().getRef_pwc_columns().getColumn_type();
            String naturalKey =
                vo.getRef_columns_filter().getRef_pwc_columns().getNaturalKey();
            String apply_filter =
                vo.getRef_filter().getRef_pwc_tables().getApply_filter();
            String operation =
                vo.getRef_columns_filter().getOperation().toUpperCase();

            Data_columns_filter_valuesVO data_columns_filter_valuesVO =
                new Data_columns_filter_valuesVO();

            data_columns_filter_valuesVO.setData_columns_filter_fk(vo.getData_columns_filter_id());

            Collection<Data_columns_filter_valuesVO> col =
                vo.getData_columns_filter_values_col();
            Iterator<Data_columns_filter_valuesVO> iter = col.iterator();

            List<Object> listVals = new ArrayList<Object>();

            while (iter.hasNext()) {
                if (column_type.equals("Long")) {
                    listVals.add(Long.valueOf(iter.next().getVal()));
                } else {
                    listVals.add(iter.next().getVal());
                }
            }

            if (listVals.size() > 0) {
                if (entity_name.equals(Mail_config.class.getName()) &&
                      apply_filter.equals("Y")) {
                    Mail_configProperties.dynamicAttribute = column_name.toLowerCase();

                    if (operation.equals("I")) {
                        con.add(ConditionalCriteria.in(
                                Mail_configProperties.dynamicAttribute(),
                                listVals));
                    } else {
                        con.add(ConditionalCriteria.not(ConditionalCriteria.in(
                                    Mail_configProperties.dynamicAttribute(),
                                    listVals)));
                    }
                }
            }
        }
    }
}
