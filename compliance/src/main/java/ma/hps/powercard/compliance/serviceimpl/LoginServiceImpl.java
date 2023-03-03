package ma.hps.powercard.compliance.serviceimpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.Bank;
import ma.hps.powercard.compliance.domain.BankProperties;
import ma.hps.powercard.compliance.domain.Menu;
import ma.hps.powercard.compliance.domain.MenuProperties;
import ma.hps.powercard.compliance.domain.MenuRepository;
import ma.hps.powercard.compliance.domain.Menus2rolesProperties;
import ma.hps.powercard.compliance.domain.Menus2rolesRepository;
import ma.hps.powercard.compliance.domain.Password;
import ma.hps.powercard.compliance.domain.Pcard_authentification_hist;
import ma.hps.powercard.compliance.domain.Pcard_authentification_histProperties;
import ma.hps.powercard.compliance.domain.Profile;
import ma.hps.powercard.compliance.domain.Role;
import ma.hps.powercard.compliance.domain.User_passwords;
import ma.hps.powercard.compliance.domain.User_passwordsProperties;
import ma.hps.powercard.compliance.domain.Users;
import ma.hps.powercard.compliance.domain.UsersProperties;
import ma.hps.powercard.compliance.repositoryimpl.MenuUtils;
import ma.hps.powercard.compliance.repositoryimpl.PasswordUtils;
import ma.hps.powercard.compliance.repositoryimpl.RoleUtils;
import ma.hps.powercard.compliance.serviceapi.ComponentVO;
import ma.hps.powercard.compliance.serviceapi.Data_columns_filterVO;
import ma.hps.powercard.compliance.serviceapi.Grant_permissionVO;
import ma.hps.powercard.compliance.serviceapi.LoginVO;
import ma.hps.powercard.compliance.serviceapi.MenuVO;
import ma.hps.powercard.compliance.serviceapi.ScreenInfosVO;
import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.domain.PagingParameter;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;

import com.google.gson.JsonObject;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of LoginService.
 */
@Lazy
@Service("loginService")
public class LoginServiceImpl extends LoginServiceImplBase {

	private static Logger logger = Logger.getLogger(LoginServiceImpl.class);

	@Autowired
	private Menus2rolesRepository menus2rolesRepository;

	@Autowired
	private MenuRepository menuRepository;
	

	public LoginServiceImpl() {
	}

	public LoginVO loginService(ServiceContext ctx, LoginVO loginVO)
			throws Exception {

		logger.info("PowerCardV3 : Operation loginService , USER :"
				+ ctx.getUserId() + " , SessionID :"
				+ ctx.getDetails().getSessionId() + " , RemoteAddress:"
				+ ctx.getDetails().getRemoteAddress());

		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();
		con.add(ConditionalCriteria.like(UsersProperties.user_code(), loginVO
				.getUser_code()));
		List<Users> lusers = this.getUsersRepository().findByCondition(con);
		Users user = lusers.get(0);
		
		/********************** User not active ***********************/
		if (user.getAccount_start_date() != null)
			if (new Date().before(user.getAccount_start_date())){
				throw new OurException("0058", new Exception());
		}
		
		if (user.getAccount_end_date() != null)
			if (new Date().after(user.getAccount_end_date())){
				throw new OurException("0058", new Exception());
		}

		
		/***************** Verification Profile ***********************/
		if (user.getFk_profile() != null) {
			if (user.getFk_profile().getStatus().equals("N")) {
				throw new OurException("0041", new Exception());
			}
		}

		/********* Verification password ******************/

		con = new ArrayList<ConditionalCriteria>();
		con.add(ConditionalCriteria.like(User_passwordsProperties.login(), loginVO.getUser_code()));
		if(!user.getIs_ldap_user().equals("Y")) {
			con.add(ConditionalCriteria.like(User_passwordsProperties.password(),
					user.getPassword()));			
		}
		con.add(ConditionalCriteria.orderDesc(User_passwordsProperties.date_change()));
		List<User_passwords> userPasswords = this.getUser_passwordsRepository().findByCondition(con);

		List<Password> passConfigs = this.getPasswordRepository().findByCondition(new ArrayList<ConditionalCriteria>());
		String passwordLife = passConfigs.get(0).getPassword_life();
		String passwordGrace = passConfigs.get(0).getPassword_grace();

		if (!user.getIs_ldap_user().equals("Y")) {
			logger.info(ctx.getUserId() + ": is not an LDAP user");
			if (user.getStatus() != null && user.getStatus().equals("F")) {
				passwordLife = "1"; // in case of firstLogin password should expire after 1 day
			}

			if (userPasswords != null && userPasswords.size() > 0) {
				userPasswords.get(0).setInvalide_authentif_num("0");
				this.getUser_passwordsRepository().save(userPasswords.get(0));
			}
		}

		/************* DataFilter **************/
		
		 if (user.getAccess_by().equals("P")) {
			if (user.getFk_profile() != null)
				if (user.getFk_profile().getData_access_id() != null)
					loginVO.setDataColumnsFilter(this.createNewDataFilter(ctx,
							user.getFk_profile().getData_access_id()
									.getData_access_id()));
		} else if (user.getFk_data_access() != null)
			loginVO.setDataColumnsFilter(this.createNewDataFilter(ctx, user
					.getFk_data_access().getData_access_id()));
		
		loginVO.setStatus("K");
		Date dateChange = null;
		if (userPasswords != null && userPasswords.size() > 0) {
			dateChange = userPasswords.get(0).getDate_change();
		}

		GregorianCalendar dateExpiry = new java.util.GregorianCalendar();
		dateExpiry.setTime(dateChange);

		dateExpiry.add(Calendar.DAY_OF_MONTH, Integer.parseInt(passwordLife));
		if(user.getStatus() != null && !user.getStatus().equals("F")) {
			dateExpiry.add(Calendar.DAY_OF_MONTH, Integer.parseInt(passwordGrace));
		}

		/********************** PasswordLife & Grace ***********************/
		if (new Date().after(dateExpiry.getTime())
				&& !loginVO.getUser_code().equalsIgnoreCase("Admin") && !user.getIs_ldap_user().equals("Y")) {
			logger.info("PowerCardV3 : Expired Password , USER :"
					+ ctx.getUserId() + " , SessionID :"
					+ ctx.getDetails().getSessionId() + " , RemoteAddress:"
					+ ctx.getDetails().getRemoteAddress());
			loginVO.setStatus("X");
			loginVO.setPassConfigs(PasswordUtils.entityToVO(ctx, passConfigs.get(0), 0, 0));
			return loginVO;

		}

		/********************** first login ***********************/

		if (user.getStatus().equals("F") && !"L".equals(loginVO.getConnection_status())) {
			logger
					.info("PowerCardV3 : First Login , USER :"
							+ ctx.getUserId() + " , SessionID :"
							+ ctx.getDetails().getSessionId()
							+ " , RemoteAddress:"
							+ ctx.getDetails().getRemoteAddress());
			loginVO.setStatus("F");
			loginVO.setPassConfigs(PasswordUtils.entityToVO(ctx, passConfigs.get(0), 0, 0));
			return loginVO;
		}
		
		/********************** Notification of password expiration ***********************/

		GregorianCalendar dateNotif = new java.util.GregorianCalendar();
		dateNotif.setTime(dateChange);
		dateNotif.add(Calendar.DAY_OF_MONTH, Integer.parseInt(passwordLife));
		
		if (new Date().after(dateNotif.getTime())
				&& new Date().before(dateExpiry.getTime())
            	&& !user.getIs_ldap_user().equals("Y")
				) {
			logger.info("PowerCardV3 : Expired Password Notification, USER :"
					+ ctx.getUserId() + " , SessionID :"
					+ ctx.getDetails().getSessionId() + " , RemoteAddress:"
					+ ctx.getDetails().getRemoteAddress());
			loginVO.setStatus("W");
			loginVO.setPassword_expiration_date(dateExpiry.getTime());
		}
		

		Profile profile = user.getFk_profile();

		List<MenuVO> grantedMenus = null;

		if ("A".equalsIgnoreCase(profile.getStatus())) {
			Collection<Role> rolesAssigned = profile.getRoles2();
			List<Role> roleList = new ArrayList<Role>(rolesAssigned);
			loginVO.setRole_col(RoleUtils.mapListOfEntitiesToVO(ctx, roleList,
					0, 0));

			// Get granted menus.
			Set<String> menusIds = this.menus2rolesRepository.findByCondition(
					Arrays.asList(ConditionalCriteria.in(
								Menus2rolesProperties.role_fk().role_id(),
								rolesAssigned.stream().map(Role::getRole_id).collect(Collectors.toSet())
							)
						)
					)
					.stream()
					.map(menurole -> menurole.getMenus2rolesPK().getMenu_id())
					.collect(Collectors.toSet());

			grantedMenus = this.menuRepository.findByCondition(
					Arrays.asList(ConditionalCriteria.in(MenuProperties.menu_id(), menusIds)) 
				)
					.stream()
					.map(menu -> {
						try {
							return MenuUtils.entityToVO(ctx, menu, 0, 0);
						} catch (Exception e) {
							logger.error(e.getMessage());
							return null;
						}
					})
					.filter(Objects::nonNull)
					.collect(Collectors.toList());

		}
		
		//Getting vb banks and setting them in ctx
		List<ConditionalCriteria> con_vbBank = new ArrayList<ConditionalCriteria>();
		con_vbBank.add(ConditionalCriteria.like(BankProperties.category(), "VB"));
		List<Bank> vb_banks = this.getBankRepository().findByCondition(con_vbBank);
		
		List<JsonObject> vb_BanksObj = vb_banks.stream().map(x -> {
        	JsonObject obj = new JsonObject();
        	obj.addProperty("bank_code", x.getBank_code());
    		obj.addProperty("bank_name", x.getBank_name());
        	return obj;
        }).collect(Collectors.toList());
		
		ctx.setProperty("vb_banks", (Serializable) vb_BanksObj);
		
		if (profile != null) {
			ctx.setProperty("pan_visualization", profile.getPan_visualization());
		}
		
		loginVO.setBankDataAccess(profile.getBank_data_access());
		loginVO.setBank_card_batch(user.getBank_card_batch());
		loginVO.setCurrent_card_batch(user.getCurrent_card_batch());
		loginVO.setAccess_by(user.getAccess_by());
		loginVO.setUser_name(user.getUser_name());
		loginVO.setUser_code(user.getUser_code());
		if(loginVO.getStatus() == null || (loginVO.getStatus() != null && !loginVO.getStatus().equals("W"))) {
			loginVO.setStatus(user.getStatus());
		}
		loginVO.setStaff_indicateur(user.getStaff_indicateur());
		loginVO.setJob_title(user.getJob_title());
		loginVO.setMail(user.getMail());
		loginVO.setActiv_email(user.getActiv_email());
		loginVO.setEmploye_number(user.getEmploye_number());
		loginVO.setAccount_expiry_date(user.getAccount_expiry_date());
		loginVO.setAccount_start_date(user.getAccount_start_date());
		loginVO.setAccount_end_date(user.getAccount_end_date());
		loginVO.setPrivilege_start_date(user.getPrivilege_start_date());
		loginVO.setPrivilege_end_date(user.getPrivilege_end_date());
		loginVO.setPrivilege_last_connexion_date(user
				.getPrivilege_last_connexion_date());
		loginVO.setAccess_resriction(user.getAccess_resriction());
		loginVO.setDis_notification_type(user.getDis_notification_type());
		loginVO.setBrowser_disconnection(user.getBrowser_disconnection());
		loginVO.setTimer_browser_disconnection(user
				.getTimer_browser_disconnection());
		loginVO.setPwc_disconnection(user.getPwc_disconnection());
		if (user.getPwc_disconnection().equals("N")){			
				if (user.getFk_profile().getTimer_pwc_disconnection() ==null || user.getFk_profile().getTimer_pwc_disconnection().equals("0"))			
					loginVO.setTimer_pwc_disconnection("7200");
				else
					loginVO.setTimer_pwc_disconnection(user.getFk_profile().getTimer_pwc_disconnection());
		}
		else{
			if (user.getTimer_pwc_disconnection() ==null || user.getTimer_pwc_disconnection().equals("0"))
				loginVO.setTimer_pwc_disconnection("7200");
			else
				loginVO.setTimer_pwc_disconnection(user.getTimer_pwc_disconnection());
		}
		
		loginVO.setPhone_number(user.getPhone_number());
		loginVO.setPassword(user.getPassword());
		if (user.getFk_profile()!=null){
			loginVO.setProfile_fk(user.getFk_profile().getProfile_id());
			loginVO.setProfile_code(user.getFk_profile().getProfile_code());
			if (user.getFk_profile().getAdmin() != null && user.getFk_profile().getAdmin().equalsIgnoreCase("Y")) {
				loginVO.setIsProfileAdmin(true);
			} else {
				loginVO.setIsProfileAdmin(false);
			}
		}
		if (user.getFk_country() !=null )
			loginVO.setCountry_fk(user.getFk_country().getCountry_code());
		loginVO.setBranch_fk(user.getBranch_fk());
		if (user.getFk_users_04() != null)
			loginVO.setInstitution_fk(user.getFk_users_04().
					getBank_code());
		loginVO.setBoss_fk(user.getBoss_fk());
		loginVO.setDepartement_fk(user.getDepartement_fk());
		if (user.getFk_data_access() != null)
			loginVO.setData_access_fk(user.getFk_data_access()
					.getData_access_id());
		loginVO.setSub_departement_fk(user.getSub_departement_fk());
		if (user.getFk_language() != null)
			loginVO.setLanguage_fk(user.getFk_language().getLanguage_code());
		loginVO.setConnection_status(user.getConnection_status());
		loginVO.setUsers_id(user.getUsers_id());
		loginVO.setMenusGranted(grantedMenus);
		loginVO.setPassConfigs(PasswordUtils.entityToVO(ctx, passConfigs.get(0), 0, 0));
		
		/*****************LastLoginDate*******************************/
		
		PagingParameter pagingParameter =
                PagingParameter.pageAccess(2, 1, false);
		List<ConditionalCriteria> conAuthentification_hist = new ArrayList<ConditionalCriteria>();
		conAuthentification_hist.add(ConditionalCriteria.equal(Pcard_authentification_histProperties.login(),
				loginVO.getUser_code()));
		conAuthentification_hist.add(ConditionalCriteria.orderDesc(Pcard_authentification_histProperties.date_authentif()));
		conAuthentification_hist.add(ConditionalCriteria.equal(Pcard_authentification_histProperties.statu_authentif(),"A"));
		
		List<Pcard_authentification_hist> listAuthentification_hist = this.getPcard_authentification_histRepository().findByCondition(conAuthentification_hist, pagingParameter).getValues();
		
		if (listAuthentification_hist.size()>0){
			if (listAuthentification_hist.size()>1)
				loginVO.setLoginDate(listAuthentification_hist.get(1).getLocale_date_authentif());
			else
				loginVO.setLoginDate(listAuthentification_hist.get(0).getLocale_date_authentif());
		}
		
		return loginVO;

	}

	public LoginVO loginOAuth2Service(ServiceContext ctx, LoginVO loginVO) throws Exception {
		
		logger.info("PowerCardV3 : Operation loginOAuth2Service , USER :"
				+ ctx.getUserId() + " , SessionID :"
				+ ctx.getDetails().getSessionId() + " , RemoteAddress:"
				+ ctx.getDetails().getRemoteAddress());

		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();
		con.add(ConditionalCriteria.like(UsersProperties.user_code(), loginVO
				.getUser_code()));
		List<Users> lusers = this.getUsersRepository().findByCondition(con);
		Users user = new Users();
		Profile profile;
		Bank bank;
		if(lusers == null || (lusers != null && lusers.size() == 0)) {
			profile = getProfileRepository().findByKey(loginVO.getProfile_code());
			if (profile == null) {
				throw new OurException("Profile not found", null);
			}
			bank = getBankRepository().findByKey(loginVO.getInstitution_fk());
			if (bank == null) {
				throw new OurException("Bank not found", null);
			}
			Users newOAuth2User = new Users();
			newOAuth2User.setUser_code(loginVO.getUser_code());
			newOAuth2User.setUser_name(loginVO.getUser_code());
			newOAuth2User.setMail(loginVO.getMail());
			newOAuth2User.setAccess_by("P");
			newOAuth2User.setPwc_disconnection("Y");
			newOAuth2User.setFk_profile(profile);
			newOAuth2User.setFk_users_04(bank);
			newOAuth2User.setStatus("N");
			newOAuth2User.setConnection_status("O");
			newOAuth2User.setAccount_start_date(new Date());
			user = getUsersRepository().save(newOAuth2User);
		} else {
			user = lusers.get(0);
			profile = user.getFk_profile();
			bank = user.getFk_users_04();
			Profile profileMapped = getProfileRepository().findByKey(loginVO.getProfile_code());
			if (profileMapped == null) {
				throw new OurException("Profile mapped not found", null);
			}
			if(!profile.getProfile_id().equalsIgnoreCase(profileMapped.getProfile_id())) {
				user.setFk_profile(profileMapped);
				getUsersRepository().save(user);
				profile = profileMapped;
			}
		}
		
		/***************** Verification Profile ***********************/
		if (profile != null) {
			if (profile.getStatus() != null && profile.getStatus().equals("N")) {
				throw new OurException("0041", new Exception());
			}
		}

		List<MenuVO> menusGranted = new ArrayList<MenuVO>();	

		if (profile.getStatus().equalsIgnoreCase("A")) {
			Collection<Role> rolesAssigned = profile.getRoles2();
			List<Role> roleList = new ArrayList<Role>(rolesAssigned);
			loginVO.setRole_col(RoleUtils.mapListOfEntitiesToVO(ctx, roleList, 0, 0));

			for (Role role : rolesAssigned) {
				if (role.getStatus() != null && role.getStatus().equalsIgnoreCase("A")) {
					Collection<Menu> lmenu = role.getMenusRole();
					for (Menu menu : lmenu) {
						menusGranted.add(MenuUtils.entityToVO(ctx, menu, 0, 0));
					}
				}
			}
		}
		
		List<ConditionalCriteria> con_vbBank = new ArrayList<ConditionalCriteria>();
		con_vbBank.add(ConditionalCriteria.like(BankProperties.category(), "VB"));
		List<Bank> vb_banks = this.getBankRepository().findByCondition(con_vbBank);
		
		List<JsonObject> vb_BanksObj = vb_banks.stream().map(x -> {
        	JsonObject obj = new JsonObject();
        	obj.addProperty("bank_code", x.getBank_code());
    		obj.addProperty("bank_name", x.getBank_name());
        	return obj;
        }).collect(Collectors.toList());
		
		ctx.setProperty("vb_banks", (Serializable) vb_BanksObj);
		
		if (profile != null) {
			ctx.setProperty("pan_visualization", profile.getPan_visualization());
		}
		
		loginVO.setBankDataAccess(profile.getBank_data_access());
		loginVO.setBank_card_batch(user.getBank_card_batch());
		loginVO.setCurrent_card_batch(user.getCurrent_card_batch());
		loginVO.setAccess_by(user.getAccess_by());
		loginVO.setUser_name(user.getUser_name());
		loginVO.setUser_code(user.getUser_code());
		loginVO.setStatus(user.getStatus());
		loginVO.setStaff_indicateur(user.getStaff_indicateur());
		loginVO.setJob_title(user.getJob_title());
		loginVO.setMail(user.getMail());
		loginVO.setActiv_email(user.getActiv_email());
		loginVO.setEmploye_number(user.getEmploye_number());
		loginVO.setAccount_expiry_date(user.getAccount_expiry_date());
		loginVO.setAccount_start_date(user.getAccount_start_date());
		loginVO.setAccount_end_date(user.getAccount_end_date());
		loginVO.setPrivilege_start_date(user.getPrivilege_start_date());
		loginVO.setPrivilege_end_date(user.getPrivilege_end_date());
		loginVO.setPrivilege_last_connexion_date(user
				.getPrivilege_last_connexion_date());
		loginVO.setAccess_resriction(user.getAccess_resriction());
		loginVO.setDis_notification_type(user.getDis_notification_type());
		loginVO.setBrowser_disconnection(user.getBrowser_disconnection());
		loginVO.setTimer_browser_disconnection(user
				.getTimer_browser_disconnection());
		loginVO.setPwc_disconnection(user.getPwc_disconnection());
		if (user.getPwc_disconnection().equals("N")){			
				if (user.getFk_profile().getTimer_pwc_disconnection() ==null || user.getFk_profile().getTimer_pwc_disconnection().equals("0"))			
					loginVO.setTimer_pwc_disconnection("7200");
				else
					loginVO.setTimer_pwc_disconnection(user.getFk_profile().getTimer_pwc_disconnection());
		}
		else{
			if (user.getTimer_pwc_disconnection() ==null || user.getTimer_pwc_disconnection().equals("0"))
				loginVO.setTimer_pwc_disconnection("7200");
			else
				loginVO.setTimer_pwc_disconnection(user.getTimer_pwc_disconnection());
		}
		
		loginVO.setPhone_number(user.getPhone_number());
		loginVO.setPassword(user.getPassword());
		if (profile != null) {
			loginVO.setProfile_fk(profile.getProfile_id());
			loginVO.setProfile_code(profile.getProfile_code());
		}
		if (user.getFk_country() !=null )
			loginVO.setCountry_fk(user.getFk_country().getCountry_code());
		loginVO.setBranch_fk(user.getBranch_fk());
		if (bank != null)
			loginVO.setInstitution_fk(bank.getBank_code());
		loginVO.setBoss_fk(user.getBoss_fk());
		loginVO.setDepartement_fk(user.getDepartement_fk());
		if (user.getFk_data_access() != null)
			loginVO.setData_access_fk(user.getFk_data_access()
					.getData_access_id());
		loginVO.setSub_departement_fk(user.getSub_departement_fk());
		if (user.getFk_language() != null)
			loginVO.setLanguage_fk(user.getFk_language().getLanguage_code());
		loginVO.setConnection_status(user.getConnection_status());
		loginVO.setUsers_id(user.getUsers_id());
		loginVO.setMenusGranted(menusGranted);
		
		/*****************LastLoginDate*******************************/
		
		PagingParameter pagingParameter =
                PagingParameter.pageAccess(2, 1, false);
		List<ConditionalCriteria> conAuthentification_hist = new ArrayList<ConditionalCriteria>();
		conAuthentification_hist.add(ConditionalCriteria.equal(Pcard_authentification_histProperties.login(),
				loginVO.getUser_code()));
		conAuthentification_hist.add(ConditionalCriteria.orderDesc(Pcard_authentification_histProperties.date_authentif()));
		conAuthentification_hist.add(ConditionalCriteria.equal(Pcard_authentification_histProperties.statu_authentif(),"A"));
		
		List<Pcard_authentification_hist> listAuthentification_hist = this.getPcard_authentification_histRepository().findByCondition(conAuthentification_hist, pagingParameter).getValues();
		
		if (listAuthentification_hist.size()>0){
			if (listAuthentification_hist.size()>1)
				loginVO.setLoginDate(listAuthentification_hist.get(1).getLocale_date_authentif());
			else
				loginVO.setLoginDate(listAuthentification_hist.get(0).getLocale_date_authentif());
		}
		
		return loginVO;
	}
	
	public String loginOut(ServiceContext ctx, String login) throws Exception {

		logger.info("PowerCardV3 : LogOUT, USER :" + ctx.getUserId()
				+ " , SessionID :" + ctx.getDetails().getSessionId()
				+ " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());

		/*
		 * UsersVO user = new UsersVO(); user.setUser_code(login); user =
		 * this.getUsersService().searchUsersService(ctx, user).get(0);
		 * user.setConnection_status("N");
		 * this.getUsersService().updateUsersService(ctx, user);
		 */
		return "0000";
	}

	public class GrantedMenu {
		boolean granted;
		MenuVO mVO;

		public boolean isGranted() {
			return granted;
		}

		public void setGranted(boolean granted) {
			this.granted = granted;
		}

		public MenuVO getmVO() {
			return mVO;
		}

		public void setmVO(MenuVO mVO) {
			this.mVO = mVO;
		}

	}

	// DataFilter

	private Collection<Data_columns_filterVO> createNewDataFilter(
			ServiceContext ctx, Long dataAccessID) throws Exception {

		Data_columns_filterVO data_columns_filterVO = new Data_columns_filterVO();
		data_columns_filterVO.setData_access_fk(dataAccessID);
		data_columns_filterVO.setLazy_level(2);
		data_columns_filterVO.setLazy_level_col(1);
		Collection<Data_columns_filterVO> listData_columns_filterVO = this
				.getData_columns_filterService()
				.searchData_columns_filterService(ctx, data_columns_filterVO);

		return listData_columns_filterVO;

	}

	public Map<String, Object> retrieveScreenInfos(ServiceContext ctx,
			ScreenInfosVO screenInfosVO) throws Exception {
		
		Map<String,Object> map = new  HashMap<String,Object>();
		Collection<String> listOfRoles = new ArrayList<String>();
		if (ctx!=null){ 
			listOfRoles  = (Collection<String>) ctx.getProperty("listOfRoles");
		}
		
        /*Ressource_bundleVO ressource_bundleVO =new Ressource_bundleVO();        
        ressource_bundleVO.setBundle(screenInfosVO.getBundle());
        ressource_bundleVO.setLocale_chain(screenInfosVO.getLang());
        ressource_bundleVO.setInBundle(screenInfosVO.getInBundle());
		List<Ressource_bundleVO>  lRessource_bundle = this.getRessource_bundleService().searchRessource_bundleService(ctx,ressource_bundleVO);		
		map.put("Ressource_bundle", lRessource_bundle);*/
		
		Grant_permissionVO screenGrants =new Grant_permissionVO ();
		screenGrants.setScreen_code(screenInfosVO.getScreen_code());
		if (listOfRoles != null){
			screenGrants.setConditionIn(true);
			screenGrants.setColRoles(listOfRoles);
		}
		List<Grant_permissionVO > lscreenGrants=this.getGrant_permissionService().searchGrant_permissionService(ctx,screenGrants);		
		map.put("ScreenGrants", lscreenGrants);
					
		ComponentVO componentsGrants =new ComponentVO ();
		componentsGrants.setScreen_code(screenInfosVO.getScreen_code());
		componentsGrants.setLazy_level_col(1);
		List<ComponentVO > lcomponentsGrant=this.getComponentService().searchComponentService(ctx,componentsGrants);
		if (listOfRoles != null)
			for (ComponentVO componentVO:lcomponentsGrant){
				List<Grant_permissionVO>  listGrants = filter(having(on(Grant_permissionVO.class).getRole_fk(), isIn(listOfRoles)), componentVO.getGrantsPermissions());
				componentVO.setGrantsPermissions(listGrants);
			} 	
		map.put("ComponentsGrant", lcomponentsGrant);
		 
		/*
	 	Component_validatorVO  component_validatorVO  = new Component_validatorVO();
		component_validatorVO.setScreen_code(screenInfosVO.getScreen_code());
		component_validatorVO.setLazy_level(1);
		List<Component_validatorVO > lComponent_validator=this.getComponent_validatorService().searchComponent_validatorService(ctx,component_validatorVO);		
		map.put("Component_validator", lComponent_validator);
		*/

		return map;
	}

	
	

}
