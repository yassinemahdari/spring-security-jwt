package ma.hps.powercard.compliance.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.Bank;
import ma.hps.powercard.compliance.domain.BankRepository;
import ma.hps.powercard.compliance.domain.Country;
import ma.hps.powercard.compliance.domain.CountryRepository;
import ma.hps.powercard.compliance.domain.Ldap_param_by_bank;
import ma.hps.powercard.compliance.domain.Ldap_param_by_bankRepository;
import ma.hps.powercard.compliance.domain.Password;
import ma.hps.powercard.compliance.domain.PasswordProperties;
import ma.hps.powercard.compliance.domain.PasswordRepository;
import ma.hps.powercard.compliance.domain.Pcard_authentification_hist;
import ma.hps.powercard.compliance.domain.Pcard_authentification_histProperties;
import ma.hps.powercard.compliance.domain.Pcard_authentification_histRepository;
import ma.hps.powercard.compliance.domain.Pcard_user_blockedRepository;
import ma.hps.powercard.compliance.domain.Powercard_globals;
import ma.hps.powercard.compliance.domain.Powercard_globalsProperties;
import ma.hps.powercard.compliance.domain.Powercard_globalsRepository;
import ma.hps.powercard.compliance.domain.Profile;
import ma.hps.powercard.compliance.domain.ProfileRepository;
import ma.hps.powercard.compliance.domain.User_passwords;
import ma.hps.powercard.compliance.domain.User_passwordsProperties;
import ma.hps.powercard.compliance.domain.User_passwordsRepository;
import ma.hps.powercard.compliance.domain.Users;
import ma.hps.powercard.compliance.domain.UsersProperties;
import ma.hps.powercard.compliance.domain.UsersRepository;
import ma.hps.powercard.compliance.exception.UsersNotFoundException;
import ma.hps.powercard.compliance.serviceapi.LoginVO;
import ma.hps.powercard.compliance.serviceapi.ProfileService;
import ma.hps.powercard.compliance.serviceimpl.spec.ldap.SuffixAwareActiveDirectoryLdapAuthenticationProvider;
import ma.hps.powercard.compliance.utils.GsonHelper;

/**
 * Implementation of AuthentificationService.
 */
@Lazy
@Service("authentificationService")
public class AuthentificationServiceImpl extends AuthentificationServiceImplBase {

	private ProviderManager authenticationManager;
	private static Logger logger = Logger.getLogger(AuthentificationServiceImpl.class);

	@Lazy
	@Autowired
	UsersRepository usersRepository;
	@Lazy
	@Autowired
	ProfileRepository profileRepository;
	@Lazy
	@Autowired
	BankRepository bankRepository;
	@Lazy
	@Autowired
	CountryRepository countryRepository;
	@Lazy
	@Autowired
	User_passwordsRepository user_passwordsRepository;
	@Lazy
	@Autowired
	Ldap_param_by_bankRepository ldap_param_by_bankRepository;
	@Lazy
	@Autowired
	Pcard_authentification_histRepository pcard_authentification_histRepository;
	@Lazy
	@Autowired
	Pcard_user_blockedRepository pcard_user_blockedRepository;
	@Lazy
	@Autowired
	ProfileService profileService;
	@Lazy
	@Autowired
	Powercard_globalsRepository powercard_globalsRepository;

	private static final String SCOPE = "memberOf";
	JsonObject mappedProfiles;
	Profile p;
	Bank ldap_bank;

	public AuthentificationServiceImpl() {

	}

	@Autowired
	public void setAuthenticationManager(ProviderManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Lazy
	@Autowired
	PasswordRepository passwordRepository;

	public LoginVO authentificationService(ServiceContext ctx, LoginVO loginVO) throws Exception {

		logger.info("PowerCardV3 : Operation authentificationService , USER :" + loginVO.getUser_code());

		Calendar calendar = new GregorianCalendar();
		TimeZone timeZone = calendar.getTimeZone();
		calendar.setTimeZone(timeZone);
		Pcard_authentification_hist pcard_authentification_hist = new Pcard_authentification_hist();
		pcard_authentification_hist.setDate_authentif(calendar.getTime());
		pcard_authentification_hist.setLogin(loginVO.getUser_code());
		pcard_authentification_hist.setLocale_date_authentif(loginVO.getLoginDate());
		pcard_authentification_hist.setIp_ldap_server(null);
		pcard_authentification_hist.setLdap_authentif("N");
		pcard_authentification_hist.setStatu_authentif("A");
		pcard_authentification_hist.setSessionID(ctx.getSessionId());
		pcard_authentification_hist.setRemoteAddress(ctx.getDetails().getRemoteAddress());

		List<ConditionalCriteria> conUser = new ArrayList<ConditionalCriteria>();
		conUser.add(ConditionalCriteria.equal(UsersProperties.user_code(), loginVO.getUser_code()));
		UsersRepository userRepository = getUsersRepository();
		List<Users> usersList = userRepository.findByCondition(conUser);

		List<ConditionalCriteria> conList = new ArrayList<ConditionalCriteria>();
		conList.add(ConditionalCriteria.orderDesc(Pcard_authentification_histProperties.date_authentif()));
		List<Pcard_authentification_hist> dateList = pcard_authentification_histRepository.findByCondition(conList);

		List<ConditionalCriteria> conPassword = new ArrayList<ConditionalCriteria>();
		// conPassword.add(ConditionalCriteria.equal(PasswordProperties.max_inactivity_days(),
		// null));
		conPassword.add(ConditionalCriteria.orderDesc(PasswordProperties.max_inactivity_days()));
		List<Password> pass = passwordRepository.findByCondition(conPassword);

		if (usersList.size() == 0) {
			throw new OurException("0017", new UsersNotFoundException(""));
		}

		if (usersList.size() == 1) {
			/** max inactivity days **/
			if (!dateList.isEmpty()) {
				Date lastConnection = dateList.get(0).getDate_authentif();
				String maxDays = pass.get(0).getMax_inactivity_days();
				int days = Days.daysBetween(new DateTime(lastConnection), new DateTime(new Date())).getDays();
				if (maxDays != null) {
					if (Integer.parseInt(maxDays) <= days) {
						usersList.get(0).setIs_blocked("Y");
						usersList.get(0).setDate_blocking(new Date());
						userRepository.save(usersList.get(0));
					}
				}
			}
			pcard_authentification_hist.setBank_fk(new Bank(usersList.get(0).getFk_users_04().getBank_code()));
			if ("C".equals(usersList.get(0).getStatus())) {
				logger.info("PowerCardV3 : Authentication failed - user canceled , USER :" + ctx.getUserId()
						+ " , SessionID :" + ctx.getDetails().getSessionId() + " , RemoteAddress:"
						+ ctx.getDetails().getRemoteAddress());
				pcard_authentification_hist.setStatu_authentif("C");
				sendToPcard_authentification_hist(pcard_authentification_hist);
				throw new OurException("0045", new Exception());
			}
		}

		List<ConditionalCriteria> conUser_passwords = new ArrayList<ConditionalCriteria>();
		conUser_passwords.add(ConditionalCriteria.equal(User_passwordsProperties.login(), loginVO.getUser_code()));
		conUser_passwords.add(ConditionalCriteria.orderDesc(User_passwordsProperties.date_change()));
		List<User_passwords> userPasswords = user_passwordsRepository.findByCondition(conUser_passwords);

		List<Password> passConfigs = this.getPasswordRepository().findByCondition(new ArrayList<ConditionalCriteria>());
		String badLogins = passConfigs.get(0).getBad_logins();
		int badNumberLogins = badLogins == null ? 0 : Integer.parseInt(badLogins);

		int invalide_authentif_num = 0;

		if (userPasswords.size() > 0) {
			invalide_authentif_num = userPasswords.get(0).getInvalide_authentif_num() == null ? 0
					: Integer.parseInt(userPasswords.get(0).getInvalide_authentif_num());
		}

		try {
			Authentication authentication = null;
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginVO.getUser_code(), loginVO.getPassword()));

			// handle user blocked.
			if ((usersList.get(0).getIs_blocked() != null && usersList.get(0).getIs_blocked().equals("Y"))
					|| (userPasswords.size() > 0 && badNumberLogins < invalide_authentif_num)) {

				logger.info("PowerCardV3 : Authentication failed - user blocked , USER :" + ctx.getUserId()
						+ " , SessionID :" + ctx.getDetails().getSessionId() + " , RemoteAddress:"
						+ ctx.getDetails().getRemoteAddress());

				pcard_authentification_hist.setStatu_authentif("B");
				sendToPcard_authentification_hist(pcard_authentification_hist);
				throw new OurException("0042", new Exception());
			}

			loginVO.setAuthentication(authentication);
			sendToPcard_authentification_hist(pcard_authentification_hist);
			logger.info("PowerCardV3 : Authentication success , USER :" + ctx.getUserId() + " , SessionID :"
					+ ctx.getDetails().getSessionId() + " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());
		} catch (BadCredentialsException e) {
			logger.info("PowerCardV3 : Authentication failed , USER :" + ctx.getUserId() + " , SessionID :"
					+ ctx.getDetails().getSessionId() + " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());

			pcard_authentification_hist.setStatu_authentif("R");
			sendToPcard_authentification_hist(pcard_authentification_hist);
			List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();
			con.add(ConditionalCriteria.equal(User_passwordsProperties.login(), loginVO.getUser_code()));

			if (userPasswords.size() > 0) {
				User_passwords user_passwords = userPasswords.get(0);
				invalide_authentif_num = Integer.parseInt(user_passwords.getInvalide_authentif_num());
				user_passwords.setInvalide_authentif_num(String.valueOf(invalide_authentif_num + 1));
				user_passwordsRepository.save(user_passwords);

				if (Integer.parseInt(badLogins) == invalide_authentif_num) {

					List<ConditionalCriteria> conUsers = new ArrayList<ConditionalCriteria>();
					conUsers.add(ConditionalCriteria.equal(UsersProperties.user_code(), loginVO.getUser_code()));

					UsersRepository usersRepository = getUsersRepository();
					List<Users> users = usersRepository.findByCondition(conUsers);

					if (users.size() > 0) {
						// Using is_blocked property instead of Pcard_user_blocked table
						Users u = users.get(0);
						u.setIs_blocked("Y");
						u.setDate_blocking(new Date());
						usersRepository.save(u);
					}
				}
			}
			throw e;
		}
		return loginVO;
	}

	public LoginVO ldapMailAuthentificationService(ServiceContext ctx, LoginVO loginVO, String silo) throws Exception {

		logger.info("PowerCardV3 : Operation ldapMailAuthentificationService , USER :" + loginVO.getMail());

		/** Ldap parameters */
		String LDAPUserDnt = "";
		String LDAPUserProfile = "";
		String status_ldap = "P";
		String ldap_url = null;
		String group_search_base = null;
		String ldap_base = null;
		Users newLdapUser = new Users();
		Boolean ldap_creation = false;
		String manager_id = null;
		String manager_password = null;
		String is_ldap_user = null;
		Ldap_param_by_bankRepository ldap_param_by_bankRepository = getLdap_param_by_bankRepository();
		Ldap_param_by_bank Ldap_param_by_bankList = null;
		ldap_url = null;
		group_search_base = null;
		ldap_base = null;
		manager_id = null;
		manager_password = null;
		List<Ldap_param_by_bank> ldapEntries = ldap_param_by_bankRepository.findAll();
		String ldap_bank_code = null;

		if (ldapEntries.size() > 0) {
			Ldap_param_by_bankList = ldapEntries.get(0);
			ldap_bank_code = Ldap_param_by_bankList.getBank_code();
			ldap_bank = bankRepository.findByKey(ldap_bank_code);
			ldap_url = Ldap_param_by_bankList.getLdap_url();
			group_search_base = Ldap_param_by_bankList.getGroup_search_base();
			ldap_base = Ldap_param_by_bankList.getLdap_base();
			manager_id = Ldap_param_by_bankList.getManager_id();
			manager_password = Ldap_param_by_bankList.getManager_password();
			mappedProfiles = GsonHelper.getGson().fromJson(Ldap_param_by_bankList.getMapped_profiles().toString(),
					JsonObject.class);
		}

		Calendar calendar = new GregorianCalendar();
		TimeZone timeZone = calendar.getTimeZone();
		calendar.setTimeZone(timeZone);
		Pcard_authentification_hist pcard_authentification_hist = new Pcard_authentification_hist();
		pcard_authentification_hist.setDate_authentif(calendar.getTime());
		pcard_authentification_hist.setLogin(loginVO.getUser_code());
		pcard_authentification_hist.setLocale_date_authentif(loginVO.getLoginDate());
		pcard_authentification_hist.setIp_ldap_server(ldap_url);
		pcard_authentification_hist.setLdap_authentif("Y");
		pcard_authentification_hist.setStatu_authentif("A");
		pcard_authentification_hist.setSessionID(ctx.getSessionId());
		pcard_authentification_hist.setRemoteAddress(ctx.getDetails().getRemoteAddress());
		pcard_authentification_hist.setBank_fk(ldap_bank);

		List<ConditionalCriteria> conUser = new ArrayList<ConditionalCriteria>();
		conUser.add(ConditionalCriteria.equal(UsersProperties.mail(), loginVO.getMail()));
		UsersRepository userRepository = getUsersRepository();
		List<Users> usersList = userRepository.findByCondition(conUser);

		if (usersList.size() == 1) {
			is_ldap_user = usersList.get(0).getIs_ldap_user();
			/** Case of Ldap connection */
			if ("Y".equals(is_ldap_user)) {
				loginVO.setConnection_status("L");
				sendToPcard_authentification_hist(pcard_authentification_hist);
			}
		}

		/** No user found in pwc => Check LDAP Base */
		else if (usersList.size() == 0 && ldapEntries.size() > 0) {

			LDAPUserDnt = this.doLookup(manager_id, manager_password, ldap_url, ldap_base, loginVO.getMail(), silo,
					"cacpUserScope", ctx).size() > 0
							? this.doLookup(manager_id, manager_password, ldap_url, ldap_base, loginVO.getMail(), silo,
									"cacpUserScope", ctx).get(0)
							: "";

			/** if the user who's trying to login is existing in ldap create it in pwc **/
			if (!LDAPUserDnt.equals("")) {
				ldap_creation = true;
				// A verifier

				LDAPUserProfile = this.doLookup(manager_id, manager_password, ldap_url, ldap_base, loginVO.getMail(),
						silo, "cacpUserScope", ctx).size() > 1
								? this.doLookup(manager_id, manager_password, ldap_url, ldap_base, loginVO.getMail(),
										silo, "cacpUserScope", ctx).get(1)
								: "";
				if (LDAPUserProfile.equals("")) {
					// if profile non mapped
					throw new OurException("0106", new Exception());
				}

				Country country = countryRepository.findByKey("504");

				try {
					/** Create user */
					newLdapUser = new Users();
					newLdapUser.setUser_name(loginVO.getUser_code());
					newLdapUser.setUser_code(loginVO.getUser_code());
					newLdapUser.setMail(loginVO.getMail());
					newLdapUser.setStatus("N");
					newLdapUser.setStaff_indicateur("N");
					newLdapUser.setJob_title("DEV");
					newLdapUser.setAccess_by("P");
					newLdapUser.setAccess_resriction("N");
					newLdapUser.setDis_notification_type("E");
					newLdapUser.setPwc_disconnection("N");
					newLdapUser.setTimer_pwc_disconnection("7200");
					newLdapUser.setFk_profile(p);
					newLdapUser.setFk_country(country);
					newLdapUser.setFk_users_04(ldap_bank);
					newLdapUser.setCollection_process_privilege("N");
					newLdapUser.setCollection_dispatch_privilege("N");
					newLdapUser.setBalances_hidden_flag("N");
					newLdapUser.setScreen_show_name("N");
					newLdapUser.setScreen_show_db("N");
					newLdapUser.setScreen_show_db_connect("N");
					newLdapUser.setIs_ldap_user("Y");
					Users savedUser = usersRepository.save(newLdapUser);
					logger.info("---------------------------user created-------------------------");
					is_ldap_user = "Y";

					/** Create user password */
					User_passwords user_passwords = new User_passwords();
					user_passwords.setLogin(loginVO.getUser_code());
					user_passwords.setDate_change(Calendar.getInstance().getTime());
					user_passwords.setUser_id(savedUser);
					user_passwordsRepository.save(user_passwords);
					logger.info("---------------------------password created-------------------------");
				} catch (Exception e) {
					// If sequence problem
					throw new OurException("0103", new Exception());
				}

				/** Set Ldap connection */
				loginVO.setConnection_status("L");
				ldap_url = Ldap_param_by_bankList.getLdap_url();
				group_search_base = Ldap_param_by_bankList.getGroup_search_base();
				ldap_base = Ldap_param_by_bankList.getLdap_base();
				// domaine_name = Ldap_param_by_bankList.getDomaine_name();
				manager_id = Ldap_param_by_bankList.getManager_id();
				manager_password = Ldap_param_by_bankList.getManager_password();
				/* Add Ip_ldap_server and Ldap_authentif to pcard_authentification_hist */
				pcard_authentification_hist.setLdap_authentif("Y");
				pcard_authentification_hist.setIp_ldap_server(ldap_url);
				sendToPcard_authentification_hist(pcard_authentification_hist);

			}
		}

		List<ConditionalCriteria> conUser_passwords = new ArrayList<ConditionalCriteria>();
		conUser_passwords.add(ConditionalCriteria.equal(User_passwordsProperties.login(), loginVO.getUser_code()));
		conUser_passwords.add(ConditionalCriteria.orderDesc(User_passwordsProperties.date_change()));

		User_passwordsRepository user_passwordsRepository = getUser_passwordsRepository();
		List<User_passwords> userPasswords = user_passwordsRepository.findByCondition(conUser_passwords);

		try {
			/** Case of Ldap connection */
			Authentication authentication = null;
			if (usersList.size() == 1) {
				is_ldap_user = usersList.get(0).getIs_ldap_user();
			}
			if ("Y".equals(is_ldap_user)) {
				/** Update with new profile if it has changed on LDAP */
				if (ldap_creation.equals(false)) {
					LDAPUserProfile = this.doLookup(manager_id, manager_password, ldap_url, ldap_base,
							loginVO.getMail(), silo, "cacpUserScope", ctx).size() > 1
									? this.doLookup(manager_id, manager_password, ldap_url, ldap_base,
											loginVO.getMail(), silo, "cacpUserScope", ctx).get(1)
									: "";
					if (LDAPUserProfile.equals("")) {
						// if profile non mapped
						throw new OurException("0106", new Exception());
					}
					if (!usersList.get(0).getFk_profile().equals(p)) {
						usersList.get(0).setFk_profile(p);
						usersRepository.save(usersList.get(0));
					}
				}

				String providerUrl = ldap_url + ldap_base;
				DefaultSpringSecurityContextSource ds = new DefaultSpringSecurityContextSource(providerUrl);
				ds.setUserDn(manager_id);
				ds.setPassword(manager_password);
				ds.setCacheEnvironmentProperties(false);
				BindAuthenticator ba = new BindAuthenticator(ds);
				String[] dnPattern = new String[1];
				dnPattern[0] = this.doLookup(manager_id, manager_password, ldap_url, ldap_base, loginVO.getMail(), silo,
						"cacpUserScope", ctx).size() > 0
								? this.doLookup(manager_id, manager_password, ldap_url, ldap_base, loginVO.getMail(),
										silo, "cacpUserScope", ctx).get(0)
								: "";
				ba.setUserDnPatterns(dnPattern);
				DefaultLdapAuthoritiesPopulator dp = new DefaultLdapAuthoritiesPopulator(ds, group_search_base);
				dp.setIgnorePartialResultException(true);
				LdapAuthenticationProvider provider = new LdapAuthenticationProvider(ba, dp);
				// authentication = provider.authenticate(
				// new UsernamePasswordAuthenticationToken(loginVO.getMail(),
				// loginVO.getPassword()));
			} else {
				authentication = authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(loginVO.getUser_code(), loginVO.getPassword()));
			}
			loginVO.setAuthentication(authentication);

			sendToPcard_authentification_hist(pcard_authentification_hist);

			logger.info("PowerCardV3 : Authentication success , USER :" + ctx.getUserId() + " , SessionID :"
					+ ctx.getDetails().getSessionId() + " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());
		}

		catch (BadCredentialsException e) {
			logger.info("PowerCardV3 : Authentication failed , USER :" + ctx.getUserId() + " , SessionID :"
					+ ctx.getDetails().getSessionId() + " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());

			pcard_authentification_hist.setStatu_authentif("R");
			sendToPcard_authentification_hist(pcard_authentification_hist);

			if (userPasswords.size() > 0) {
				User_passwords user_passwords = userPasswords.get(0);
				user_passwordsRepository.save(user_passwords);
			}
			if ("L".equals(status_ldap)) {
				throw new OurException("0017", new Exception());
			}
			throw e;
		}
		return loginVO;
	}

	public LoginVO ldapAuthentificationService(ServiceContext ctx, LoginVO loginVO) throws Exception {

		logger.info("PowerCardV3 : Operation ldapAuthentificationService , USER :" + loginVO.getMail());

		/** Ldap parameters */
		String LDAPUserDnt = "";
		String LDAPUserProfile = "";
		String status_ldap = "P";
		String ldap_url = null;
		String ldap_domaine = null;
		String group_search_base = null;
		String ldap_base = null;
		Users newLdapUser = new Users();
		Boolean ldap_creation = false;
		String manager_id = null;
		String manager_password = null;
		String is_ldap_user = null;
		Ldap_param_by_bankRepository ldap_param_by_bankRepository = getLdap_param_by_bankRepository();
		Ldap_param_by_bank Ldap_param_by_bankList = null;
		List<Ldap_param_by_bank> ldapEntries = ldap_param_by_bankRepository.findAll();
		String ldap_bank_code = null;

		if (ldapEntries.size() > 0) {
			Ldap_param_by_bankList = ldapEntries.get(0);
			ldap_bank_code = Ldap_param_by_bankList.getBank_code();
			ldap_bank = bankRepository.findByKey(ldap_bank_code);
			ldap_url = Ldap_param_by_bankList.getLdap_url();
			ldap_domaine = Ldap_param_by_bankList.getDomaine_name();
			group_search_base = Ldap_param_by_bankList.getGroup_search_base();
			ldap_base = Ldap_param_by_bankList.getLdap_base();
			manager_id = Ldap_param_by_bankList.getManager_id();
			manager_password = Ldap_param_by_bankList.getManager_password();
			mappedProfiles = GsonHelper.getGson().fromJson(Ldap_param_by_bankList.getMapped_profiles().toString(),
					JsonObject.class);
		}

		Calendar calendar = new GregorianCalendar();
		TimeZone timeZone = calendar.getTimeZone();
		calendar.setTimeZone(timeZone);
		Pcard_authentification_hist pcard_authentification_hist = new Pcard_authentification_hist();
		pcard_authentification_hist.setDate_authentif(calendar.getTime());
		pcard_authentification_hist.setLogin(loginVO.getUser_code());
		pcard_authentification_hist.setLocale_date_authentif(loginVO.getLoginDate());
		pcard_authentification_hist.setIp_ldap_server(ldap_url);
		pcard_authentification_hist.setLdap_authentif("Y");
		pcard_authentification_hist.setStatu_authentif("A");
		pcard_authentification_hist.setSessionID(ctx.getSessionId());
		pcard_authentification_hist.setRemoteAddress(ctx.getDetails().getRemoteAddress());
		pcard_authentification_hist.setBank_fk(ldap_bank);

		List<ConditionalCriteria> conUser = new ArrayList<ConditionalCriteria>();
		conUser.add(ConditionalCriteria.equal(UsersProperties.user_code(), loginVO.getUser_code()));
		UsersRepository userRepository = getUsersRepository();
		List<Users> usersList = userRepository.findByCondition(conUser);

		// getting the user info using UPN
		String cn = null;
		Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, ldap_url);
		properties.put(Context.SECURITY_AUTHENTICATION, "none");
		if (StringUtils.isNotBlank(manager_id) && StringUtils.isNotBlank(manager_password)) {
			properties.put(Context.SECURITY_AUTHENTICATION, "simple");
			properties.put(Context.SECURITY_PRINCIPAL, manager_id);
			properties.put(Context.SECURITY_CREDENTIALS, manager_password);
		}
		DirContext context = new InitialDirContext(properties);

		// Looking for user's CN.
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> rslt = context.search(Ldap_param_by_bankList.getGroup_search_base(),
				"userPrincipalName=" + loginVO.getMail(), searchControls);
		if (rslt.hasMore()) {
			Attributes attrs = rslt.next().getAttributes();
			cn = attrs.get("cn").toString().replace("cn: ", "");
			logger.debug("CN = " + cn);
		} else {
			logger.debug("Cannot find CN using userPrincipalName=" + loginVO.getMail());
		}
		context.close();

		if (usersList.size() == 1) {
			is_ldap_user = usersList.get(0).getIs_ldap_user();
			/** Case of Ldap connection */
			if ("Y".equals(is_ldap_user)) {
				loginVO.setConnection_status("L");
				sendToPcard_authentification_hist(pcard_authentification_hist);
			}
		}

		/** No user found in pwc => Check LDAP Base */
		else if (usersList.size() == 0 && ldapEntries.size() > 0) {

			List<String> lookups = this.doLookup(manager_id, manager_password, ldap_url, ldap_base,
					cn != null ? cn : loginVO.getMail(), "", "memberOf", ctx);
			LDAPUserDnt = lookups.size() > 0 ? lookups.get(0) : "";

			logger.debug("userNameAndProfile: " + Arrays.toString(lookups.toArray()));

			/** if the user who's trying to login is existing in ldap create it in pwc **/
			if (!LDAPUserDnt.equals("")) {
				ldap_creation = true;
				// A verifier

				LDAPUserProfile = lookups.size() > 1 ? lookups.get(1) : "";
				if (LDAPUserProfile.equals("")) {
					// if profile non mapped
					throw new OurException("0106", new Exception());
				}

				Country country = countryRepository.findByKey("504");

				try {
					/** Create user */
					newLdapUser = new Users();
					newLdapUser.setUser_name(loginVO.getUser_code());
					newLdapUser.setUser_code(loginVO.getUser_code());
					newLdapUser.setMail(loginVO.getMail());
					newLdapUser.setStatus("N");
					newLdapUser.setStaff_indicateur("N");
					newLdapUser.setJob_title("DEV");
					newLdapUser.setAccess_by("P");
					newLdapUser.setAccess_resriction("N");
					newLdapUser.setDis_notification_type("E");
					newLdapUser.setPwc_disconnection("N");
					newLdapUser.setTimer_pwc_disconnection("7200");
					newLdapUser.setFk_profile(p);
					newLdapUser.setFk_country(country);
					newLdapUser.setFk_users_04(ldap_bank);
					newLdapUser.setCollection_process_privilege("N");
					newLdapUser.setCollection_dispatch_privilege("N");
					newLdapUser.setBalances_hidden_flag("N");
					newLdapUser.setScreen_show_name("N");
					newLdapUser.setScreen_show_db("N");
					newLdapUser.setScreen_show_db_connect("N");
					newLdapUser.setIs_ldap_user("Y");
					Users savedUser = usersRepository.save(newLdapUser);
					logger.info("---------------------------user created-------------------------");
					is_ldap_user = "Y";

					/** Create user password */
					User_passwords user_passwords = new User_passwords();
					user_passwords.setLogin(loginVO.getUser_code());
					user_passwords.setDate_change(Calendar.getInstance().getTime());
					user_passwords.setUser_id(savedUser);
					user_passwordsRepository.save(user_passwords);
					logger.info("---------------------------password created-------------------------");
				} catch (Exception e) {
					// If sequence problem
					throw new OurException("0103", new Exception());
				}

				/** Set Ldap connection */
				loginVO.setConnection_status("L");
				ldap_url = Ldap_param_by_bankList.getLdap_url();
				group_search_base = Ldap_param_by_bankList.getGroup_search_base();
				ldap_base = Ldap_param_by_bankList.getLdap_base();
				// domaine_name = Ldap_param_by_bankList.getDomaine_name();
				manager_id = Ldap_param_by_bankList.getManager_id();
				manager_password = Ldap_param_by_bankList.getManager_password();
				/* Add Ip_ldap_server and Ldap_authentif to pcard_authentification_hist */
				pcard_authentification_hist.setLdap_authentif("Y");
				pcard_authentification_hist.setIp_ldap_server(ldap_url);
				sendToPcard_authentification_hist(pcard_authentification_hist);

			}
		}

		List<ConditionalCriteria> conUser_passwords = new ArrayList<ConditionalCriteria>();
		conUser_passwords.add(ConditionalCriteria.equal(User_passwordsProperties.login(), loginVO.getUser_code()));
		conUser_passwords.add(ConditionalCriteria.orderDesc(User_passwordsProperties.date_change()));

		User_passwordsRepository user_passwordsRepository = getUser_passwordsRepository();
		List<User_passwords> userPasswords = user_passwordsRepository.findByCondition(conUser_passwords);

		try {
			/** Case of Ldap connection */
			Authentication authentication = null;
			List<String> lookups = this.doLookup(manager_id, manager_password, ldap_url, ldap_base,
					cn != null ? cn : loginVO.getMail(), "", "memberOf", ctx);

			if (usersList.size() == 1) {
				is_ldap_user = usersList.get(0).getIs_ldap_user();
			}
			if ("Y".equals(is_ldap_user)) {
				/** Update with new profile if it has changed on LDAP */
				if (ldap_creation.equals(false)) {

					LDAPUserProfile = lookups.size() > 1 ? lookups.get(1) : "";

					if (LDAPUserProfile.equals("")) {
						// if profile non mapped
						throw new OurException("0106", new Exception());
					}
					if (!usersList.get(0).getFk_profile().equals(p)) {
						usersList.get(0).setFk_profile(p);
						usersRepository.save(usersList.get(0));
					}
				}

				// String providerUrl = ldap_url + ldap_base;
				// DefaultSpringSecurityContextSource ds = new
				// DefaultSpringSecurityContextSource(providerUrl);
				// ds.setUserDn(manager_id);
				// ds.setPassword(manager_password);
				// ds.setCacheEnvironmentProperties(false);
				// BindAuthenticator ba = new BindAuthenticator(ds);
				// String[] dnPattern = new String[1];
				// dnPattern[0] = lookups.size() > 0 ? lookups.get(0) : "";
				//
				// ba.setUserDnPatterns(dnPattern);
				// DefaultLdapAuthoritiesPopulator dp = new DefaultLdapAuthoritiesPopulator(ds,
				// group_search_base);
				// dp.setIgnorePartialResultException(true);
				// LdapAuthenticationProvider provider = new LdapAuthenticationProvider(ba, dp);
				// authentication = provider.authenticate(new
				// UsernamePasswordAuthenticationToken(loginVO.getMail(),
				// loginVO.getPassword()));

				String username = loginVO.getMail();

				// We will try to add the suffix only if the username does not already have one.
				if (!username.contains("@")) {
					String suffix = null;
					try {
						List<ConditionalCriteria> conditions = new ArrayList<ConditionalCriteria>();
						conditions.add(
								ConditionalCriteria.equal(Powercard_globalsProperties.variable_name(), "LDAP_SUFFIX"));
						List<Powercard_globals> vars = powercard_globalsRepository.findByCondition(conditions);
						if (vars.size() > 0)
							suffix = vars.get(0).getVariable_value();
					} catch (Exception e) {
						logger.error("Could not get LDAP_SUFFIX " + e.getMessage());
					}
					if (StringUtils.isNotBlank(suffix))
						username = loginVO.getMail() + suffix;
				}

				AuthenticationProvider authenticationProvider = new SuffixAwareActiveDirectoryLdapAuthenticationProvider(
						ldap_domaine, ldap_url);
				authentication = authenticationProvider
						.authenticate(new UsernamePasswordAuthenticationToken(username, loginVO.getPassword()));

			} else {
				authentication = authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(loginVO.getUser_code(), loginVO.getPassword()));
			}
			loginVO.setAuthentication(authentication);

			logger.info("PowerCardV3 : Authentication success , USER :" + ctx.getUserId() + " , SessionID :"
					+ ctx.getDetails().getSessionId() + " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());
		}

		catch (BadCredentialsException e) {
			logger.info("PowerCardV3 : Authentication failed , USER :" + ctx.getUserId() + " , SessionID :"
					+ ctx.getDetails().getSessionId() + " , RemoteAddress:" + ctx.getDetails().getRemoteAddress());

			pcard_authentification_hist.setStatu_authentif("R");
			sendToPcard_authentification_hist(pcard_authentification_hist);

			if (userPasswords.size() > 0) {
				User_passwords user_passwords = userPasswords.get(0);
				user_passwordsRepository.save(user_passwords);
			}
			if ("L".equals(status_ldap)) {
				throw new OurException("0017", new Exception());
			}
			throw e;
		}
		return loginVO;
	}

	private User_passwordsRepository getUser_passwordsRepository() {

		return user_passwordsRepository;
	}

	private UsersRepository getUsersRepository() {

		return usersRepository;
	}

	private ProfileRepository getProfileRepository() {

		return profileRepository;
	}

	private BankRepository getBankRepository() {

		return bankRepository;
	}

	private Ldap_param_by_bankRepository getLdap_param_by_bankRepository() {
		return ldap_param_by_bankRepository;
	}

	private void sendToPcard_authentification_hist(Pcard_authentification_hist pcard_authentification_hist)
			throws Exception {
		Pcard_authentification_histRepository pcard_authentification_histRepository = getPcard_authentification_histRepository();
		pcard_authentification_histRepository.save(pcard_authentification_hist);
	}

	public String getProfile(List<String> scopelist) throws OurException {

		logger.debug("scopeList: " + Arrays.toString(scopelist.toArray()));
		logger.debug("mappedProfile: " + GsonHelper.getGson().toJson(mappedProfiles));

		String mapped = null;

		for (String profileId : scopelist) {

			if (mappedProfiles.has(profileId)) {
				try {
					mapped = mappedProfiles.get(profileId).getAsString();
					p = getProfileRepository().findByKey(mapped);
					return mapped;
				} catch (Exception e) {
					throw new OurException("0104", new Exception());
				}
			} else if (profileId.toLowerCase().contains("cn=")) {

				logger.debug("profileId: " + profileId);

				String[] cns = profileId.replace("[", "").replace("]", "").replaceAll("(?i)cn=", "cn").split("cn");
				for (String cn : cns) {
					cn = cn.split(",")[0];
					if (StringUtils.isNotBlank(cn) && mappedProfiles.has(cn)) {
						try {
							mapped = mappedProfiles.get(cn).getAsString();
							p = getProfileRepository().findByKey(mapped);
							return mapped;
						} catch (Exception e) {
							throw new OurException("0104", new Exception());
						}
					}
				}
			}
		}
		throw new OurException("0104", new Exception());
	}

	public String printSearchEnumeration(NamingEnumeration retEnum, String searchType, String bank_code,
			JsonObject mappedProfiles, String scope, ServiceContext ctx) throws OurException {
		String result = "";
		try {
			while (retEnum.hasMore()) {
				SearchResult sr = (SearchResult) retEnum.next();
				switch (searchType) {
				case "dn":
					result = sr.getNameInNamespace();
					logger.debug("dn: " + result);
					break;
				case "profile":
					NamingEnumeration ne = sr.getAttributes().get(scope).getAll();
					List<String> list = Collections.list(ne);
					String ldapProfile = getProfile(list);

					logger.debug(scope + ": " + Arrays.toString(list.toArray()));
					logger.debug("ldapProfile: " + ldapProfile);

					result = ldapProfile;
					break;
				default:
					break;
				}
			}
		} catch (NamingException e) {
			logger.error("Naming exception");
		}
		return result;
	}

	public List<String> doLookup(String manager_id, String manager_password, String ldap_url, String ldap_base,
			String userName, String bank_code, String scope, ServiceContext ctx) throws OurException {
		/** List contains DN and Profile */

		logger.debug("Lookup using CN: " + userName);

		List<String> dnAndProfile = new ArrayList<String>();
		String dn = "";
		String profile = "";
		if (ldap_url.endsWith("/")) {
			ldap_url = ldap_url.substring(0, ldap_url.length() - 1);
		}
		Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, ldap_url);
		properties.put(Context.SECURITY_AUTHENTICATION, "none");
		if (StringUtils.isNotBlank(manager_id) && StringUtils.isNotBlank(manager_password)) {
			properties.put(Context.SECURITY_AUTHENTICATION, "simple");
			properties.put(Context.SECURITY_PRINCIPAL, manager_id);
			properties.put(Context.SECURITY_CREDENTIALS, manager_password);
		}

		try {
			DirContext context = new InitialDirContext(properties);
			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			final String filter = "cn=" + userName;
			logger.debug("username/profile lookup: " + filter);
			NamingEnumeration values = context.search(ldap_base, filter, searchCtrls);
			dn = this.printSearchEnumeration(values, "dn", bank_code, null, scope, ctx);
			int baseLength = ldap_base.length() + 1;

			logger.debug("'dn' before substring: " + dn);

			if (dn.length() > baseLength) {
				dn = dn.substring(0, dn.length() - baseLength);
				logger.debug("'dn' after substring: " + dn);
			}
			dnAndProfile.add(dn);
			SearchControls profileControls = new SearchControls();
			profileControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			profileControls.setReturningAttributes(new String[] { scope });
			NamingEnumeration scopes = context.search(ldap_base, filter, profileControls);
			profile = this.printSearchEnumeration(scopes, "profile", bank_code, mappedProfiles, scope, ctx);

			logger.debug("profile: " + profile);

			dnAndProfile.add(profile);
			context.close();
		} catch (NamingException e) {
			throw new OurException("0105", new Exception());
		}
		return dnAndProfile;
	}

	private boolean checkUserBlocked(String user_code, boolean ldapAuthentication) throws Exception {

		// Using is_blocked property instead of Pcard_user_blocked table

		List<ConditionalCriteria> conUsers = new ArrayList<ConditionalCriteria>();
		conUsers.add(ConditionalCriteria.equal(UsersProperties.user_code(), user_code));
		UsersRepository usersRepository = getUsersRepository();
		List<Users> users = usersRepository.findByCondition(conUsers);

		if (users.size() > 0) {
			if (users.get(0).getIs_blocked() != null) {
				return users.get(0).getIs_blocked().equals("Y");
			}
			return false;
		} else if (ldapAuthentication) {
			return false;
		} else {
			throw new OurException("0017", new UsersNotFoundException(""));
		}
	}

	private Pcard_authentification_histRepository getPcard_authentification_histRepository() throws Exception {

		return pcard_authentification_histRepository;
	}

	private Pcard_user_blockedRepository getPcard_user_blockedRepository() {

		return pcard_user_blockedRepository;
	}
}
