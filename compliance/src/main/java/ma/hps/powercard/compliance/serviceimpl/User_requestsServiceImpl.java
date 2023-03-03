package ma.hps.powercard.compliance.serviceimpl;

import org.apache.log4j.Logger;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;

import java.util.ArrayList;
import java.util.List;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.User_requests;
import ma.hps.powercard.compliance.domain.User_requestsProperties;
import ma.hps.powercard.compliance.exception.User_requestsNotFoundException;
import ma.hps.powercard.compliance.repositoryimpl.User_requestsUtils;
import ma.hps.powercard.compliance.serviceapi.User_requestsVO;
import ma.hps.powercard.compliance.serviceapi.UsersVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of User_requestsService.
 */
@Lazy
@Service("user_requestsService")
public class User_requestsServiceImpl extends User_requestsServiceImplBase {
    private static Logger logger =
        Logger.getLogger(User_requestsServiceImpl.class);

    public User_requestsServiceImpl() {
    }

    /**
    * Persist a User_requests entity .
    *
    * @param User_requestsVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0024 If ever the object already exists.
    *
    */
    public String createUser_requestsService(ServiceContext ctx,
        User_requestsVO user_requestsVO) throws Exception {
        logger.info(
            "PowerCardV3 : Operation:User_requestsService.createUser_requestsService , USER :" +
            ctx.getUserId() + " , SessionID :" +
            ctx.getDetails().getSessionId() + " , RemoteAddress:" +
            ctx.getDetails().getRemoteAddress());

        ServiceContextStore.set(ctx);
        
        if (user_requestsVO.getReason_request_fk().intValue() == 1){        
			UsersVO usersVO = new UsersVO();				
			usersVO.setUser_code(user_requestsVO.getUser_code());
			List<UsersVO> l = this.getUsersService().searchUsersService( ctx,usersVO);				
			if (l.size()>0) 
				throw new OurException("0045", new Exception());
        }

        User_requests user_requests = new User_requests();

        user_requests.setRequester_code(user_requestsVO.getRequester_code());

        user_requests.setRequest_date(user_requestsVO.getRequest_date());

        user_requests.setAccess_resriction(user_requestsVO.getAccess_resriction());

        user_requests.setUser_code(user_requestsVO.getUser_code());

        user_requests.setBrowser_disconnection(user_requestsVO.getBrowser_disconnection());

        user_requests.setUser_id(user_requestsVO.getUser_id());

        user_requests.setAccount_end_date(user_requestsVO.getAccount_end_date());

        user_requests.setPrivilege_end_date(user_requestsVO.getPrivilege_end_date());

        user_requests.setMail(user_requestsVO.getMail());

        user_requests.setActiv_email(user_requestsVO.getActiv_email());

        user_requests.setJob_title(user_requestsVO.getJob_title());

        user_requests.setStatus(user_requestsVO.getStatus());

        user_requests.setStaff_indicateur(user_requestsVO.getStaff_indicateur());

        user_requests.setPrivilege_start_date(user_requestsVO.getPrivilege_start_date());

        user_requests.setDis_notification_type(user_requestsVO.getDis_notification_type());

        user_requests.setAccess_by(user_requestsVO.getAccess_by());

        user_requests.setAccount_start_date(user_requestsVO.getAccount_start_date());

        user_requests.setAccount_expiry_date(user_requestsVO.getAccount_expiry_date());

        user_requests.setUser_name(user_requestsVO.getUser_name());

        user_requests.setEmploye_number(user_requestsVO.getEmploye_number());

        user_requests.setTimer_browser_disconnection(user_requestsVO.getTimer_browser_disconnection());

        user_requests.setPwc_disconnection(user_requestsVO.getPwc_disconnection());

        user_requests.setTimer_pwc_disconnection(user_requestsVO.getTimer_pwc_disconnection());

        user_requests.setPhone_number(user_requestsVO.getPhone_number());

        user_requests.setUser_country_id(user_requestsVO.getUser_country_id());

        user_requests.setUser_branch_id(user_requestsVO.getUser_branch_id());

        user_requests.setUser_departement_id(user_requestsVO.getUser_departement_id());

        user_requests.setUser_data_access_id(user_requestsVO.getUser_data_access_id());

        user_requests.setUser_institution_id(user_requestsVO.getUser_institution_id());

        user_requests.setUser_profile_id(user_requestsVO.getUser_profile_id());

        user_requests.setUser_sub_departement_id(user_requestsVO.getUser_sub_departement_id());

        user_requests.setUser_boss_id(user_requestsVO.getUser_boss_id());

        user_requests.setUser_branch_group_id(user_requestsVO.getUser_branch_group_id());

        user_requests.setUser_language_id(user_requestsVO.getUser_language_id());

        user_requests.setReason_request_fk(user_requestsVO.getReason_request_fk());

        user_requests.setCollection_process_privilege(user_requestsVO.getCollection_process_privilege());

        user_requests.setCollection_dispatch_privilege(user_requestsVO.getCollection_dispatch_privilege());

        user_requests.setUser_collection_list(user_requestsVO.getUser_collection_list());

        user_requests.setDba_privilege(user_requestsVO.getDba_privilege());

        user_requests.setStart_date(user_requestsVO.getStart_date());

        user_requests.setEnd_date(user_requestsVO.getEnd_date());

        user_requests.setLast_db_connect(user_requestsVO.getLast_db_connect());

        user_requests.setBank_card_batch(user_requestsVO.getBank_card_batch());

        user_requests.setCurrent_card_batch(user_requestsVO.getCurrent_card_batch());

        user_requests.setDate_cur_card_batch(user_requestsVO.getDate_cur_card_batch());

        user_requests.setIp_address_access(user_requestsVO.getIp_address_access());

        user_requests.setBank_code_access_list(user_requestsVO.getBank_code_access_list());

        user_requests.setIncrease_limits_currency(user_requestsVO.getIncrease_limits_currency());

        user_requests.setIncrease_credit_limit_perc(user_requestsVO.getIncrease_credit_limit_perc());

        user_requests.setIncrease_credit_limit_max(user_requestsVO.getIncrease_credit_limit_max());

        user_requests.setIncrease_cash_limit_perc(user_requestsVO.getIncrease_cash_limit_perc());

        user_requests.setIncrease_cash_limit_max(user_requestsVO.getIncrease_cash_limit_max());

        user_requests.setIncrease_loan_limit_perc(user_requestsVO.getIncrease_loan_limit_perc());

        user_requests.setIncrease_loan_limit_max(user_requestsVO.getIncrease_loan_limit_max());

        user_requests.setBalances_hidden_flag(user_requestsVO.getBalances_hidden_flag());

        user_requests.setScreen_show_name(user_requestsVO.getScreen_show_name());

        user_requests.setScreen_show_db(user_requestsVO.getScreen_show_db());

        user_requests.setScreen_show_db_connect(user_requestsVO.getScreen_show_db_connect());

        user_requests.setCheck_sum(user_requestsVO.getCheck_sum());

        user_requests.setForms_message_level(user_requestsVO.getForms_message_level());

        user_requests.setClaims_grouping_index(user_requestsVO.getClaims_grouping_index());

        user_requests.setUser_terminal_group(user_requestsVO.getUser_terminal_group());

        User_requests user_requests1 =
            this.getUser_requestsRepository().save(user_requests);

        return "0000";

    }

    /**
    * update a User_requests entity .
    *
    * @param User_requestsVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String updateUser_requestsService(ServiceContext ctx,
        User_requestsVO user_requestsVO) throws Exception {
        logger.info(
            "PowerCardV3 : Operation:User_requestsService.updateUser_requestsService , USER :" +
            ctx.getUserId() + " , SessionID :" +
            ctx.getDetails().getSessionId() + " , RemoteAddress:" +
            ctx.getDetails().getRemoteAddress());

        ServiceContextStore.set(ctx);

        User_requests user_requests = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(
                User_requestsProperties.user_requests_id(),
                user_requestsVO.getUser_requests_id()));

        List<User_requests> list =
            this.getUser_requestsRepository().findByCondition(con);

        if (list.size() > 0) {
            user_requests = list.get(0);
        } else {
            throw new OurException("0001",
                new User_requestsNotFoundException(""));
        }

        if (user_requestsVO.getRequester_code() != null) {
            user_requests.setRequester_code(user_requestsVO.getRequester_code());
        }

        if (user_requestsVO.getRequest_date() != null) {
            user_requests.setRequest_date(user_requestsVO.getRequest_date());
        }

        if (user_requestsVO.getAccess_resriction() != null) {
            user_requests.setAccess_resriction(user_requestsVO.getAccess_resriction());
        }

        if (user_requestsVO.getUser_code() != null) {
            user_requests.setUser_code(user_requestsVO.getUser_code());
        }

        if (user_requestsVO.getBrowser_disconnection() != null) {
            user_requests.setBrowser_disconnection(user_requestsVO.getBrowser_disconnection());
        }

        if (user_requestsVO.getUser_id() != null) {
            user_requests.setUser_id(user_requestsVO.getUser_id());
        }

        if (user_requestsVO.getAccount_end_date() != null) {
            user_requests.setAccount_end_date(user_requestsVO.getAccount_end_date());
        }

        if (user_requestsVO.getPrivilege_end_date() != null) {
            user_requests.setPrivilege_end_date(user_requestsVO.getPrivilege_end_date());
        }

        if (user_requestsVO.getMail() != null) {
            user_requests.setMail(user_requestsVO.getMail());
        }

        if (user_requestsVO.getActiv_email() != null) {
            user_requests.setActiv_email(user_requestsVO.getActiv_email());
        }

        if (user_requestsVO.getJob_title() != null) {
            user_requests.setJob_title(user_requestsVO.getJob_title());
        }

        if (user_requestsVO.getStatus() != null) {
            user_requests.setStatus(user_requestsVO.getStatus());
        }

        if (user_requestsVO.getStaff_indicateur() != null) {
            user_requests.setStaff_indicateur(user_requestsVO.getStaff_indicateur());
        }

        if (user_requestsVO.getPrivilege_start_date() != null) {
            user_requests.setPrivilege_start_date(user_requestsVO.getPrivilege_start_date());
        }

        if (user_requestsVO.getDis_notification_type() != null) {
            user_requests.setDis_notification_type(user_requestsVO.getDis_notification_type());
        }

        if (user_requestsVO.getAccess_by() != null) {
            user_requests.setAccess_by(user_requestsVO.getAccess_by());
        }

        if (user_requestsVO.getAccount_start_date() != null) {
            user_requests.setAccount_start_date(user_requestsVO.getAccount_start_date());
        }

        if (user_requestsVO.getAccount_expiry_date() != null) {
            user_requests.setAccount_expiry_date(user_requestsVO.getAccount_expiry_date());
        }

        if (user_requestsVO.getUser_name() != null) {
            user_requests.setUser_name(user_requestsVO.getUser_name());
        }

        if (user_requestsVO.getEmploye_number() != null) {
            user_requests.setEmploye_number(user_requestsVO.getEmploye_number());
        }

        if (user_requestsVO.getTimer_browser_disconnection() != null) {
            user_requests.setTimer_browser_disconnection(user_requestsVO.getTimer_browser_disconnection());
        }

        if (user_requestsVO.getPwc_disconnection() != null) {
            user_requests.setPwc_disconnection(user_requestsVO.getPwc_disconnection());
        }

        if (user_requestsVO.getTimer_pwc_disconnection() != null) {
            user_requests.setTimer_pwc_disconnection(user_requestsVO.getTimer_pwc_disconnection());
        }

        if (user_requestsVO.getPhone_number() != null) {
            user_requests.setPhone_number(user_requestsVO.getPhone_number());
        }

        if (user_requestsVO.getUser_country_id() != null) {
            user_requests.setUser_country_id(user_requestsVO.getUser_country_id());
        }

        if (user_requestsVO.getUser_branch_id() != null) {
            user_requests.setUser_branch_id(user_requestsVO.getUser_branch_id());
        }

        if (user_requestsVO.getUser_departement_id() != null) {
            user_requests.setUser_departement_id(user_requestsVO.getUser_departement_id());
        }

        if (user_requestsVO.getUser_data_access_id() != null) {
            user_requests.setUser_data_access_id(user_requestsVO.getUser_data_access_id());
        }

        if (user_requestsVO.getUser_institution_id() != null) {
            user_requests.setUser_institution_id(user_requestsVO.getUser_institution_id());
        }

        if (user_requestsVO.getUser_profile_id() != null) {
            user_requests.setUser_profile_id(user_requestsVO.getUser_profile_id());
        }

        if (user_requestsVO.getUser_sub_departement_id() != null) {
            user_requests.setUser_sub_departement_id(user_requestsVO.getUser_sub_departement_id());
        }

        if (user_requestsVO.getUser_boss_id() != null) {
            user_requests.setUser_boss_id(user_requestsVO.getUser_boss_id());
        }

        if (user_requestsVO.getUser_branch_group_id() != null) {
            user_requests.setUser_branch_group_id(user_requestsVO.getUser_branch_group_id());
        }

        if (user_requestsVO.getUser_language_id() != null) {
            user_requests.setUser_language_id(user_requestsVO.getUser_language_id());
        }

        if (user_requestsVO.getReason_request_fk() != null) {
            user_requests.setReason_request_fk(user_requestsVO.getReason_request_fk());
        }

        if (user_requestsVO.getCollection_process_privilege() != null) {
            user_requests.setCollection_process_privilege(user_requestsVO.getCollection_process_privilege());
        }

        if (user_requestsVO.getCollection_dispatch_privilege() != null) {
            user_requests.setCollection_dispatch_privilege(user_requestsVO.getCollection_dispatch_privilege());
        }

        if (user_requestsVO.getUser_collection_list() != null) {
            user_requests.setUser_collection_list(user_requestsVO.getUser_collection_list());
        }

        if (user_requestsVO.getDba_privilege() != null) {
            user_requests.setDba_privilege(user_requestsVO.getDba_privilege());
        }

        if (user_requestsVO.getStart_date() != null) {
            user_requests.setStart_date(user_requestsVO.getStart_date());
        }

        if (user_requestsVO.getEnd_date() != null) {
            user_requests.setEnd_date(user_requestsVO.getEnd_date());
        }

        if (user_requestsVO.getLast_db_connect() != null) {
            user_requests.setLast_db_connect(user_requestsVO.getLast_db_connect());
        }

        if (user_requestsVO.getBank_card_batch() != null) {
            user_requests.setBank_card_batch(user_requestsVO.getBank_card_batch());
        }

        if (user_requestsVO.getCurrent_card_batch() != null) {
            user_requests.setCurrent_card_batch(user_requestsVO.getCurrent_card_batch());
        }

        if (user_requestsVO.getDate_cur_card_batch() != null) {
            user_requests.setDate_cur_card_batch(user_requestsVO.getDate_cur_card_batch());
        }

        if (user_requestsVO.getIp_address_access() != null) {
            user_requests.setIp_address_access(user_requestsVO.getIp_address_access());
        }

        if (user_requestsVO.getBank_code_access_list() != null) {
            user_requests.setBank_code_access_list(user_requestsVO.getBank_code_access_list());
        }

        if (user_requestsVO.getIncrease_limits_currency() != null) {
            user_requests.setIncrease_limits_currency(user_requestsVO.getIncrease_limits_currency());
        }

        if (user_requestsVO.getIncrease_credit_limit_perc() != null) {
            user_requests.setIncrease_credit_limit_perc(user_requestsVO.getIncrease_credit_limit_perc());
        }

        if (user_requestsVO.getIncrease_credit_limit_max() != null) {
            user_requests.setIncrease_credit_limit_max(user_requestsVO.getIncrease_credit_limit_max());
        }

        if (user_requestsVO.getIncrease_cash_limit_perc() != null) {
            user_requests.setIncrease_cash_limit_perc(user_requestsVO.getIncrease_cash_limit_perc());
        }

        if (user_requestsVO.getIncrease_cash_limit_max() != null) {
            user_requests.setIncrease_cash_limit_max(user_requestsVO.getIncrease_cash_limit_max());
        }

        if (user_requestsVO.getIncrease_loan_limit_perc() != null) {
            user_requests.setIncrease_loan_limit_perc(user_requestsVO.getIncrease_loan_limit_perc());
        }

        if (user_requestsVO.getIncrease_loan_limit_max() != null) {
            user_requests.setIncrease_loan_limit_max(user_requestsVO.getIncrease_loan_limit_max());
        }

        if (user_requestsVO.getBalances_hidden_flag() != null) {
            user_requests.setBalances_hidden_flag(user_requestsVO.getBalances_hidden_flag());
        }

        if (user_requestsVO.getScreen_show_name() != null) {
            user_requests.setScreen_show_name(user_requestsVO.getScreen_show_name());
        }

        if (user_requestsVO.getScreen_show_db() != null) {
            user_requests.setScreen_show_db(user_requestsVO.getScreen_show_db());
        }

        if (user_requestsVO.getScreen_show_db_connect() != null) {
            user_requests.setScreen_show_db_connect(user_requestsVO.getScreen_show_db_connect());
        }

        if (user_requestsVO.getCheck_sum() != null) {
            user_requests.setCheck_sum(user_requestsVO.getCheck_sum());
        }

        if (user_requestsVO.getForms_message_level() != null) {
            user_requests.setForms_message_level(user_requestsVO.getForms_message_level());
        }

        if (user_requestsVO.getClaims_grouping_index() != null) {
            user_requests.setClaims_grouping_index(user_requestsVO.getClaims_grouping_index());
        }

        if (user_requestsVO.getUser_terminal_group() != null) {
            user_requests.setUser_terminal_group(user_requestsVO.getUser_terminal_group());
        }

        User_requests user_requests1 =
            this.getUser_requestsRepository().save(user_requests);

        return "0000";

    }

    /**
    * delete a User_requests entity .
    *
    * @param User_requestsVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String deleteUser_requestsService(ServiceContext ctx,
        User_requestsVO user_requestsVO) throws Exception {
        logger.info(
            "PowerCardV3 : Operation:User_requestsService.deleteUser_requestsService , USER :" +
            ctx.getUserId() + " , SessionID :" +
            ctx.getDetails().getSessionId() + " , RemoteAddress:" +
            ctx.getDetails().getRemoteAddress());

        ServiceContextStore.set(ctx);

        User_requests user_requests = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(
                User_requestsProperties.user_requests_id(),
                user_requestsVO.getUser_requests_id()));

        List<User_requests> list =
            this.getUser_requestsRepository().findByCondition(con);

        if (list.size() > 0) {
            user_requests = list.get(0);
        } else {
            throw new OurException("0001",
                new User_requestsNotFoundException(""));
        }

        this.getUser_requestsRepository().delete(user_requests);

        return "0000";

    }

    /**
    *  Find all entities of a specific type .
    *
    * @return List of User_requestsVO
    *
    */
    public List<User_requestsVO> getAllUser_requestsService(ServiceContext ctx)
        throws Exception {
        logger.info(
            "PowerCardV3 : Operation:User_requestsService.getAllUser_requestsService , USER :" +
            ctx.getUserId() + " , SessionID :" +
            ctx.getDetails().getSessionId() + " , RemoteAddress:" +
            ctx.getDetails().getRemoteAddress());

        List<User_requestsVO> l = new ArrayList<User_requestsVO>();

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (ctx != null) {
            User_requestsUtils.dataFilter(ctx, con);
        }

        List<User_requests> l_entity = new ArrayList<User_requests>();

        l_entity = this.getUser_requestsRepository().findByCondition(con);

        l = User_requestsUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

        return l;

    }

    /**
    * Find entities by conditions
    *
     * @return List of User_requestsVO
    *
    */
    public List<User_requestsVO> searchUser_requestsService(
        ServiceContext ctx, User_requestsVO user_requestsVO) throws Exception {
        logger.info(
            "PowerCardV3 : Operation:User_requestsService.searchUser_requestsService , USER :" +
            ctx.getUserId() + " , SessionID :" +
            ctx.getDetails().getSessionId() + " , RemoteAddress:" +
            ctx.getDetails().getRemoteAddress());

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (ctx != null) {
            User_requestsUtils.dataFilter(ctx, con);
        }

        User_requestsUtils.setListOfCriteria(ctx, con, user_requestsVO);

        List<User_requests> l_entity = new ArrayList<User_requests>();
        List<User_requestsVO> l = new ArrayList<User_requestsVO>();

        l_entity = this.getUser_requestsRepository().findByCondition(con);

        l = User_requestsUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

        return l;

    }
}
