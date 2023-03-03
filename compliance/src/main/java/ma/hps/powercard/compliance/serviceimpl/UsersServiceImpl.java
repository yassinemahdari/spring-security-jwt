package ma.hps.powercard.compliance.serviceimpl;

import java.net.PasswordAuthentication;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;


import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.Bank;
import ma.hps.powercard.compliance.domain.Country;
import ma.hps.powercard.compliance.domain.Data_access;
import ma.hps.powercard.compliance.domain.Language_list;
import ma.hps.powercard.compliance.domain.Profile;
import ma.hps.powercard.compliance.domain.Users;
import ma.hps.powercard.compliance.domain.UsersProperties;
import ma.hps.powercard.compliance.exception.UsersNotFoundException;
import ma.hps.powercard.compliance.repositoryimpl.UsersUtils;
import ma.hps.powercard.compliance.serviceapi.UsersVO;
import ma.hps.powercard.compliance.utils.ApplicationContextProcessor;
import ma.hps.powercard.compliance.serviceapi.EncryptionService;
import ma.hps.powercard.compliance.serviceapi.Multi_lang_tablesService;
import ma.hps.powercard.compliance.serviceapi.NotificationService;
import ma.hps.powercard.compliance.serviceapi.Ressource_bundleService;
import ma.hps.powercard.compliance.serviceapi.User_passwordsService;
import ma.hps.powercard.compliance.serviceapi.User_passwordsVO;
import ma.hps.powercard.compliance.serviceapi.NotificationVO;
import ma.hps.powercard.compliance.serviceapi.Ressource_bundleVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of UsersService.
 */

@Lazy
@Service("usersService")
public class UsersServiceImpl extends UsersServiceImplBase {
    private static Logger logger = Logger.getLogger(UsersServiceImpl.class);

    @Lazy
    @Autowired
	private EncryptionService encryptionService;

    @Lazy
	@Autowired
	private NotificationService notificationService;
	
    @Lazy
	@Autowired
	private Ressource_bundleService ressource_bundleService;
	

//	@Autowired
//	private User_passwordsService user_passwordsService;
	
    public UsersServiceImpl() {
    }

    /**
    * Persist a Users entity .
    *
    * @param UsersVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0024 If ever the object already exists.
    *
    */
    public String createUsersService(ServiceContext ctx, UsersVO usersVO)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:createUsersService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        
        /////////////////////////////// USER CREATION //////////////////////////////////

        Users users = new Users();
        
        //If is ldap user
        if(usersVO.getIs_ldap_user() !=null && usersVO.getIs_ldap_user().equals("Y")) {
        	users.setIs_ldap_user("Y");
        	users.setUser_code(usersVO.getUser_code());
        	
        	logger.info("PowerCardV3 : Operation:createUsersService , USER is LDAP user");

            try {
        		Users users1 = this.getUsersRepository().save(users);

        		return ""+users1.getUsers_id();
        	}catch(Exception e) {
                logger.error("PowerCardV3 : Operation:createUsersService , error: "+ e.getMessage());
        		throw new Exception(e.getMessage());
        	}
         
        }
        
        //Check if user_code is already used
        UsersVO uvo = new UsersVO();
        uvo.setUser_code(usersVO.getUser_code());
        List<UsersVO> l = this.searchUsersService(ctx, uvo);
		if (l.size() > 0)
			throw new OurException("0045", new Exception());

        //TODO set profile array
        if (usersVO.getProfile_fk() != null) {
            users.setFk_profile(new Profile(usersVO.getProfile_fk()));
        }
        
        if (usersVO.getCountry_fk() != null &&
              !usersVO.getCountry_fk().equals("")) {
            users.setFk_country(new Country(usersVO.getCountry_fk()));

        } else {
            if ("".equals(usersVO.getCountry_fk())) {
                users.setFk_country(null);
            }
        }

        if (usersVO.getInstitution_fk() != null &&
              !usersVO.getInstitution_fk().equals("")) {
        	
        	List<String> banks = (List<String>) ctx.getProperty("bankDataAccess");
        	
        	if (banks != null && banks.size() > 0 && !banks.contains(usersVO.getInstitution_fk())) {

        		throw new OurException("0403", new Exception("Bank data access restriction"));
        		
        	} else {
        		users.setFk_users_04(new Bank(usersVO.getInstitution_fk()));        		
        	}

        } else {
            if ("".equals(usersVO.getInstitution_fk())) {
                users.setFk_users_04(null);
            }
        }

        if (usersVO.getData_access_fk() != null) {
            users.setFk_data_access(new Data_access(usersVO.getData_access_fk()));

        } else {
        }

        if (usersVO.getLanguage_fk() != null &&
              !usersVO.getLanguage_fk().equals("")) {
            users.setFk_language(new Language_list(usersVO.getLanguage_fk()));

        } else {
            if ("".equals(usersVO.getLanguage_fk())) {
                users.setFk_language(null);
            }
        }
        
        this.setUsersProperties(users,usersVO);

		char[] password = generatePassword().toCharArray();//usersVO.getPassword();
      	String encryptedPass = encryptionService.encryptPassword(ctx, new String(password), usersVO.getUser_code());
		users.setPassword(encryptedPass);
		users.setStatus("F");
		users.setIs_ldap_user("N");
		
		Users users1;
		try {
			users1 = this.getUsersRepository().save(users);
    	}catch(Exception e) {
            logger.error("PowerCardV3 : Operation:createUsersService , error: "+ e.getMessage());
    		throw new Exception(e.getMessage());
    	}
        /////////////////////////END User Creation ///////////////////////////
        
        ///////////////////// CREATING USER_PASSWORD /////////////////////////
        User_passwordsVO userPasswordsVO = new User_passwordsVO();
        userPasswordsVO.setDate_change(new Timestamp(new Date().getTime()));
        userPasswordsVO.setLogin(usersVO.getUser_code());
        userPasswordsVO.setPassword(encryptedPass);
        userPasswordsVO.setUsers_fk(users1.getUsers_id());
        userPasswordsVO.setInvalide_authentif_num("0");

        WebApplicationContext context = ApplicationContextProcessor.getContext();

        User_passwordsService user_passwordsService = (User_passwordsService) context.getBean("user_passwordsService");
        
        try {
            user_passwordsService.createUser_passwordsService(ctx, userPasswordsVO);
    	}catch(Exception e) {
            logger.error("PowerCardV3 : Operation:createUsersService , error: "+ e.getMessage());
    		throw new Exception(e.getMessage());
    	}
        
        ////////////////////// Notifying The requester ///////////////////////
        try {
        	this.notifyRequester(ctx, usersVO, "00002", "msg_user_create_admin");
    	}catch(Exception e) {
            logger.error("PowerCardV3 : Operation:createUsersService , error: "+ e.getMessage());
    		throw new Exception(e.getMessage());
    	}
        

        ////////////////////// Notifying The created User ////////////////////
        String message = "";
	    String language = "";
	    NotificationVO notificationVO = new NotificationVO();
	    
		notificationVO.setNotification_code("00002");
		notificationVO.setLazy_level(1);
		notificationVO = this.notificationService.searchNotificationService(ctx, notificationVO).get(0);
		
     	Ressource_bundleVO ressource_bundleVO = new Ressource_bundleVO();
        List<Ressource_bundleVO> list_ressource_bundle = new ArrayList<Ressource_bundleVO>();

		if (usersVO.getLanguage_fk().equals("SPA")) {
			language = "es_ES";
		} else if (usersVO.getLanguage_fk().equals("FRE")) {
			language = "fr_FR";
		} else {
			language = "en_US";
		}

		ressource_bundleVO.setLocale_chain(language);
		ressource_bundleVO.setBundle("NOTIFICATION");
		ressource_bundleVO.setKey_val("msg_user_create_user");
		list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
		list_ressource_bundle = ressource_bundleService.searchRessource_bundleService(ctx,
				ressource_bundleVO);
		if (list_ressource_bundle.size() > 0) {
			ressource_bundleVO = list_ressource_bundle.get(0);
			message = ressource_bundleVO.getValue();
			if (!message.isEmpty()) {
				message = message.replace("$USER_CODE", usersVO.getUser_code());
              	message = message.replace("$USER_NAME", usersVO.getUser_name());
				message = message.replace("$USER_PASSWORD", new String(password));
			}
		} else {
			message = notificationVO.getContent() + "  : " + usersVO.getUser_code() + " Password  "
					+ new String(password);
		}
		java.util.Arrays.fill(password, '*');
		boolean use_smtps = notificationVO.getRef_mail().getEnable_smtps() != null && notificationVO.getRef_mail().getEnable_smtps().equalsIgnoreCase("Y");
		boolean use_credential = notificationVO.getRef_mail().getEnable_connection() != null && notificationVO.getRef_mail().getEnable_connection().equalsIgnoreCase("Y");

		SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getPort_number(), notificationVO.getRef_mail().getEmail_sender(),
				usersVO.getMail(), notificationVO.getNotification_subject(), message, use_smtps, use_credential,notificationVO.getRef_mail().getLogin(), notificationVO.getRef_mail().getPassword());
		
		logger.info("PowerCardV3 : createUsersService, User Created <" + usersVO.getUser_code() + "> , USER :"
				+ ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId() + " , RemoteAddress:"
				+ ctx.getDetails().getRemoteAddress());
        return ""+users1.getUsers_id();
    }

    /**
    * update a Users entity .
    *
    * @param UsersVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String updateUsersService(ServiceContext ctx, UsersVO usersVO)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:updateUsersService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Users users = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(UsersProperties.users_id(),
                usersVO.getUsers_id()));

        List<Users> list = this.getUsersRepository().findByCondition(con);

        if (list.size() > 0) {
            users = list.get(0);
        } else {
            throw new OurException("0001", new UsersNotFoundException(""));
        }

        if (usersVO.getProfile_fk() != null) {
        	if(!usersVO.getProfile_fk().equals(users.getFk_profile().getProfile_id())) {
        		 logger.info("PowerCardV3 : Operation:updateUsersService, Changing Profile of user_id : "+ usersVO.getUsers_id() +" from : "+users.getFk_profile().getProfile_id()+" to : "+usersVO.getProfile_fk()+
        				 	" , USER : " + ctx.getUserId() + " , SessionID :" + sessionID +
        	                " , RemoteAddress:" + remoteAddress);
        	}
            users.setFk_profile(new Profile(usersVO.getProfile_fk()));
        }
        
        if (usersVO.getCountry_fk() != null &&
              !usersVO.getCountry_fk().equals("")) {
            users.setFk_country(new Country(usersVO.getCountry_fk()));

        } else {
            if ("".equals(usersVO.getCountry_fk())) {
                users.setFk_country(null);
            }
        }

        if (usersVO.getInstitution_fk() != null &&
              !usersVO.getInstitution_fk().equals("")) {
        	
        	List<String> banks = (List<String>) ctx.getProperty("bankDataAccess");
        	
        	if (banks != null && banks.size() > 0 && !banks.contains(usersVO.getInstitution_fk())) {

        		throw new OurException("0403", new Exception("Bank data access restriction"));

        	} else {
                users.setFk_users_04(new Bank(usersVO.getInstitution_fk()));            		
            }
            
        } else {
            if ("".equals(usersVO.getInstitution_fk())) {
                users.setFk_users_04(null);
            }
        }
      
        if (usersVO.getUser_code() != null && 
                !usersVO.getUser_code().equals(users.getUser_code())) {
            throw new Exception("Cannot update user code");
        }

        if (usersVO.getData_access_fk() != null) {
            users.setFk_data_access(new Data_access(usersVO.getData_access_fk()));

        } else {
        }

        if (usersVO.getLanguage_fk() != null &&
              !usersVO.getLanguage_fk().equals("")) {
            users.setFk_language(new Language_list(usersVO.getLanguage_fk()));

        } else {
            if ("".equals(usersVO.getLanguage_fk())) {
                users.setFk_language(null);
            }
        }

        if (usersVO.getUser_name() != null) {
            users.setUser_name(usersVO.getUser_name());
        }

        if (usersVO.getStatus() != null) {
            users.setStatus(usersVO.getStatus());
        }

        if (usersVO.getStaff_indicateur() != null) {
            users.setStaff_indicateur(usersVO.getStaff_indicateur());
        }

        if (usersVO.getJob_title() != null) {
            users.setJob_title(usersVO.getJob_title());
        }

        if (usersVO.getMail() != null) {
            users.setMail(usersVO.getMail());
        }

        if (usersVO.getActiv_email() != null) {
            users.setActiv_email(usersVO.getActiv_email());
        }

        if (usersVO.getEmploye_number() != null) {
            users.setEmploye_number(usersVO.getEmploye_number());
        }

        if (usersVO.getAccess_by() != null) {
            users.setAccess_by(usersVO.getAccess_by());
        }

        if (usersVO.getAccount_expiry_date() != null) {
            users.setAccount_expiry_date(usersVO.getAccount_expiry_date());
        }

        if (usersVO.getAccount_start_date() != null) {
            users.setAccount_start_date(usersVO.getAccount_start_date());
        }

        if (usersVO.getAccount_end_date() != null || usersVO.getAccount_start_date() != null) { // When account is reopened account_end_date must be set to null
            users.setAccount_end_date(usersVO.getAccount_end_date());
        }

        if (usersVO.getPrivilege_start_date() != null) {
            users.setPrivilege_start_date(usersVO.getPrivilege_start_date());
        }

        if (usersVO.getPrivilege_end_date() != null) {
            users.setPrivilege_end_date(usersVO.getPrivilege_end_date());
        }

        if (usersVO.getPrivilege_last_connexion_date() != null) {
            users.setPrivilege_last_connexion_date(usersVO.getPrivilege_last_connexion_date());
        }

        if (usersVO.getAccess_resriction() != null) {
            users.setAccess_resriction(usersVO.getAccess_resriction());
        }

        if (usersVO.getDis_notification_type() != null) {
            users.setDis_notification_type(usersVO.getDis_notification_type());
        }

        if (usersVO.getBrowser_disconnection() != null) {
            users.setBrowser_disconnection(usersVO.getBrowser_disconnection());
        }

        if (usersVO.getTimer_browser_disconnection() != null) {
            users.setTimer_browser_disconnection(usersVO.getTimer_browser_disconnection());
        }

        if (usersVO.getPwc_disconnection() != null) {
            users.setPwc_disconnection(usersVO.getPwc_disconnection());
        }

        if (usersVO.getTimer_pwc_disconnection() != null) {
            users.setTimer_pwc_disconnection(usersVO.getTimer_pwc_disconnection());
        }

        if (usersVO.getPhone_number() != null) {
            users.setPhone_number(usersVO.getPhone_number());
        }

        if (usersVO.getPassword() != null) {
            users.setPassword(usersVO.getPassword());
        }

        if (usersVO.getBranch_fk() != null) {
            users.setBranch_fk(usersVO.getBranch_fk());
        }

        if (usersVO.getBranch_group_fk() != null) {
            users.setBranch_group_fk(usersVO.getBranch_group_fk());
        }

        if (usersVO.getBoss_fk() != null) {
            users.setBoss_fk(usersVO.getBoss_fk());
        }

        if (usersVO.getDepartement_fk() != null) {
            users.setDepartement_fk(usersVO.getDepartement_fk());
        }

        if (usersVO.getSub_departement_fk() != null) {
            users.setSub_departement_fk(usersVO.getSub_departement_fk());
        }

        if (usersVO.getConnection_status() != null) {
            users.setConnection_status(usersVO.getConnection_status());
        }

        if (usersVO.getUser_collection_list() != null) {
            users.setUser_collection_list(usersVO.getUser_collection_list());
        }

        if (usersVO.getCollection_process_privilege() != null) {
            users.setCollection_process_privilege(usersVO.getCollection_process_privilege());
        }

        if (usersVO.getCollection_dispatch_privilege() != null) {
            users.setCollection_dispatch_privilege(usersVO.getCollection_dispatch_privilege());
        }

        if (usersVO.getDba_privilege() != null) {
            users.setDba_privilege(usersVO.getDba_privilege());
        }

        if (usersVO.getStart_date() != null) {
            users.setStart_date(usersVO.getStart_date());
        }

        if (usersVO.getEnd_date() != null) {
            users.setEnd_date(usersVO.getEnd_date());
        }

        if (usersVO.getLast_db_connect() != null) {
            users.setLast_db_connect(usersVO.getLast_db_connect());
        }

        if (usersVO.getBank_card_batch() != null) {
            users.setBank_card_batch(usersVO.getBank_card_batch());
        }

        if (usersVO.getCurrent_card_batch() != null) {
            users.setCurrent_card_batch(usersVO.getCurrent_card_batch());
        }

        if (usersVO.getDate_cur_card_batch() != null) {
            users.setDate_cur_card_batch(usersVO.getDate_cur_card_batch());
        }

        if (usersVO.getIp_address_access() != null) {
            users.setIp_address_access(usersVO.getIp_address_access());
        }

        if (usersVO.getBank_code_access_list() != null) {
            users.setBank_code_access_list(usersVO.getBank_code_access_list());
        }

        if (usersVO.getIncrease_limits_currency() != null) {
            users.setIncrease_limits_currency(usersVO.getIncrease_limits_currency());
        }

        users.setIncrease_credit_limit_perc(usersVO.getIncrease_credit_limit_perc());

        users.setIncrease_credit_limit_max(usersVO.getIncrease_credit_limit_max());

        users.setIncrease_cash_limit_perc(usersVO.getIncrease_cash_limit_perc());

        users.setIncrease_cash_limit_max(usersVO.getIncrease_cash_limit_max());

        users.setIncrease_loan_limit_perc(usersVO.getIncrease_loan_limit_perc());

        users.setIncrease_loan_limit_max(usersVO.getIncrease_loan_limit_max());

        if (usersVO.getBalances_hidden_flag() != null) {
            users.setBalances_hidden_flag(usersVO.getBalances_hidden_flag());
        }

        if (usersVO.getScreen_show_name() != null) {
            users.setScreen_show_name(usersVO.getScreen_show_name());
        }

        if (usersVO.getScreen_show_db() != null) {
            users.setScreen_show_db(usersVO.getScreen_show_db());
        }

        if (usersVO.getScreen_show_db_connect() != null) {
            users.setScreen_show_db_connect(usersVO.getScreen_show_db_connect());
        }

        if (usersVO.getCheck_sum() != null) {
            users.setCheck_sum(usersVO.getCheck_sum());
        }

        if (usersVO.getForms_message_level() != null) {
            users.setForms_message_level(usersVO.getForms_message_level());
        }

        if (usersVO.getClaims_grouping_index() != null) {
            users.setClaims_grouping_index(usersVO.getClaims_grouping_index());
        }

        if (usersVO.getUser_terminal_group() != null) {
            users.setUser_terminal_group(usersVO.getUser_terminal_group());
        }
		
        //When user is being blocked date_unblocking is null and vis versa
		/*if (usersVO.getDate_blocking() != null || usersVO.getDate_unblocking() != null) {
            users.setDate_blocking(usersVO.getDate_blocking());
        }

        if (usersVO.getDate_unblocking() != null || usersVO.getDate_blocking() != null) {
            users.setDate_unblocking(usersVO.getDate_unblocking());
        }*/        
        
        if (usersVO.getUser_unblocking() != null) {
            users.setUser_unblocking(usersVO.getUser_unblocking());
        }      
        
        /*if (usersVO.getIs_blocked() != null) {
            users.setIs_blocked(usersVO.getIs_blocked());
        }*/

        try {
        	Users users1 = this.getUsersRepository().save(users);
    	}catch(Exception e) {
            logger.error("PowerCardV3 : Operation:updateUsersService , error: "+ e.getMessage());
    		throw new Exception(e.getMessage());
    	}
        
        return "0000";

    }

    /**
    * delete a Users entity .
    *
    * @param UsersVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String deleteUsersService(ServiceContext ctx, UsersVO usersVO)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:deleteUsersService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Users users = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(UsersProperties.users_id(),
                usersVO.getUsers_id()));

        List<Users> list = this.getUsersRepository().findByCondition(con);

        if (list.size() > 0) {
            users = list.get(0);
        } else {
            throw new OurException("0001", new UsersNotFoundException(""));
        }

        if (usersVO.getProfile_fk() != null) {
            users.setFk_profile(new Profile(usersVO.getProfile_fk()));
        }
        
        if (usersVO.getCountry_fk() != null &&
              !usersVO.getCountry_fk().equals("")) {
            users.setFk_country(new Country(usersVO.getCountry_fk()));

        } else {
            if ("".equals(usersVO.getCountry_fk())) {
                users.setFk_country(null);
            }
        }

        if (usersVO.getInstitution_fk() != null &&
              !usersVO.getInstitution_fk().equals("")) {
            users.setFk_users_04(new Bank(usersVO.getInstitution_fk()));

        } else {
            if ("".equals(usersVO.getInstitution_fk())) {
                users.setFk_users_04(null);
            }
        }

        if (usersVO.getData_access_fk() != null) {
            users.setFk_data_access(new Data_access(usersVO.getData_access_fk()));

        } else {
        }

        if (usersVO.getLanguage_fk() != null &&
              !usersVO.getLanguage_fk().equals("")) {
            users.setFk_language(new Language_list(usersVO.getLanguage_fk()));

        } else {
            if ("".equals(usersVO.getLanguage_fk())) {
                users.setFk_language(null);
            }
        }

        this.getUsersRepository().delete(users);

        return "0000";

    }

    /**
    *  Find all entities of a specific type .
    *
    * @return List of UsersVO
    *
    */
    public List<UsersVO> getAllUsersService(ServiceContext ctx)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:getAllUsersService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        List<UsersVO> l = new ArrayList<UsersVO>();

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (ctx != null) {
            UsersUtils.dataFilter(ctx, con);
        }

        List<Users> l_entity = new ArrayList<Users>();

        l_entity = this.getUsersRepository().findByCondition(con);

        l = UsersUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

        return l;

    }

    /**
    * Find entities by conditions
    *
     * @return List of UsersVO
    *
    */
    public List<UsersVO> searchUsersService(ServiceContext ctx, UsersVO usersVO)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:searchUsersService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (ctx != null) {
        	UsersUtils.dataFilter(ctx, con);
        }
        //in case called from changePassword service
        final char[] plainPassword = usersVO.getPassword() != null ? usersVO.getPassword().toCharArray() : null;
        if(usersVO.getPassword() != null) {
        	usersVO.setPassword(null);
        }
        
        UsersUtils.setListOfCriteria(ctx, con, usersVO);
        
        if(usersVO.getIs_blocked() != null && usersVO.getIs_blocked().equalsIgnoreCase("N")) {
        	con.removeIf(c -> c.getPropertyName().equalsIgnoreCase(UsersProperties.is_blocked().getName()));
        	con.add(ConditionalCriteria.or(ConditionalCriteria.like(UsersProperties.is_blocked(), usersVO.getIs_blocked()), ConditionalCriteria.isNull(UsersProperties.is_blocked())));
        }

        List<Users> l_entity = new ArrayList<Users>();
        
        List<UsersVO> l = new ArrayList<UsersVO>();

        l_entity = this.getUsersRepository().findByCondition(con);

      	//using passwordEncoder.matches() to check plainPassword matching with stored passwords
        if(plainPassword != null && new String(plainPassword) != null) {
        	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        	l_entity = l_entity.stream().filter(u->passwordEncoder.matches(new String(plainPassword), u.getPassword())).collect(Collectors.toList());
            java.util.Arrays.fill(plainPassword, '*');
        }
      
        l = UsersUtils.mapListOfEntitiesToVO(ctx, l_entity,0, 0);

        return l;

    }
    
    
    /**
     * reopenAccountService
     *
     * @return String
     *
     */
     public String reopenAccountService(ServiceContext ctx, UsersVO usersVO)
         throws Exception {
         String sessionID = null;
         String remoteAddress = null;
         if (ctx != null) {
             if (ctx.getDetails() != null) {
                 sessionID = ctx.getDetails().getSessionId();
                 remoteAddress = ctx.getDetails().getRemoteAddress();
             }
             logger.info("PowerCardV3 : Operation:reopenAccountService , USER :" +
                 ctx.getUserId() + " , SessionID :" + sessionID +
                 " , RemoteAddress:" + remoteAddress);
         }

         ServiceContextStore.set(ctx);

         Users users = null;

         List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

         con.add(ConditionalCriteria.equal(UsersProperties.users_id(),
                 usersVO.getUsers_id()));

         List<Users> list = this.getUsersRepository().findByCondition(con);

         if (list.size() > 0) {
             users = list.get(0);
             usersVO.setUser_code(users.getUser_code());
             usersVO.setUser_name(users.getUser_name());
             usersVO.setMail(users.getMail());
         } else {
             throw new OurException("0001", new UsersNotFoundException(""));
         }
         
         ////////////////////// Reopening Account //////////////////

         users.setStatus("N");
         users.setAccount_start_date(new Date());
         users.setAccount_end_date(null);
         Users u = this.getUsersRepository().save(users);

        try {
            this.notifyRequester(ctx,usersVO, "00007", "msg_account_reopen_admin");
        }catch(Exception e) {
            logger.error("PowerCardV3 : reopenAccountService , error: "+ e.getMessage());
            throw new Exception(e.getMessage());
        }
         

         logger.info("PowerCardV3 : Admin Validation,  REOPEN_ACCOUNT <" + usersVO.getUser_code() + "> , USER :"
				+ ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId() + " , RemoteAddress:"
				+ ctx.getDetails().getRemoteAddress());
		return "0000";		
		
     }
     
     
    /**
     * closeAccountService
     *
     * @return String
     *
     */
     public String closeAccountService(ServiceContext ctx, UsersVO usersVO)
         throws Exception {
         String sessionID = null;
         String remoteAddress = null;
         if (ctx != null) {
             if (ctx.getDetails() != null) {
                 sessionID = ctx.getDetails().getSessionId();
                 remoteAddress = ctx.getDetails().getRemoteAddress();
             }
             logger.info("PowerCardV3 : Operation:closeAccountService , USER :" +
                 ctx.getUserId() + " , SessionID :" + sessionID +
                 " , RemoteAddress:" + remoteAddress);
         }

         ServiceContextStore.set(ctx);
         
         Users users = null;

         List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

         con.add(ConditionalCriteria.equal(UsersProperties.users_id(),
                 usersVO.getUsers_id()));

         List<Users> list = this.getUsersRepository().findByCondition(con);

         if (list.size() > 0) {
             users = list.get(0);
             usersVO.setUser_code(users.getUser_code());
             usersVO.setUser_name(users.getUser_name());
             usersVO.setMail(users.getMail());
         } else {
             throw new OurException("0001", new UsersNotFoundException(""));
         }
         ////////////////////// Closing Account //////////////////
     
         users.setStatus("C");
         users.setAccount_end_date(new Date());
         Users u = this.getUsersRepository().save(users);

        try {
            this.notifyRequester(ctx,usersVO, "00006", "msg_account_close_admin");
        }catch(Exception e) {
            logger.error("PowerCardV3 : closeAccountService , error: "+ e.getMessage());
            throw new Exception(e.getMessage());
        }
         
 
		logger.info("PowerCardV3 : Admin Validation,  CLOSE_ACCOUNT <" + usersVO.getUser_code() + "> , USER :"
				+ ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId() + " , RemoteAddress:"
				+ ctx.getDetails().getRemoteAddress());
       
		return "0000";		
     }
    
     /**
      * blockUsersService
      *
       * @return String
      *
      */
      public String blockUsersService(ServiceContext ctx, UsersVO usersVO)
          throws Exception {
          String sessionID = null;
          String remoteAddress = null;
          if (ctx != null) {
              if (ctx.getDetails() != null) {
                  sessionID = ctx.getDetails().getSessionId();
                  remoteAddress = ctx.getDetails().getRemoteAddress();
              }
              logger.info("PowerCardV3 : Operation:blockUsersService , USER :" +
                  ctx.getUserId() + " , SessionID :" + sessionID +
                  " , RemoteAddress:" + remoteAddress);
          }

          ServiceContextStore.set(ctx);
          
          Users users = null;

          List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

          con.add(ConditionalCriteria.equal(UsersProperties.users_id(),
                  usersVO.getUsers_id()));

          List<Users> list = this.getUsersRepository().findByCondition(con);

          if (list.size() > 0) {
              users = list.get(0);
              usersVO.setUser_code(users.getUser_code());
              usersVO.setUser_name(users.getUser_name());
              usersVO.setMail(users.getMail());
          } else {
              throw new OurException("0001", new UsersNotFoundException(""));
          }

          ////////////////////// Blocking User //////////////////

          users.setIs_blocked("Y");
          users.setDate_blocking(new Date());
          users.setDate_unblocking(null);
          users.setUser_unblocking(ctx.getUserId());
          Users u = this.getUsersRepository().save(users);

        try {
            this.notifyRequester(ctx,usersVO, "00008", "msg_user_block_admin");
        }catch(Exception e) {
            logger.error("PowerCardV3 : blockUsersService , error: "+ e.getMessage());
            throw new Exception(e.getMessage());
        }
          
  
 		logger.info("PowerCardV3 : Admin Validation,  BLOCK USER <" + usersVO.getUser_code() + "> , USER :"
 				+ ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId() + " , RemoteAddress:"
 				+ ctx.getDetails().getRemoteAddress());
        
 		return "Date block : " + users.getDate_blocking();		
      }
     

      /**
       * unblockUsersService
       *
       * @return String
       *
       */
       public String unblockUsersService(ServiceContext ctx, UsersVO usersVO)
           throws Exception {
           String sessionID = null;
           String remoteAddress = null;
           if (ctx != null) {
               if (ctx.getDetails() != null) {
                   sessionID = ctx.getDetails().getSessionId();
                   remoteAddress = ctx.getDetails().getRemoteAddress();
               }
               logger.info("PowerCardV3 : Operation:unblockUsersService , USER :" +
                   ctx.getUserId() + " , SessionID :" + sessionID +
                   " , RemoteAddress:" + remoteAddress);
           }

           ServiceContextStore.set(ctx);
           
           Users users = null;

           List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

           con.add(ConditionalCriteria.equal(UsersProperties.users_id(),
                   usersVO.getUsers_id()));

           List<Users> list = this.getUsersRepository().findByCondition(con);

           if (list.size() > 0) {
               users = list.get(0);
               usersVO.setUser_code(users.getUser_code());
               usersVO.setUser_name(users.getUser_name());
               usersVO.setMail(users.getMail());
           } else {
               throw new OurException("0001", new UsersNotFoundException(""));
           }
           ////////////////////// Unlocking User //////////////////

           users.setIs_blocked("N");
           users.setDate_unblocking(new Date());
           users.setDate_blocking(null);
           users.setUser_unblocking(ctx.getUserId());
           Users u = this.getUsersRepository().save(users);
           
           //Reset user_passwords invalide_authentif_num to zero
           WebApplicationContext context = ApplicationContextProcessor.getContext();

           User_passwordsService user_passwordsService = (User_passwordsService) context.getBean("user_passwordsService");

           User_passwordsVO userPasswordVO = new User_passwordsVO();
           userPasswordVO.setLogin(users.getUser_code());
           
   		   List<User_passwordsVO> userPasswords = user_passwordsService.searchUser_passwordsService(ctx,userPasswordVO);

   		   if(userPasswords.size() >0 ) {
   			   User_passwordsVO user_passwords = userPasswords.get(0);
	 		   user_passwords.setInvalide_authentif_num("0");
	 		   user_passwordsService.updateUser_passwordsService(ctx,user_passwords);
   		   }

           try {
	           	this.notifyRequester(ctx, usersVO, "00009", "msg_user_unblock_admin");
	       	}catch(Exception e) {
                logger.error("PowerCardV3 : unblockUsersService , error: "+ e.getMessage());
	       		throw new Exception(e.getMessage());
	       	}
           
  		logger.info("PowerCardV3 : Admin Validation,  UNBLOCK USER <" + usersVO.getUser_code() + "> , USER :"
  				+ ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId() + " , RemoteAddress:"
  				+ ctx.getDetails().getRemoteAddress());
         
  		return "Date unblock : " + users.getDate_unblocking();		
       }
      
     
    /**
     * Init User Password
     *
     * @return String
     *
     */
     public String initPasswordService(ServiceContext ctx, UsersVO usersVO)
         throws Exception {
         String sessionID = null;
         String remoteAddress = null;
         if (ctx != null) {
             if (ctx.getDetails() != null) {
                 sessionID = ctx.getDetails().getSessionId();
                 remoteAddress = ctx.getDetails().getRemoteAddress();
             }
             logger.info("PowerCardV3 : Operation:initPasswordService , USER :" +
                 ctx.getUserId() + " , SessionID :" + sessionID +
                 " , RemoteAddress:" + remoteAddress);
         }

         ServiceContextStore.set(ctx);

         Users users = null;

         List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

         con.add(ConditionalCriteria.equal(UsersProperties.users_id(),
                 usersVO.getUsers_id()));

         List<Users> list = this.getUsersRepository().findByCondition(con);

         if (list.size() > 0) {
             users = list.get(0);
             usersVO.setUser_code(users.getUser_code());
             usersVO.setUser_name(users.getUser_name());
             usersVO.setMail(users.getMail());
         } else {
             throw new OurException("0001", new UsersNotFoundException(""));
         }
         
         ////////////////////// Password Initialization //////////////////
         
         char[] newPassword = generatePassword().toCharArray();
       	 String encryptedPass = encryptionService.encryptPassword(ctx, new String(newPassword), usersVO.getUser_code());
         users.setPassword(encryptedPass);
         users.setStatus("F");
         Users u = this.getUsersRepository().save(users);
         
       	///////////////////// CREATING USER_PASSWORD /////////////////////////
        User_passwordsVO userPasswordsVO = new User_passwordsVO();
        userPasswordsVO.setDate_change(new Timestamp(new Date().getTime()));
        userPasswordsVO.setLogin(usersVO.getUser_code());
        userPasswordsVO.setPassword(encryptedPass);
        userPasswordsVO.setUsers_fk(u.getUsers_id());
        userPasswordsVO.setInvalide_authentif_num("0");

        WebApplicationContext context = ApplicationContextProcessor.getContext();

        User_passwordsService user_passwordsService = (User_passwordsService) context.getBean("user_passwordsService");

        try {
            user_passwordsService.createUser_passwordsService(ctx, userPasswordsVO);
    	}catch(Exception e) {
            logger.error("PowerCardV3 : initPasswordService , error: "+ e.getMessage());
    		throw new Exception(e.getMessage());
    	}
       
       

        try {
            this.notifyRequester(ctx,usersVO, "00003", "msg_pass_update_admin");
        }catch(Exception e) {
            logger.error("PowerCardV3 : initPasswordService , error: "+ e.getMessage());
            throw new Exception(e.getMessage());
        }
        
 
     	String message = "";
	    String language = "";
	    NotificationVO notificationVO = new NotificationVO();
		notificationVO.setNotification_code("00003");
		notificationVO.setLazy_level(1);
		notificationVO = this.notificationService.searchNotificationService(ctx, notificationVO).get(0);
		
     	Ressource_bundleVO ressource_bundleVO = new Ressource_bundleVO();
        List<Ressource_bundleVO> list_ressource_bundle = new ArrayList<Ressource_bundleVO>();

		if (usersVO.getLanguage_fk().equals("SPA")) {
			language = "es_ES";
		} else if (usersVO.getLanguage_fk().equals("FRE")) {
			language = "fr_FR";
		} else {
			language = "en_US";
		}

		ressource_bundleVO.setLocale_chain(language);
		ressource_bundleVO.setBundle("NOTIFICATION");
		ressource_bundleVO.setKey_val("msg_pass_update_user");
		list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
		list_ressource_bundle = ressource_bundleService.searchRessource_bundleService(ctx,
				ressource_bundleVO);
		if (list_ressource_bundle.size() > 0) {
			ressource_bundleVO = list_ressource_bundle.get(0);
			message = ressource_bundleVO.getValue();
			if (!message.isEmpty()) {
				message = message.replace("$USER_CODE", usersVO.getUser_code());
                message = message.replace("$USER_NAME", usersVO.getUser_name());
				message = message.replace("$USER_PASSWORD", new String(newPassword));
			}
		} else {
			message = notificationVO.getContent() + " : " + new String(newPassword);
		}
		java.util.Arrays.fill(newPassword, '*');
		boolean use_smtps = notificationVO.getRef_mail().getEnable_smtps() != null && notificationVO.getRef_mail().getEnable_smtps().equalsIgnoreCase("Y");
		boolean use_credential = notificationVO.getRef_mail().getEnable_connection() != null && notificationVO.getRef_mail().getEnable_connection().equalsIgnoreCase("Y");
		SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getPort_number(), notificationVO.getRef_mail().getEmail_sender(),
				usersVO.getMail(), notificationVO.getNotification_subject(), message, use_smtps,use_credential,notificationVO.getRef_mail().getLogin(), notificationVO.getRef_mail().getPassword());

		logger.info("PowerCardV3 : Init Password Service,  Password Intialized <" + usersVO.getUser_code() + "> , USER :"
				+ ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId() + " , RemoteAddress:"
				+ ctx.getDetails().getRemoteAddress());
			
			
         return "0000";
     }
     
     
    public void notifyRequester(ServiceContext ctx, UsersVO usersVO, String notifCode, String rb_kv) throws Exception{
		 
    	UsersVO requester = new UsersVO();
		requester.setUser_code(ctx.getUserId());
		requester = this.searchUsersService(ctx, requester).get(0);
	
	    NotificationVO notificationVO = new NotificationVO();
	    notificationVO.setNotification_code(notifCode);
	    notificationVO.setLazy_level(1);
	    notificationVO = notificationService.searchNotificationService(ctx, notificationVO).get(0);
	    String language = "";
	    String message = "";
	
	    if (requester.getLanguage_fk().equals("SPA")) {
			language = "es_ES";
	    } else if (requester.getLanguage_fk().equals("FRE")) {
				language = "fr_FR";
	    } else {
				language = "en_US";
	    }
	    
	    Ressource_bundleVO ressource_bundleVO = new Ressource_bundleVO();
	    ressource_bundleVO.setLocale_chain(language);
	    ressource_bundleVO.setBundle("NOTIFICATION");
	    ressource_bundleVO.setKey_val(rb_kv);
		
        List<Ressource_bundleVO> list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
		list_ressource_bundle = ressource_bundleService.searchRessource_bundleService(ctx,ressource_bundleVO);
		
		if (list_ressource_bundle.size() > 0) {
			ressource_bundleVO = list_ressource_bundle.get(0);
			message = ressource_bundleVO.getValue();
			if (!message.isEmpty()){
				message = message.replace("$USER_CODE", usersVO.getUser_code());
                message = message.replace("$USER_NAME", usersVO.getUser_name());
            }
		} else {
			message = notificationVO.getContent();
		}
		boolean use_smtps = notificationVO.getRef_mail().getEnable_smtps() != null && notificationVO.getRef_mail().getEnable_smtps().equalsIgnoreCase("Y");
		boolean use_credential = notificationVO.getRef_mail().getEnable_connection() != null && notificationVO.getRef_mail().getEnable_connection().equalsIgnoreCase("Y");
		SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getPort_number(), notificationVO.getRef_mail().getEmail_sender(),
				requester.getMail(), notificationVO.getNotification_subject(), message, use_smtps,use_credential,notificationVO.getRef_mail().getLogin(), notificationVO.getRef_mail().getPassword());
			
    }
     
 	public static void send(String smtpHost, int smtpPort, String from, String to, String subject, String content)
			throws AddressException, MessagingException {

		java.util.Properties props = new java.util.Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", "" + smtpPort);
		Session session = Session.getDefaultInstance(props, null);

		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(from));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		msg.setSubject(subject);
		msg.setText(content);

		Transport.send(msg);
	}
     
 	public void SendMail(String host, String port, String from, String add_dest, String message_objet, String message_corps, boolean use_smtps, boolean use_credential , String login, String password)
			throws Exception {

		//logger.info("PowerCardV3 : Admin Validation,  Sending Mail : OBJECT " + message_objet + "MESSAGE CORPS : "
		//		+ message_corps + " ,TO :" + add_dest + " from : " + from + " using host :" + host);

		boolean debug = false;
		Properties props = new Properties();
		Session session = null;
        if(use_credential) {
        	
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.socketFactory.port", port);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
               
               
        	session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(login, password);
                }
            });

        }
 
        else {
        	
        	//will need to get it from mail_config
        	if(use_smtps) props.put("mail.transport.protocol", "smtps");
      
        	props.put("mail.smtp.host", host);
        	props.put("mail.smtp.port", port);
        	// Time out parameters
        	props.put("mail.smtp.timeout", "60000");
        	props.put("mail.smtp.connectiontimeout", "60000");

        	session = Session.getDefaultInstance(props);
     	
        }
        session.setDebug(debug);
		Message mesg = new MimeMessage(session);
		
		mesg.addRecipient(Message.RecipientType.TO, new InternetAddress(add_dest));
		mesg.setFrom(new InternetAddress(from));
		mesg.setSubject(message_objet);
		mesg.setText(message_corps);
		logger.info("PowerCardV3 : Admin Validation,  Start sending Mail TO :" + add_dest);
		try {
			if(use_smtps) {
              	Transport transport = session.getTransport();
	        	transport.connect(host, Integer.parseInt(port), null, null);
	        	transport.sendMessage(mesg,mesg.getRecipients(Message.RecipientType.TO));
	        	transport.close();
            } else {
              	Transport.send(mesg);
            }
		} catch (MessagingException e) {
			// Adding trace when mail is not sent
			logger.info("PowerCardV3 : Admin Validation, Error when sending mail :" + e.getMessage());
			throw new OurException("1259", new Exception(e.getMessage()));
		}
		logger.info("PowerCardV3 : Admin Validation,  End sending Mail TO :" + add_dest);

	}

 	public static void setUsersProperties(Users users, UsersVO usersVO) {

        users.setUser_name(usersVO.getUser_name());

        users.setUser_code(usersVO.getUser_code());

//      users.setStatus(usersVO.getStatus());

        users.setStaff_indicateur(usersVO.getStaff_indicateur());

        users.setJob_title(usersVO.getJob_title());

        users.setMail(usersVO.getMail());

        users.setActiv_email(usersVO.getActiv_email());

        users.setEmploye_number(usersVO.getEmploye_number());

        users.setAccess_by(usersVO.getAccess_by());

        users.setAccount_expiry_date(usersVO.getAccount_expiry_date());

        users.setAccount_start_date(usersVO.getAccount_start_date());

        users.setAccount_end_date(usersVO.getAccount_end_date());

        users.setPrivilege_start_date(usersVO.getPrivilege_start_date());

        users.setPrivilege_end_date(usersVO.getPrivilege_end_date());

        users.setPrivilege_last_connexion_date(usersVO.getPrivilege_last_connexion_date());

        users.setAccess_resriction(usersVO.getAccess_resriction());

        users.setDis_notification_type(usersVO.getDis_notification_type());

        users.setBrowser_disconnection(usersVO.getBrowser_disconnection());

        users.setTimer_browser_disconnection(usersVO.getTimer_browser_disconnection());

        users.setPwc_disconnection(usersVO.getPwc_disconnection());

        users.setTimer_pwc_disconnection(usersVO.getTimer_pwc_disconnection());

        users.setPhone_number(usersVO.getPhone_number());

        //users.setPassword(usersVO.getPassword());

        users.setBranch_fk(usersVO.getBranch_fk());

        users.setBranch_group_fk(usersVO.getBranch_group_fk());

        users.setBoss_fk(usersVO.getBoss_fk());

        users.setDepartement_fk(usersVO.getDepartement_fk());

        users.setSub_departement_fk(usersVO.getSub_departement_fk());

        users.setConnection_status(usersVO.getConnection_status());

        users.setUser_collection_list(usersVO.getUser_collection_list());

        users.setCollection_process_privilege(usersVO.getCollection_process_privilege());

        users.setCollection_dispatch_privilege(usersVO.getCollection_dispatch_privilege());

        users.setDba_privilege(usersVO.getDba_privilege());

        users.setStart_date(usersVO.getStart_date());

        users.setEnd_date(usersVO.getEnd_date());

        users.setLast_db_connect(usersVO.getLast_db_connect());

        users.setBank_card_batch(usersVO.getBank_card_batch());

        users.setCurrent_card_batch(usersVO.getCurrent_card_batch());

        users.setDate_cur_card_batch(usersVO.getDate_cur_card_batch());

        users.setIp_address_access(usersVO.getIp_address_access());

        users.setBank_code_access_list(usersVO.getBank_code_access_list());

        users.setIncrease_limits_currency(usersVO.getIncrease_limits_currency());

        users.setIncrease_credit_limit_perc(usersVO.getIncrease_credit_limit_perc());

        users.setIncrease_credit_limit_max(usersVO.getIncrease_credit_limit_max());

        users.setIncrease_cash_limit_perc(usersVO.getIncrease_cash_limit_perc());

        users.setIncrease_cash_limit_max(usersVO.getIncrease_cash_limit_max());

        users.setIncrease_loan_limit_perc(usersVO.getIncrease_loan_limit_perc());

        users.setIncrease_loan_limit_max(usersVO.getIncrease_loan_limit_max());

        users.setBalances_hidden_flag(usersVO.getBalances_hidden_flag());

        users.setScreen_show_name(usersVO.getScreen_show_name());

        users.setScreen_show_db(usersVO.getScreen_show_db());

        users.setScreen_show_db_connect(usersVO.getScreen_show_db_connect());

        users.setCheck_sum(usersVO.getCheck_sum());

        users.setForms_message_level(usersVO.getForms_message_level());

        users.setClaims_grouping_index(usersVO.getClaims_grouping_index());

        users.setUser_terminal_group(usersVO.getUser_terminal_group());
 	}
 	
 	private String generatePassword() throws Exception {

		java.util.Random r = new java.util.Random();
		final char[] goodChar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't',
				'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R',
				'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		final char[] goodDigit = { '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		final char[] goodSpecialCaracter = { '+', '-', '@', '#', '*' };
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < 2; i++) {
			sb.append(goodChar[r.nextInt(goodChar.length)]);
		}
		for (int i = 2; i < 3; i++) {
			sb.append(goodDigit[r.nextInt(goodDigit.length)]);
		}
		for (int i = 3; i < 4; i++) {
			sb.append(goodSpecialCaracter[r.nextInt(goodSpecialCaracter.length)]);
		}
		for (int i = 4; i < 6; i++) {
			sb.append(goodChar[r.nextInt(goodChar.length)]);
		}
		for (int i = 6; i < 7; i++) {
			sb.append(goodDigit[r.nextInt(goodDigit.length)]);
		}
		for (int i = 7; i < 8; i++) {
			sb.append(goodSpecialCaracter[r.nextInt(goodSpecialCaracter.length)]);
		}

		return sb.toString();
	}

}
