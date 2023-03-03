package ma.hps.powercard.compliance.serviceimpl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.serviceapi.NotificationVO;
import ma.hps.powercard.compliance.serviceapi.User_passwordsVO;
import ma.hps.powercard.compliance.serviceapi.User_requestsVO;
import ma.hps.powercard.compliance.serviceapi.User_requests_histVO;
import ma.hps.powercard.compliance.serviceapi.UsersVO;
import ma.hps.powercard.compliance.serviceapi.Ressource_bundleVO;

import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;

import ma.hps.powercard.compliance.domain.Ldap_param_by_bank;
import ma.hps.powercard.compliance.domain.Ldap_param_by_bankProperties;
import ma.hps.powercard.compliance.domain.Ldap_param_by_bankRepository;
import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of UserRequestsAdminstrationService.
 */
@Lazy
@Service("userRequestsAdminstrationService")
public class UserRequestsAdminstrationServiceImpl extends UserRequestsAdminstrationServiceImplBase {
	private final int USER_CREATE = 1;
	private final int CHANGE_GENERAL_DETAIL = 2;
	private final int INIT_PASSWORD = 3;
	private final int CHANGE_JOB_DETAIL = 4;
	private final int CHANGE_ACCESS_DETAIL = 5;
	private final int CHANGE_ACCOUNT_DETAIL = 6;
	private final int CHANGE_CONNEXION_DETAIL = 7;
	private final int CHANGE_DISCONNECTION_CONFIG = 8;
	private final int CLOSE_ACCOUNT = 9;
	private final int REOPEN_ACCOUNT = 10;

	private static Logger logger = Logger.getLogger(UserRequestsAdminstrationServiceImpl.class);

	private UsersVO usersvo;
	private NotificationVO notificationVO;

	public UserRequestsAdminstrationServiceImpl() {

	}

	public String validateRequest(ServiceContext ctx, User_requestsVO user_requestsVO) throws Exception {
		UsersVO requestervo = new UsersVO();
		UsersVO vo = new UsersVO();
		List<UsersVO> lusers = new ArrayList<UsersVO>();
		Ressource_bundleVO ressource_bundleVO = new Ressource_bundleVO();
		List<Ressource_bundleVO> list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
		String language = "";
		String message = "";

		User_requests_histVO user_requests_hist = convertToHistVO(user_requestsVO);
		user_requests_hist.setRequest_status("V");
		this.getUser_requests_histService().createUser_requests_histService(ctx, user_requests_hist);

		switch (user_requestsVO.getReason_request_fk().intValue()) {
		case USER_CREATE:
			/* Start ASB LDAP MODIF */
			String status_ldap = "";
			List<ConditionalCriteria> conLdap_param_by_bank = new ArrayList<ConditionalCriteria>();
			conLdap_param_by_bank.add(ConditionalCriteria.equal(Ldap_param_by_bankProperties.bank_code(),
					user_requestsVO.getUser_institution_id()));
			Ldap_param_by_bankRepository ldap_param_by_bankRepository = getLdap_param_by_bankRepository();
			List<Ldap_param_by_bank> Ldap_param_by_bankList = ldap_param_by_bankRepository
					.findByCondition(conLdap_param_by_bank);
			if (Ldap_param_by_bankList.size() > 0) {
				status_ldap = Ldap_param_by_bankList.get(0).getStatus_ldap();
			}
			/* End ASB LDAP MODIF */

			logger.info("PowerCardV3 : Admin Validation, User Creation , USER :" + ctx.getUserId() + " , SessionID :"
					+ ctx.getDetails().getSessionId() + " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());

			UsersVO u = convertVO(user_requestsVO);
			String password = u.getPassword();
			u.setPassword(this.getEncryptionService().encryptPassword(ctx, password, u.getUser_code()));
			u.setStatus("F");

			UsersVO usersVO = new UsersVO();
			usersVO.setUser_code(user_requestsVO.getUser_code());
			List<UsersVO> l = this.getUsersService().searchUsersService(ctx, usersVO);
			if (l.size() > 0)
				throw new OurException("0045", new Exception());

			this.getUsersService().createUsersService(ctx, u);
			this.getUser_requestsService().deleteUser_requestsService(ctx, user_requestsVO);
			UsersVO requester = new UsersVO();
			requester.setUser_code(user_requestsVO.getRequester_code());
			requester = this.getUsersService().searchUsersService(ctx, requester).get(0);

			UsersVO newUser = new UsersVO();
			newUser.setUser_code(u.getUser_code());
			newUser = this.getUsersService().searchUsersService(ctx, newUser).get(0);

			User_passwordsVO us = new User_passwordsVO();
			Date date = new Date();
			us.setDate_change(new Timestamp(date.getTime()));
			us.setLogin(u.getUser_code());
			us.setPassword(this.getEncryptionService().encryptPassword(ctx, password, u.getUser_code()));
			us.setUsers_fk(newUser.getUsers_id());
			us.setInvalide_authentif_num("0");
			this.getUser_passwordsService().createUser_passwordsService(ctx, us);

			notificationVO = new NotificationVO();
			notificationVO.setNotification_code("00002");
			notificationVO.setLazy_level(1);
			notificationVO = this.getNotificationService().searchNotificationService(ctx, notificationVO).get(0);
			if (requester.getLanguage_fk().equals("SPA")) {
				language = "es_ES";
			} else if (requester.getLanguage_fk().equals("FRE")) {
				language = "fr_FR";
			} else {
				language = "en_US";
			}
			ressource_bundleVO.setLocale_chain(language);
			ressource_bundleVO.setBundle("NOTIFICATION");
			ressource_bundleVO.setKey_val("msg_user_create_admin");
			list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
			list_ressource_bundle = this.getRessource_bundleService().searchRessource_bundleService(ctx,
					ressource_bundleVO);
			if (list_ressource_bundle.size() > 0) {
				ressource_bundleVO = list_ressource_bundle.get(0);
				message = ressource_bundleVO.getValue();
				if (!message.isEmpty())
					message = message.replace("***", newUser.getUser_code());
			} else {
				message = notificationVO.getContent() + "  : " + user_requestsVO.getUser_code();
			}
			/* Start ASB LDAP MODIF */
			if (!status_ldap.equals("L")) {
				/* End ASB LDAP MODIF */
				SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getEmail_sender(),
						requester.getMail(), notificationVO.getNotification_subject(), message);
			}
			ressource_bundleVO = new Ressource_bundleVO();
			if (newUser.getLanguage_fk().equals("SPA")) {
				language = "es_ES";
			} else if (newUser.getLanguage_fk().equals("FRE")) {
				language = "fr_FR";
			} else {
				language = "en_US";
			}
			ressource_bundleVO.setLocale_chain(language);
			ressource_bundleVO.setBundle("NOTIFICATION");
			ressource_bundleVO.setKey_val("msg_user_create_user");
			list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
			list_ressource_bundle = this.getRessource_bundleService().searchRessource_bundleService(ctx,
					ressource_bundleVO);
			if (list_ressource_bundle.size() > 0) {
				ressource_bundleVO = list_ressource_bundle.get(0);
				message = ressource_bundleVO.getValue();
				if (!message.isEmpty()) {
					message = message.replace("***", newUser.getUser_code());
					message = message.replace("###", password);
				}
			} else {
				message = notificationVO.getContent() + "  : " + user_requestsVO.getUser_code() + " Password  "
						+ password;
			}
			/* Start ASB LDAP MODIF */
			if (!status_ldap.equals("L")) {
				/* End ASB LDAP MODIF */
				SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getEmail_sender(),
						u.getMail(), notificationVO.getNotification_subject(), message);

				logger.info("PowerCardV3 : Admin Validation, User Created <" + u.getUser_code() + "> , USER :"
						+ ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId() + " , RemoteAddress:"
						+ ctx.getDetails().getRemoteAddress());
			}
			break;

		case CHANGE_GENERAL_DETAIL:

			logger.info("PowerCardV3 : Admin Validation, Changing General Detail , USER :" + ctx.getUserId()
					+ " , SessionID :" + ctx.getDetails().getSessionId() + " , RemoteAddress:"
					+ ctx.getDetails().getRemoteAddress());

			usersvo = new UsersVO();
			vo = new UsersVO();
			vo.setUsers_id(user_requestsVO.getUser_id());
			lusers = this.getUsersService().searchUsersService(ctx, vo);
			usersvo = lusers.get(0);
			usersvo.setUser_name(user_requestsVO.getUser_name());
			if (user_requestsVO.getUser_branch_id() != null) {
				usersvo.setBranch_fk(user_requestsVO.getUser_branch_id());
			} else {
				usersvo.setBranch_fk("");
			}
			if (user_requestsVO.getUser_institution_id() != null) {
				usersvo.setInstitution_fk(user_requestsVO.getUser_institution_id());
			} else {
				usersvo.setInstitution_fk("");
			}
			
			if (user_requestsVO.getUser_profile_id() != null && !user_requestsVO.getUser_profile_id().equals("")) {
				usersvo.setProfile_fk(user_requestsVO.getUser_profile_id());
			} else {
				usersvo.setProfile_fk(null);
			}
			
			if (user_requestsVO.getUser_language_id() != null) {
				usersvo.setLanguage_fk(user_requestsVO.getUser_language_id());
			} else {
				usersvo.setLanguage_fk("");
			}
			if (user_requestsVO.getUser_branch_group_id() != null) {
				usersvo.setBranch_group_fk(user_requestsVO.getUser_branch_group_id());
			} else {
				usersvo.setBranch_group_fk("");
			}
			if (user_requestsVO.getUser_country_id() != null) {
				usersvo.setCountry_fk(user_requestsVO.getUser_country_id());
			} else {
				usersvo.setCountry_fk("");
			}
			if (user_requestsVO.getUser_sub_departement_id() != null) {
				usersvo.setSub_departement_fk(user_requestsVO.getUser_sub_departement_id());
			} else {
				usersvo.setSub_departement_fk("");
			}
			if (user_requestsVO.getUser_departement_id() != null) {
				usersvo.setDepartement_fk(user_requestsVO.getUser_departement_id());
			} else {
				usersvo.setDepartement_fk("");
			}
			if (user_requestsVO.getUser_boss_id() != null && !user_requestsVO.getUser_boss_id().equals("")) {
				usersvo.setBoss_fk(user_requestsVO.getUser_boss_id());
			} else {
				usersvo.setBoss_fk(null);
			}
			this.getUsersService().updateUsersService(ctx, usersvo);
			this.getUser_requestsService().deleteUser_requestsService(ctx, user_requestsVO);
			requestervo.setUser_code(user_requestsVO.getRequester_code());
			requester = this.getUsersService().searchUsersService(ctx, requestervo).get(0);
			notificationVO = new NotificationVO();
			notificationVO.setNotification_code("00005");
			notificationVO.setLazy_level(1);
			notificationVO = this.getNotificationService().searchNotificationService(ctx, notificationVO).get(0);
			if (requester.getLanguage_fk().equals("SPA")) {
				language = "es_ES";
			} else if (requester.getLanguage_fk().equals("FRE")) {
				language = "fr_FR";
			} else {
				language = "en_US";
			}
			ressource_bundleVO.setLocale_chain(language);
			ressource_bundleVO.setBundle("NOTIFICATION");
			ressource_bundleVO.setKey_val("msg_user_update_admin");
			list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
			list_ressource_bundle = this.getRessource_bundleService().searchRessource_bundleService(ctx,
					ressource_bundleVO);
			if (list_ressource_bundle.size() > 0) {
				ressource_bundleVO = list_ressource_bundle.get(0);
				message = ressource_bundleVO.getValue();
				if (!message.isEmpty())
					message = message.replace("***", usersvo.getUser_code());
			} else {
				message = notificationVO.getContent();
			}
			SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getEmail_sender(),
					requester.getMail(), notificationVO.getNotification_subject(), message);

			logger.info("PowerCardV3 : Admin Validation,  General Detail Changed <" + usersvo.getUser_code()
					+ "> , USER :" + ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId()
					+ " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());

			break;

		case INIT_PASSWORD:
			logger.info("PowerCardV3 : Admin Validation, Password Initialization , USER :" + ctx.getUserId()
					+ " , SessionID :" + ctx.getDetails().getSessionId() + " , RemoteAddress:"
					+ ctx.getDetails().getRemoteAddress());
			usersvo = new UsersVO();
			vo = new UsersVO();
			vo.setUsers_id(user_requestsVO.getUser_id());
			lusers = this.getUsersService().searchUsersService(ctx, vo);
			usersvo = lusers.get(0);

			String newPassword = generatePassword();
			usersvo.setPassword(this.getEncryptionService().encryptPassword(ctx, newPassword, usersvo.getUser_code()));
			usersvo.setStatus("F");
			this.getUsersService().updateUsersService(ctx, usersvo);
			this.getUser_requestsService().deleteUser_requestsService(ctx, user_requestsVO);
			requestervo.setUser_code(user_requestsVO.getRequester_code());
			requester = this.getUsersService().searchUsersService(ctx, requestervo).get(0);
			notificationVO = new NotificationVO();
			notificationVO.setNotification_code("00003");
			notificationVO.setLazy_level(1);
			notificationVO = this.getNotificationService().searchNotificationService(ctx, notificationVO).get(0);
			if (requester.getLanguage_fk().equals("SPA")) {
				language = "es_ES";
			} else if (requester.getLanguage_fk().equals("FRE")) {
				language = "fr_FR";
			} else {
				language = "en_US";
			}
			ressource_bundleVO.setLocale_chain(language);
			ressource_bundleVO.setBundle("NOTIFICATION");
			ressource_bundleVO.setKey_val("msg_pass_update_admin");
			list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
			list_ressource_bundle = this.getRessource_bundleService().searchRessource_bundleService(ctx,
					ressource_bundleVO);
			if (list_ressource_bundle.size() > 0) {
				ressource_bundleVO = list_ressource_bundle.get(0);
				message = ressource_bundleVO.getValue();
				if (!message.isEmpty())
					message = message.replace("***", usersvo.getUser_code());
			} else {
				message = notificationVO.getContent();
			}
			SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getEmail_sender(),
					requester.getMail(), notificationVO.getNotification_subject(), message);
			ressource_bundleVO = new Ressource_bundleVO();
			if (usersvo.getLanguage_fk().equals("SPA")) {
				language = "es_ES";
			} else if (usersvo.getLanguage_fk().equals("FRE")) {
				language = "fr_FR";
			} else {
				language = "en_US";
			}
			ressource_bundleVO.setLocale_chain(language);
			ressource_bundleVO.setBundle("NOTIFICATION");
			ressource_bundleVO.setKey_val("msg_pass_update_user");
			list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
			list_ressource_bundle = this.getRessource_bundleService().searchRessource_bundleService(ctx,
					ressource_bundleVO);
			if (list_ressource_bundle.size() > 0) {
				ressource_bundleVO = list_ressource_bundle.get(0);
				message = ressource_bundleVO.getValue();
				if (!message.isEmpty()) {
					message = message.replace("***", usersvo.getUser_code());
					message = message.replace("###", newPassword);
				}
			} else {
				message = notificationVO.getContent() + " : " + newPassword;
			}
			SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getEmail_sender(),
					usersvo.getMail(), notificationVO.getNotification_subject(), message);

			logger.info("PowerCardV3 : Admin Validation,  Password Intialized <" + usersvo.getUser_code() + "> , USER :"
					+ ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId() + " , RemoteAddress:"
					+ ctx.getDetails().getRemoteAddress());
			break;

		case CHANGE_JOB_DETAIL:
			logger.info("PowerCardV3 : Admin Validation,  CHANGE_JOB_DETAIL <" + user_requestsVO.getUser_code()
					+ "> , USER :" + ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId()
					+ " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());
			usersvo = new UsersVO();
			vo = new UsersVO();
			vo.setUsers_id(user_requestsVO.getUser_id());
			lusers = this.getUsersService().searchUsersService(ctx, vo);
			usersvo = lusers.get(0);

			usersvo.setMail(user_requestsVO.getMail());
			usersvo.setActiv_email(user_requestsVO.getActiv_email());
			usersvo.setJob_title(user_requestsVO.getJob_title());
			usersvo.setStaff_indicateur(user_requestsVO.getStaff_indicateur());
			usersvo.setEmploye_number(user_requestsVO.getEmploye_number());
			usersvo.setPhone_number(user_requestsVO.getPhone_number());

			this.getUsersService().updateUsersService(ctx, usersvo);
			this.getUser_requestsService().deleteUser_requestsService(ctx, user_requestsVO);
			requestervo.setUser_code(user_requestsVO.getRequester_code());
			requester = this.getUsersService().searchUsersService(ctx, requestervo).get(0);
			notificationVO = new NotificationVO();
			notificationVO.setLazy_level(1);
			notificationVO.setNotification_code("00005");
			notificationVO = this.getNotificationService().searchNotificationService(ctx, notificationVO).get(0);
			if (requester.getLanguage_fk().equals("SPA")) {
				language = "es_ES";
			} else if (requester.getLanguage_fk().equals("FRE")) {
				language = "fr_FR";
			} else {
				language = "en_US";
			}
			ressource_bundleVO.setLocale_chain(language);
			ressource_bundleVO.setBundle("NOTIFICATION");
			ressource_bundleVO.setKey_val("msg_user_update_admin");
			list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
			list_ressource_bundle = this.getRessource_bundleService().searchRessource_bundleService(ctx,
					ressource_bundleVO);
			if (list_ressource_bundle.size() > 0) {
				ressource_bundleVO = list_ressource_bundle.get(0);
				message = ressource_bundleVO.getValue();
				if (!message.isEmpty())
					message = message.replace("***", usersvo.getUser_code());
			} else {
				message = notificationVO.getContent();
			}
			SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getEmail_sender(),
					requester.getMail(), notificationVO.getNotification_subject(), message);
			break;

		case CHANGE_ACCESS_DETAIL:

			logger.info("PowerCardV3 : Admin Validation,  CHANGE_ACCESS_DETAIL <" + user_requestsVO.getUser_code()
					+ "> , USER :" + ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId()
					+ " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());
			usersvo = new UsersVO();
			vo = new UsersVO();
			vo.setUsers_id(user_requestsVO.getUser_id());
			lusers = this.getUsersService().searchUsersService(ctx, vo);
			usersvo = lusers.get(0);

			usersvo.setAccess_by(user_requestsVO.getAccess_by());
			usersvo.setData_access_fk(user_requestsVO.getUser_data_access_id());
			this.getUsersService().updateUsersService(ctx, usersvo);
			this.getUser_requestsService().deleteUser_requestsService(ctx, user_requestsVO);
			requestervo.setUser_code(user_requestsVO.getRequester_code());
			requester = this.getUsersService().searchUsersService(ctx, requestervo).get(0);
			notificationVO = new NotificationVO();
			notificationVO.setNotification_code("00005");
			notificationVO.setLazy_level(1);
			notificationVO = this.getNotificationService().searchNotificationService(ctx, notificationVO).get(0);
			if (requester.getLanguage_fk().equals("SPA")) {
				language = "es_ES";
			} else if (requester.getLanguage_fk().equals("FRE")) {
				language = "fr_FR";
			} else {
				language = "en_US";
			}
			ressource_bundleVO.setLocale_chain(language);
			ressource_bundleVO.setBundle("NOTIFICATION");
			ressource_bundleVO.setKey_val("msg_user_update_admin");
			list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
			list_ressource_bundle = this.getRessource_bundleService().searchRessource_bundleService(ctx,
					ressource_bundleVO);
			if (list_ressource_bundle.size() > 0) {
				ressource_bundleVO = list_ressource_bundle.get(0);
				message = ressource_bundleVO.getValue();
				if (!message.isEmpty())
					message = message.replace("***", usersvo.getUser_code());
			} else {
				message = notificationVO.getContent();
			}

			SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getEmail_sender(),
					requester.getMail(), notificationVO.getNotification_subject(), message);
			break;
		case CHANGE_ACCOUNT_DETAIL:
			logger.info("PowerCardV3 : Admin Validation,  CHANGE_ACCOUNT_DETAIL <" + user_requestsVO.getUser_code()
					+ "> , USER :" + ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId()
					+ " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());
			usersvo = new UsersVO();
			vo = new UsersVO();
			vo.setUsers_id(user_requestsVO.getUser_id());
			lusers = this.getUsersService().searchUsersService(ctx, vo);
			usersvo = lusers.get(0);

			usersvo.setAccount_end_date(user_requestsVO.getAccount_end_date());
			usersvo.setAccount_start_date(user_requestsVO.getAccount_start_date());
			usersvo.setAccount_expiry_date(user_requestsVO.getAccount_expiry_date());
			this.getUsersService().updateUsersService(ctx, usersvo);
			this.getUser_requestsService().deleteUser_requestsService(ctx, user_requestsVO);
			requestervo.setUser_code(user_requestsVO.getRequester_code());
			requester = this.getUsersService().searchUsersService(ctx, requestervo).get(0);
			notificationVO = new NotificationVO();
			notificationVO.setNotification_code("00005");
			notificationVO.setLazy_level(1);
			notificationVO = this.getNotificationService().searchNotificationService(ctx, notificationVO).get(0);
			if (requester.getLanguage_fk().equals("SPA")) {
				language = "es_ES";
			} else if (requester.getLanguage_fk().equals("FRE")) {
				language = "fr_FR";
			} else {
				language = "en_US";
			}
			ressource_bundleVO.setLocale_chain(language);
			ressource_bundleVO.setBundle("NOTIFICATION");
			ressource_bundleVO.setKey_val("msg_user_update_admin");
			list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
			list_ressource_bundle = this.getRessource_bundleService().searchRessource_bundleService(ctx,
					ressource_bundleVO);
			if (list_ressource_bundle.size() > 0) {
				ressource_bundleVO = list_ressource_bundle.get(0);
				message = ressource_bundleVO.getValue();
				if (!message.isEmpty())
					message = message.replace("***", usersvo.getUser_code());
			} else {
				message = notificationVO.getContent();
			}
			SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getEmail_sender(),
					requester.getMail(), notificationVO.getNotification_subject(), message);
			break;

		case CHANGE_CONNEXION_DETAIL:
			logger.info("PowerCardV3 : Admin Validation,  CHANGE_CONNEXION_DETAIL <" + user_requestsVO.getUser_code()
					+ "> , USER :" + ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId()
					+ " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());
			usersvo = new UsersVO();
			vo = new UsersVO();
			vo.setUsers_id(user_requestsVO.getUser_id());
			lusers = this.getUsersService().searchUsersService(ctx, vo);
			usersvo = lusers.get(0);

			usersvo.setAccess_resriction(user_requestsVO.getAccess_resriction());
			usersvo.setPrivilege_start_date(user_requestsVO.getPrivilege_start_date());
			usersvo.setPrivilege_end_date(user_requestsVO.getPrivilege_end_date());
			usersvo.setCollection_process_privilege(user_requestsVO.getCollection_process_privilege());
			usersvo.setCollection_dispatch_privilege(user_requestsVO.getCollection_dispatch_privilege());
			usersvo.setBalances_hidden_flag(user_requestsVO.getBalances_hidden_flag());
			usersvo.setUser_collection_list(user_requestsVO.getUser_collection_list());
			usersvo.setIp_address_access(user_requestsVO.getIp_address_access());
			usersvo.setClaims_grouping_index(user_requestsVO.getClaims_grouping_index());
			usersvo.setUser_terminal_group(user_requestsVO.getUser_terminal_group());
			usersvo.setIncrease_limits_currency(user_requestsVO.getIncrease_limits_currency());
			usersvo.setIncrease_credit_limit_perc(user_requestsVO.getIncrease_credit_limit_perc());
			usersvo.setIncrease_credit_limit_max(user_requestsVO.getIncrease_credit_limit_max());
			usersvo.setIncrease_cash_limit_perc(user_requestsVO.getIncrease_cash_limit_perc());
			usersvo.setIncrease_cash_limit_max(user_requestsVO.getIncrease_cash_limit_max());
			usersvo.setIncrease_loan_limit_perc(user_requestsVO.getIncrease_loan_limit_perc());
			usersvo.setIncrease_loan_limit_max(user_requestsVO.getIncrease_loan_limit_max());
			usersvo.setCurrent_card_batch(user_requestsVO.getCurrent_card_batch());
			usersvo.setScreen_show_name(user_requestsVO.getScreen_show_name());
			usersvo.setScreen_show_db(user_requestsVO.getScreen_show_db());
			usersvo.setScreen_show_db_connect(user_requestsVO.getScreen_show_db_connect());
			usersvo.setForms_message_level(user_requestsVO.getForms_message_level());
			usersvo.setDate_cur_card_batch(user_requestsVO.getDate_cur_card_batch());
			this.getUsersService().updateUsersService(ctx, usersvo);
			this.getUser_requestsService().deleteUser_requestsService(ctx, user_requestsVO);
			requestervo.setUser_code(user_requestsVO.getRequester_code());
			requester = this.getUsersService().searchUsersService(ctx, requestervo).get(0);
			notificationVO = new NotificationVO();
			notificationVO.setNotification_code("00005");
			notificationVO.setLazy_level(1);
			notificationVO = this.getNotificationService().searchNotificationService(ctx, notificationVO).get(0);
			if (requester.getLanguage_fk().equals("SPA")) {
				language = "es_ES";
			} else if (requester.getLanguage_fk().equals("FRE")) {
				language = "fr_FR";
			} else {
				language = "en_US";
			}
			ressource_bundleVO.setLocale_chain(language);
			ressource_bundleVO.setBundle("NOTIFICATION");
			ressource_bundleVO.setKey_val("msg_user_update_admin");
			list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
			list_ressource_bundle = this.getRessource_bundleService().searchRessource_bundleService(ctx,
					ressource_bundleVO);
			if (list_ressource_bundle.size() > 0) {
				ressource_bundleVO = list_ressource_bundle.get(0);
				message = ressource_bundleVO.getValue();
				if (!message.isEmpty())
					message = message.replace("***", usersvo.getUser_code());
			} else {
				message = notificationVO.getContent();
			}
			SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getEmail_sender(),
					requester.getMail(), notificationVO.getNotification_subject(), message);
			break;
		case CHANGE_DISCONNECTION_CONFIG:
			logger.info("PowerCardV3 : Admin Validation,  CHANGE_DISCONNECTION_CONFIG <"
					+ user_requestsVO.getUser_code() + "> , USER :" + ctx.getUserId() + " , SessionID :"
					+ ctx.getDetails().getSessionId() + " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());
			usersvo = new UsersVO();
			vo = new UsersVO();
			vo.setUsers_id(user_requestsVO.getUser_id());
			lusers = this.getUsersService().searchUsersService(ctx, vo);
			usersvo = lusers.get(0);

			usersvo.setPwc_disconnection(user_requestsVO.getPwc_disconnection());
			usersvo.setTimer_pwc_disconnection(user_requestsVO.getTimer_pwc_disconnection());
			usersvo.setBrowser_disconnection(user_requestsVO.getBrowser_disconnection());
			usersvo.setTimer_browser_disconnection(user_requestsVO.getTimer_browser_disconnection());
			usersvo.setDis_notification_type(user_requestsVO.getDis_notification_type());
			this.getUsersService().updateUsersService(ctx, usersvo);
			this.getUser_requestsService().deleteUser_requestsService(ctx, user_requestsVO);
			requestervo.setUser_code(user_requestsVO.getRequester_code());
			requester = this.getUsersService().searchUsersService(ctx, requestervo).get(0);
			notificationVO = new NotificationVO();
			notificationVO.setNotification_code("00005");
			notificationVO.setLazy_level(1);
			notificationVO = this.getNotificationService().searchNotificationService(ctx, notificationVO).get(0);
			if (requester.getLanguage_fk().equals("SPA")) {
				language = "es_ES";
			} else if (requester.getLanguage_fk().equals("FRE")) {
				language = "fr_FR";
			} else {
				language = "en_US";
			}
			ressource_bundleVO.setLocale_chain(language);
			ressource_bundleVO.setBundle("NOTIFICATION");
			ressource_bundleVO.setKey_val("msg_user_update_admin");
			list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
			list_ressource_bundle = this.getRessource_bundleService().searchRessource_bundleService(ctx,
					ressource_bundleVO);
			if (list_ressource_bundle.size() > 0) {
				ressource_bundleVO = list_ressource_bundle.get(0);
				message = ressource_bundleVO.getValue();
				if (!message.isEmpty())
					message = message.replace("***", usersvo.getUser_code());
			} else {
				message = notificationVO.getContent();
			}
			SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getEmail_sender(),
					requester.getMail(), notificationVO.getNotification_subject(), message);
			break;

		case CLOSE_ACCOUNT:
			logger.info("PowerCardV3 : Admin Validation, CLOSE_ACCOUNT , USER :" + ctx.getUserId() + " , SessionID :"
					+ ctx.getDetails().getSessionId() + " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());
			usersvo = new UsersVO();
			vo = new UsersVO();
			vo.setUsers_id(user_requestsVO.getUser_id());
			lusers = this.getUsersService().searchUsersService(ctx, vo);
			usersvo = lusers.get(0);

			usersvo.setStatus("C");
			usersvo.setAccount_end_date(new Date());
			this.getUsersService().updateUsersService(ctx, usersvo);
			this.getUser_requestsService().deleteUser_requestsService(ctx, user_requestsVO);
			requestervo.setUser_code(user_requestsVO.getRequester_code());
			requester = this.getUsersService().searchUsersService(ctx, requestervo).get(0);
			notificationVO = new NotificationVO();
			notificationVO.setNotification_code("00006");
			notificationVO.setLazy_level(1);
			notificationVO = this.getNotificationService().searchNotificationService(ctx, notificationVO).get(0);
			if (requester.getLanguage_fk().equals("SPA")) {
				language = "es_ES";
			} else if (requester.getLanguage_fk().equals("FRE")) {
				language = "fr_FR";
			} else {
				language = "en_US";
			}
			ressource_bundleVO.setLocale_chain(language);
			ressource_bundleVO.setBundle("NOTIFICATION");
			ressource_bundleVO.setKey_val("msg_account_close_admin");
			list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
			list_ressource_bundle = this.getRessource_bundleService().searchRessource_bundleService(ctx,
					ressource_bundleVO);
			if (list_ressource_bundle.size() > 0) {
				ressource_bundleVO = list_ressource_bundle.get(0);
				message = ressource_bundleVO.getValue();
				if (!message.isEmpty())
					message = message.replace("***", usersvo.getUser_code());
			} else {
				message = notificationVO.getContent();
			}
			SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getEmail_sender(),
					requester.getMail(), notificationVO.getNotification_subject(), message);

			logger.info("PowerCardV3 : Admin Validation,  CLOSE_ACCOUNT <" + usersvo.getUser_code() + "> , USER :"
					+ ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId() + " , RemoteAddress:"
					+ ctx.getDetails().getRemoteAddress());
			break;

		case REOPEN_ACCOUNT:
			logger.info("PowerCardV3 : Admin Validation, REOPEN_ACCOUNT , USER :" + ctx.getUserId() + " , SessionID :"
					+ ctx.getDetails().getSessionId() + " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());
			usersvo = new UsersVO();
			vo = new UsersVO();
			vo.setUsers_id(user_requestsVO.getUser_id());
			lusers = this.getUsersService().searchUsersService(ctx, vo);
			usersvo = lusers.get(0);

			usersvo.setStatus("N");
			usersvo.setAccount_start_date(new Date());
			usersvo.setAccount_end_date(null);
			this.getUsersService().updateUsersService(ctx, usersvo);
			this.getUser_requestsService().deleteUser_requestsService(ctx, user_requestsVO);
			requestervo.setUser_code(user_requestsVO.getRequester_code());
			requester = this.getUsersService().searchUsersService(ctx, requestervo).get(0);
			notificationVO = new NotificationVO();
			notificationVO.setNotification_code("00007");
			notificationVO.setLazy_level(1);
			notificationVO = this.getNotificationService().searchNotificationService(ctx, notificationVO).get(0);
			if (requester.getLanguage_fk().equals("SPA")) {
				language = "es_ES";
			} else if (requester.getLanguage_fk().equals("FRE")) {
				language = "fr_FR";
			} else {
				language = "en_US";
			}
			ressource_bundleVO.setLocale_chain(language);
			ressource_bundleVO.setBundle("NOTIFICATION");
			ressource_bundleVO.setKey_val("msg_account_reopen_admin");
			list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
			list_ressource_bundle = this.getRessource_bundleService().searchRessource_bundleService(ctx,
					ressource_bundleVO);
			if (list_ressource_bundle.size() > 0) {
				ressource_bundleVO = list_ressource_bundle.get(0);
				message = ressource_bundleVO.getValue();
				if (!message.isEmpty())
					message = message.replace("***", usersvo.getUser_code());
			} else {
				message = notificationVO.getContent();
			}
			SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getEmail_sender(),
					requester.getMail(), notificationVO.getNotification_subject(), message);

			logger.info("PowerCardV3 : Admin Validation,  REOPEN_ACCOUNT <" + usersvo.getUser_code() + "> , USER :"
					+ ctx.getUserId() + " , SessionID :" + ctx.getDetails().getSessionId() + " , RemoteAddress:"
					+ ctx.getDetails().getRemoteAddress());
			break;
		}

		return "success";

	}

	public String rejectRequest(ServiceContext ctx, User_requestsVO user_requestsVO) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		Ressource_bundleVO ressource_bundleVO = new Ressource_bundleVO();
		List<Ressource_bundleVO> list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
		String language = "";
		String message = "";
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Admin Validation,  Rejecting request <" + user_requestsVO.getUser_code()
					+ "> , USER :" + ctx.getUserId() + " , SessionID :" + sessionID + " , RemoteAddress:"
					+ remoteAddress);
		}

		User_requests_histVO user_requests_hist = convertToHistVO(user_requestsVO);
		user_requests_hist.setRequest_status("R");
		this.getUser_requests_histService().createUser_requests_histService(ctx, user_requests_hist);
		this.getUser_requestsService().deleteUser_requestsService(ctx, user_requestsVO);
		UsersVO requester = new UsersVO();
		requester.setUser_code(user_requestsVO.getRequester_code());
		requester = this.getUsersService().searchUsersService(ctx, requester).get(0);
		notificationVO = new NotificationVO();
		notificationVO.setNotification_code("00001");
		notificationVO.setLazy_level(1);
		notificationVO = this.getNotificationService().searchNotificationService(ctx, notificationVO).get(0);
		if (requester.getLanguage_fk().equals("SPA")) {
			language = "es_ES";
		} else if (requester.getLanguage_fk().equals("FRE")) {
			language = "fr_FR";
		} else {
			language = "en_US";
		}
		ressource_bundleVO.setLocale_chain(language);
		ressource_bundleVO.setBundle("NOTIFICATION");
		ressource_bundleVO.setKey_val("user_request_rejected");
		list_ressource_bundle = new ArrayList<Ressource_bundleVO>();
		list_ressource_bundle = this.getRessource_bundleService().searchRessource_bundleService(ctx,
				ressource_bundleVO);
		if (list_ressource_bundle.size() > 0) {
			ressource_bundleVO = list_ressource_bundle.get(0);
			message = ressource_bundleVO.getValue();
			if (!message.isEmpty())
				message = message.replace("***", user_requestsVO.getRequester_code());
		} else {
			message = notificationVO.getContent();
		}
		SendMail(notificationVO.getRef_mail().getEmail_server(), notificationVO.getRef_mail().getEmail_sender(),
				requester.getMail(), notificationVO.getNotification_subject(), message);

		return "success";
	}

	public UsersVO convertVO(User_requestsVO usersvoconv) throws Exception {
		UsersVO usersvo = new UsersVO();
		usersvo.setAccess_resriction(usersvoconv.getAccess_resriction());
		usersvo.setUser_code(usersvoconv.getUser_code());
		usersvo.setAccount_end_date(usersvoconv.getAccount_end_date());
		usersvo.setPrivilege_end_date(usersvoconv.getPrivilege_end_date());
		usersvo.setMail(usersvoconv.getMail());
		usersvo.setActiv_email(usersvoconv.getActiv_email());
		usersvo.setJob_title(usersvoconv.getJob_title());
		usersvo.setPrivilege_last_connexion_date(new Date());
		usersvo.setStaff_indicateur(usersvoconv.getStaff_indicateur());
		usersvo.setPrivilege_start_date(usersvoconv.getPrivilege_start_date());
		usersvo.setDis_notification_type(usersvoconv.getDis_notification_type());
		usersvo.setAccess_by(usersvoconv.getAccess_by());
		usersvo.setAccount_start_date(usersvoconv.getAccount_start_date());
		usersvo.setAccount_expiry_date(usersvoconv.getAccount_expiry_date());
		usersvo.setUser_name(usersvoconv.getUser_name());
		usersvo.setEmploye_number(usersvoconv.getEmploye_number());
		usersvo.setPwc_disconnection(usersvoconv.getPwc_disconnection());
		usersvo.setTimer_pwc_disconnection(usersvoconv.getTimer_pwc_disconnection());
		usersvo.setPhone_number(usersvoconv.getPhone_number());
		usersvo.setCountry_fk(usersvoconv.getUser_country_id());
		usersvo.setBranch_fk(usersvoconv.getUser_branch_id());
		usersvo.setData_access_fk(usersvoconv.getUser_data_access_id());
		usersvo.setInstitution_fk(usersvoconv.getUser_institution_id());
		usersvo.setProfile_fk(usersvoconv.getUser_profile_id());
		usersvo.setSub_departement_fk(usersvoconv.getUser_sub_departement_id());
		usersvo.setBoss_fk(usersvoconv.getUser_boss_id());
		usersvo.setProfile_fk(usersvoconv.getUser_profile_id());
		usersvo.setBrowser_disconnection(usersvoconv.getBrowser_disconnection());
		usersvo.setTimer_browser_disconnection(usersvoconv.getTimer_browser_disconnection());
		usersvo.setLanguage_fk(usersvoconv.getUser_language_id());
		usersvo.setBranch_group_fk(usersvoconv.getUser_branch_group_id());

		usersvo.setCollection_process_privilege(usersvoconv.getCollection_process_privilege());
		usersvo.setCollection_dispatch_privilege(usersvoconv.getCollection_dispatch_privilege());
		usersvo.setUser_collection_list(usersvoconv.getUser_collection_list());
		usersvo.setDba_privilege(usersvoconv.getDba_privilege());
		usersvo.setStart_date(usersvoconv.getStart_date());
		usersvo.setEnd_date(usersvoconv.getEnd_date());
		usersvo.setLast_db_connect(usersvoconv.getLast_db_connect());
		usersvo.setBank_card_batch(usersvoconv.getBank_card_batch());
		usersvo.setCurrent_card_batch(usersvoconv.getCurrent_card_batch());
		usersvo.setDate_cur_card_batch(usersvoconv.getDate_cur_card_batch());
		usersvo.setIp_address_access(usersvoconv.getIp_address_access());
		usersvo.setBank_code_access_list(usersvoconv.getBank_code_access_list());
		usersvo.setIncrease_limits_currency(usersvoconv.getIncrease_limits_currency());
		usersvo.setIncrease_credit_limit_perc(usersvoconv.getIncrease_credit_limit_perc());
		usersvo.setIncrease_credit_limit_max(usersvoconv.getIncrease_credit_limit_max());
		usersvo.setIncrease_cash_limit_perc(usersvoconv.getIncrease_cash_limit_perc());
		usersvo.setIncrease_cash_limit_max(usersvoconv.getIncrease_cash_limit_max());
		usersvo.setIncrease_loan_limit_perc(usersvoconv.getIncrease_loan_limit_perc());
		usersvo.setIncrease_loan_limit_max(usersvoconv.getIncrease_loan_limit_max());
		usersvo.setBalances_hidden_flag(usersvoconv.getBalances_hidden_flag());
		usersvo.setScreen_show_name(usersvoconv.getScreen_show_name());
		usersvo.setScreen_show_db(usersvoconv.getScreen_show_db());
		usersvo.setScreen_show_db_connect(usersvoconv.getScreen_show_db_connect());
		usersvo.setCheck_sum(usersvoconv.getCheck_sum());
		usersvo.setForms_message_level(usersvoconv.getForms_message_level());
		usersvo.setClaims_grouping_index(usersvoconv.getClaims_grouping_index());
		usersvo.setUser_terminal_group(usersvoconv.getUser_terminal_group());
		usersvo.setDepartement_fk(usersvoconv.getUser_departement_id());

		usersvo.setPassword(generatePassword());

		return usersvo;
	}

	public User_requests_histVO convertToHistVO(User_requestsVO usersvoconv) throws Exception {
		User_requests_histVO user_requests_histvo = new User_requests_histVO();

		user_requests_histvo.setUser_requests_id(usersvoconv.getUser_requests_id());
		user_requests_histvo.setRequester_code(usersvoconv.getRequester_code());
		user_requests_histvo.setRequest_date(usersvoconv.getRequest_date());
		user_requests_histvo.setAccess_resriction(usersvoconv.getAccess_resriction());
		user_requests_histvo.setUser_code(usersvoconv.getUser_code());
		user_requests_histvo.setBrowser_disconnection(usersvoconv.getBrowser_disconnection());
		user_requests_histvo.setUser_id(usersvoconv.getUser_id());
		user_requests_histvo.setAccount_end_date(usersvoconv.getAccount_end_date());
		user_requests_histvo.setPrivilege_end_date(usersvoconv.getPrivilege_end_date());
		user_requests_histvo.setMail(usersvoconv.getMail());
		user_requests_histvo.setActiv_email(usersvoconv.getActiv_email());
		user_requests_histvo.setJob_title(usersvoconv.getJob_title());
		user_requests_histvo.setStatus(usersvoconv.getStatus());
		user_requests_histvo.setStaff_indicateur(usersvoconv.getStaff_indicateur());
		user_requests_histvo.setPrivilege_start_date(usersvoconv.getPrivilege_start_date());
		user_requests_histvo.setDis_notification_type(usersvoconv.getDis_notification_type());
		user_requests_histvo.setAccess_by(usersvoconv.getAccess_by());
		user_requests_histvo.setAccount_start_date(usersvoconv.getAccount_start_date());
		user_requests_histvo.setAccount_expiry_date(usersvoconv.getAccount_expiry_date());
		user_requests_histvo.setUser_name(usersvoconv.getUser_name());
		user_requests_histvo.setEmploye_number(usersvoconv.getEmploye_number());
		user_requests_histvo.setTimer_browser_disconnection(usersvoconv.getTimer_browser_disconnection());
		user_requests_histvo.setPwc_disconnection(usersvoconv.getPwc_disconnection());
		user_requests_histvo.setTimer_pwc_disconnection(usersvoconv.getTimer_pwc_disconnection());
		user_requests_histvo.setPhone_number(usersvoconv.getPhone_number());
		user_requests_histvo.setUser_country_id(usersvoconv.getUser_country_id());
		user_requests_histvo.setCollection_process_privilege(usersvoconv.getCollection_process_privilege());
		user_requests_histvo.setCollection_dispatch_privilege(usersvoconv.getCollection_dispatch_privilege());
		user_requests_histvo.setUser_collection_list(usersvoconv.getUser_collection_list());
		user_requests_histvo.setDba_privilege(usersvoconv.getDba_privilege());
		user_requests_histvo.setStart_date(usersvoconv.getStart_date());
		user_requests_histvo.setEnd_date(usersvoconv.getEnd_date());
		user_requests_histvo.setLast_db_connect(usersvoconv.getLast_db_connect());
		user_requests_histvo.setBank_card_batch(usersvoconv.getBank_card_batch());
		user_requests_histvo.setCurrent_card_batch(usersvoconv.getCurrent_card_batch());
		user_requests_histvo.setDate_cur_card_batch(usersvoconv.getDate_cur_card_batch());
		user_requests_histvo.setIp_address_access(usersvoconv.getIp_address_access());
		user_requests_histvo.setBank_code_access_list(usersvoconv.getBank_code_access_list());
		user_requests_histvo.setIncrease_limits_currency(usersvoconv.getIncrease_limits_currency());
		user_requests_histvo.setIncrease_credit_limit_perc(usersvoconv.getIncrease_credit_limit_perc());
		user_requests_histvo.setIncrease_credit_limit_max(usersvoconv.getIncrease_credit_limit_max());
		user_requests_histvo.setIncrease_cash_limit_perc(usersvoconv.getIncrease_cash_limit_perc());
		user_requests_histvo.setIncrease_cash_limit_max(usersvoconv.getIncrease_cash_limit_max());
		user_requests_histvo.setIncrease_loan_limit_perc(usersvoconv.getIncrease_loan_limit_perc());
		user_requests_histvo.setIncrease_loan_limit_max(usersvoconv.getIncrease_loan_limit_max());
		user_requests_histvo.setBalances_hidden_flag(usersvoconv.getBalances_hidden_flag());
		user_requests_histvo.setScreen_show_name(usersvoconv.getScreen_show_name());
		user_requests_histvo.setScreen_show_db(usersvoconv.getScreen_show_db());
		user_requests_histvo.setScreen_show_db_connect(usersvoconv.getScreen_show_db_connect());
		user_requests_histvo.setCheck_sum(usersvoconv.getCheck_sum());
		user_requests_histvo.setForms_message_level(usersvoconv.getForms_message_level());
		user_requests_histvo.setClaims_grouping_index(usersvoconv.getClaims_grouping_index());
		user_requests_histvo.setUser_terminal_group(usersvoconv.getUser_terminal_group());
		user_requests_histvo.setUser_branch_id(usersvoconv.getUser_branch_id());
		user_requests_histvo.setUser_departement_id(usersvoconv.getUser_departement_id());
		user_requests_histvo.setUser_branch_group_id(usersvoconv.getUser_branch_group_id());
		user_requests_histvo.setUser_boss_id(usersvoconv.getUser_boss_id());
		user_requests_histvo.setUser_data_access_id(usersvoconv.getUser_data_access_id());
		user_requests_histvo.setUser_profile_id(usersvoconv.getUser_profile_id());
		user_requests_histvo.setUser_sub_departement_id(usersvoconv.getUser_sub_departement_id());
		user_requests_histvo.setUser_language_id(usersvoconv.getUser_language_id());
		user_requests_histvo.setReason_request_fk(usersvoconv.getReason_request_fk());
		user_requests_histvo.setUser_institution_id(usersvoconv.getUser_institution_id());

		return user_requests_histvo;
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

	public void SendMail(String host, String from, String add_dest, String message_objet, String message_corps)
			throws Exception {

		logger.info("PowerCardV3 : Admin Validation,  Sending Mail : OBJECT " + message_objet + "MESSAGE CORPS : "
				+ message_corps + " ,TO :" + add_dest + " from : " + from + " using host :" + host);

		boolean debug = false;
		Properties props = new Properties();

		props.put("mail.smtp.host", host);

		// Time out parameters
		props.put("mail.smtp.timeout", "60000");
		props.put("mail.smtp.connectiontimeout", "60000");

		Session session;
		session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);

		Message mesg = new MimeMessage(session);

		InternetAddress toAddress;
		toAddress = new InternetAddress(add_dest);
		mesg.addRecipient(Message.RecipientType.TO, toAddress);
		mesg.setFrom(new InternetAddress(from));
		mesg.setSubject(message_objet);
		mesg.setText(message_corps);
		logger.info("PowerCardV3 : Admin Validation,  Start sending Mail TO :" + add_dest);
		try {
			Transport.send(mesg);
		} catch (MessagingException e) {
			// Adding trace when mail is not sent
			logger.info("PowerCardV3 : Admin Validation, Error when sending mail :" + e.getMessage());
			throw new OurException("1259", new Exception(e.getMessage()));
		}
		logger.info("PowerCardV3 : Admin Validation,  End sending Mail TO :" + add_dest);

	}

	/* Start ASB LDAP MODIF */
	private Ldap_param_by_bankRepository getLdap_param_by_bankRepository() {

		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		Ldap_param_by_bankRepository ldap_param_by_bankRepository = null;
		ldap_param_by_bankRepository = (Ldap_param_by_bankRepository) context.getBean("ldap_param_by_bankRepository");

		return ldap_param_by_bankRepository;
	}

	/* end ASB LDAP MODIF */
}
