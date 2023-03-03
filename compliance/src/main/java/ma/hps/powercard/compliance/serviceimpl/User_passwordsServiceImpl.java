package ma.hps.powercard.compliance.serviceimpl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.User_passwords;
import ma.hps.powercard.compliance.domain.User_passwordsProperties;
import ma.hps.powercard.compliance.domain.Users;
import ma.hps.powercard.compliance.exception.User_passwordsNotFoundException;
import ma.hps.powercard.compliance.serviceapi.PasswordVO;
import ma.hps.powercard.compliance.serviceapi.ProfileVO;
import ma.hps.powercard.compliance.serviceapi.User_passwordsVO;
import ma.hps.powercard.compliance.serviceapi.UsersVO;
import ma.hps.powercard.compliance.utils.GsonHelper;
import ma.hps.powercard.constants.GlobalVars;
import ma.hps.powercard.constants.PwcStatusCode;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of UserpasswordsService.
 */
@Lazy
@Service("user_passwordsService")
public class User_passwordsServiceImpl extends User_passwordsServiceImplBase {
		
	private final static Logger logger = Logger.getLogger(User_passwordsServiceImpl.class);

	public User_passwordsServiceImpl() {
	}

	public String createUser_passwordsService(ServiceContext ctx,User_passwordsVO user_passwordsVO) {
		User_passwords user_passwords = new User_passwords();

		if (user_passwordsVO.getUsers_fk() != null) {
			user_passwords.setUser_id(new Users(user_passwordsVO.getUsers_fk()));
		}

		user_passwords.setPassword(user_passwordsVO.getPassword());

		user_passwords.setDate_change(user_passwordsVO.getDate_change());

		user_passwords.setLogin(user_passwordsVO.getLogin());

		if (user_passwordsVO.getInvalide_authentif_num() != null &&
				!user_passwordsVO.getInvalide_authentif_num().equals("")) {
			user_passwords.setInvalide_authentif_num(user_passwordsVO.getInvalide_authentif_num());
		}
		else
			user_passwords.setInvalide_authentif_num("0");


		this.getUser_passwordsRepository().save(user_passwords);

		return "0000";
	}

	public String updateUser_passwordsService(ServiceContext ctx,User_passwordsVO user_passwordsVO) throws Exception {
		User_passwords user_passwords = null;

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(
				User_passwordsProperties.user_passwords_id(),
				user_passwordsVO.getUser_passwords_id()));

		List<User_passwords> list =
				this.getUser_passwordsRepository().findByCondition(con);

		if (list.size() > 0) {
			user_passwords = list.get(0);
		} else {
			throw new OurException("0001", new User_passwordsNotFoundException(""));
		}

		if (user_passwordsVO.getUsers_fk() != null) {
			user_passwords.setUser_id(new Users(user_passwordsVO.getUsers_fk()));
		}

		if (user_passwordsVO.getPassword() != null &&
				!user_passwordsVO.getPassword().equals("")) {
			user_passwords.setPassword(user_passwordsVO.getPassword());
		}

		if (user_passwordsVO.getDate_change() != null &&
				!user_passwordsVO.getDate_change().equals("")) {
			user_passwords.setDate_change(user_passwordsVO.getDate_change());
		}

		if (user_passwordsVO.getLogin() != null &&
				!user_passwordsVO.getLogin().equals("")) {
			user_passwords.setLogin(user_passwordsVO.getLogin());
		}

		if (user_passwordsVO.getInvalide_authentif_num() != null &&
				!user_passwordsVO.getInvalide_authentif_num().equals("")) {
			user_passwords.setInvalide_authentif_num(user_passwordsVO.getInvalide_authentif_num());
		}
		User_passwords user_passwords1 =
				this.getUser_passwordsRepository().save(user_passwords);

		return "0000";
	}

	public String deleteUser_passwordsService(ServiceContext ctx,User_passwordsVO user_passwordsVO) throws Exception {
		User_passwords user_passwords = null;

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(
				User_passwordsProperties.user_passwords_id(),
				user_passwordsVO.getUser_passwords_id()));

		List<User_passwords> list =
				this.getUser_passwordsRepository().findByCondition(con);

		if (list.size() > 0) {
			user_passwords = list.get(0);
		} else {
			throw new OurException("0001", new User_passwordsNotFoundException(""));
		}

		user_passwords.setPassword(user_passwordsVO.getPassword());

		user_passwords.setDate_change(user_passwordsVO.getDate_change());

		user_passwords.setLogin(user_passwordsVO.getLogin());

		user_passwords.setInvalide_authentif_num(user_passwordsVO.getInvalide_authentif_num());


		this.getUser_passwordsRepository().delete(user_passwords);

		return "0000";
	}

	public List<User_passwordsVO> getAllUser_passwordsService(ServiceContext ctx) {
		List<User_passwordsVO> l = new ArrayList<User_passwordsVO>();

		List<User_passwords> l_entity =
				this.getUser_passwordsRepository().findAll();

		for (int i = 0; i < l_entity.size(); i++) {
			l.add(entityToVO(ctx,l_entity.get(i),0));

		}

		return l;

	}

	public List<User_passwordsVO> searchUser_passwordsService(ServiceContext ctx,
			User_passwordsVO user_passwordsVO) {
		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		if (user_passwordsVO.getUser_passwords_id() != null) {
			con.add(ConditionalCriteria.likeOrEqual(
					User_passwordsProperties.user_passwords_id(),
					user_passwordsVO.getUser_passwords_id()));
		}

		if (user_passwordsVO.getPassword() != null &&
				!user_passwordsVO.getPassword().equals("")) {
			con.add(ConditionalCriteria.likeOrEqual(
					User_passwordsProperties.password(),
					user_passwordsVO.getPassword()));
		}

		if (user_passwordsVO.getDate_change() != null &&
				!user_passwordsVO.getDate_change().equals("")) {
			con.add(ConditionalCriteria.likeOrEqual(
					User_passwordsProperties.date_change(),
					user_passwordsVO.getDate_change()));
		}

		con.add(ConditionalCriteria.orderDesc(
				User_passwordsProperties.date_change()));


		if (user_passwordsVO.getLogin() != null &&
				!user_passwordsVO.getLogin().equals("")) {
			con.add(ConditionalCriteria.likeOrEqual(User_passwordsProperties.login(),
					user_passwordsVO.getLogin()));
		}

		if (user_passwordsVO.getUsers_fk() != null) {
			con.add(ConditionalCriteria.likeOrEqual(User_passwordsProperties.user_id()
					.users_id(),
					user_passwordsVO.getUsers_fk()));
		}

		List<User_passwords> l_entity =
				this.getUser_passwordsRepository().findByCondition(con);

		List<User_passwordsVO> l = new ArrayList<User_passwordsVO>();

		for (int i = 0; i < l_entity.size(); i++) {
			User_passwordsVO user_passwordsVoTmp = entityToVO(ctx,l_entity.get(i),0);

			l.add(user_passwordsVoTmp);

		}

		return l;

	}

	public User_passwordsVO entityToVO(ServiceContext ctx,User_passwords user_passwords,int lazy_level) {
		User_passwordsVO e = new User_passwordsVO();

		e.setUser_passwords_id(user_passwords.getUser_passwords_id());

		e.setPassword(user_passwords.getPassword());

		e.setDate_change(user_passwords.getDate_change());

		e.setLogin(user_passwords.getLogin());

	/*	if (user_passwords.getUser_id() == null) {
			e.setUsers_fk(0L);
		}
		else {
			e.setUsers_fk(user_passwords.getUser_id().getUsers_id());
		}
	*/

		return e;
	}

	public String changePassword(ServiceContext ctx,String login, String oldpassword,
			String _newPassword) throws Exception {

		int count = 0;

		if (oldpassword == null || oldpassword.equals("")){
			throw new OurException("0017", new Exception());
		}
      
		verifyPassword(ctx, _newPassword);

		//oldpassword = this.getEncryptionService().encryptPassword(ctx, oldpassword, login); 
		final char[] newPassword = this.getEncryptionService().encryptPassword(ctx, _newPassword, login).toCharArray(); 

		UsersVO usersVO = new UsersVO();
		usersVO.setUser_code(login);
		usersVO.setPassword(oldpassword);
		List<UsersVO> users= this.getUsersService().searchUsersService(ctx,usersVO);    

		if (users.size()==0){
			throw new OurException("0059", new Exception());
		}

		User_passwordsVO user_passwordsVO = new User_passwordsVO();
		user_passwordsVO.setLogin(login);  
		List<User_passwordsVO> passes = this.searchUser_passwordsService(ctx,user_passwordsVO);
		count = passes.size();    		

		ProfileVO profileVO = new ProfileVO();
		profileVO.setProfile_id(users.get(0).getProfile_fk());
		List<ProfileVO> profileVOConfigs= this.getProfileService().searchProfileService(ctx,profileVO);

		if (profileVOConfigs.size() == 0){
			throw new OurException("0017", new Exception());
		}

		PasswordVO passwordVO =new PasswordVO();
		passwordVO.setPassword_complexity_id(profileVOConfigs.get(0).getPassword_complexity_fk());

		List<PasswordVO> passConfigs = this.getPasswordService().searchPasswordService(ctx,passwordVO);
		
		if(passConfigs.size() == 0){
			throw new OurException("0017", new Exception());
		}
		
		// password must be not in dictionary.
		final Optional<String> dictionaryStr = Optional.ofNullable(passConfigs.get(0).getDictionary());
		final String[] dictionary = GsonHelper.getGson().fromJson(dictionaryStr.orElse("[]"), String[].class);
		boolean matchesPassInDictionary = Arrays.stream(dictionary)
				.filter(Objects::nonNull)
				.anyMatch(word -> StringUtils.getLevenshteinDistance(word, new String(newPassword)) <= 1);
		if (matchesPassInDictionary) {
			throw new OurException(PwcStatusCode.PASSWORD_IN_DICTIONARY.toString(), null);
		}

		String passHistory = passConfigs.get(0).getPassword_history();
		String lockTime    = passConfigs.get(0).getLock_time();

		int lockTimeValue = Integer.parseInt(lockTime);
		for (int i=0; i < passes.size()&& i <lockTimeValue; i++)
		{
			if (passes.get(i).getPassword().equals(new String(newPassword)))  						
				throw new OurException("0043", new Exception());
		}

		/**************** Verifying password History  *****************/

		if(count > Integer.parseInt(passHistory)){    			
			this.deleteUser_passwordsService(ctx,passes.get(count-1));  
		}
		User_passwordsVO us=new User_passwordsVO();
		Date date=new Date();
		us.setDate_change(new Timestamp(date.getTime()));
		us.setLogin(login);
		us.setPassword(new String(newPassword));
		us.setUsers_fk(users.get(0).getUsers_id());
		this.createUser_passwordsService(ctx,us);   
		/****************************Saving user**********************************/

		users.get(0).setStatus("N");
		users.get(0).setPassword(new String(newPassword));
		this.getUsersService().updateUsersService(ctx,users.get(0)); 
		java.util.Arrays.fill(newPassword, '*');
		
		return "0000";
	}

	public String verifyPassword(ServiceContext ctx, String password)
			throws Exception {

		// the new password must be different than the old password.
		// and doesn't resembles the words in the dictionary.
		PwcStatusCode passwordStatus = isPasswordPredictable(ctx, password);
		if (!passwordStatus.equals(PwcStatusCode.SUCCESS)) {
			throw new OurException(passwordStatus.toString(), new Exception());
		}
		
		Boolean param1 = false, param2 = false, param3 = false, param4 = false, param5 = false, param6 = false;
		List<PasswordVO> passConfigs = this.getPasswordService()
				.getAllPasswordService(ctx);
		String type = passConfigs.get(0).getType();
		String minLength = passConfigs.get(0).getMin_length();
		String maxLength = passConfigs.get(0).getMax_length();
		String minSpecialChar = passConfigs.get(0).getMin_special();
		String minChar = passConfigs.get(0).getMin_characters();
		String allowedChar = passConfigs.get(0).getAllowed_characters();

		/**************** Verifying type of password = param1 *******************/

		if (type.equals("A")) {
			if (password.matches("((?=.*[a-z])(?=.*[A-Z])(?=.*["+allowedChar+"]).{"+minLength+","+maxLength+"})")) {
				param1 = true;
			}
		}
		else if (type.equals("N")) {
			if (password.matches("[0-9]+"))
				param1 = true;
		}
		else if (type.equals("L"))
		{
			if (password.matches("((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*["+allowedChar+"]).{"+minLength+","+maxLength+"})"))
				param1 = true;
		}


		/**************** Verifying minLength of password = param2 *******************/
		if (password.length() >= Integer.parseInt(minLength)) {
			param2 = true;
		}

		/**************** Verifying maxLength of password = param3 *******************/
		if (password.length() <= Integer.parseInt(maxLength)) {
			param3 = true;
		}

		/**************** Verifying minSpecialChar of password = param4 *******************/
		int count = 0;
		if (Integer.parseInt(minSpecialChar) == 0)
			param4 = true;
		else {
			for (int i = 0; i < password.length(); i++) {

				if (!(password.substring(i, i + 1).matches("[a-zA-Z0-9]+")))
					count++;
			}
			if (count >= Integer.parseInt(minSpecialChar))
				param4 = true;
		}

		/**************** Verifying minChar of password = param5 *********************/

		int count1 = 0;
		for (int i = 0; i < password.length(); i++) {

			if (password.substring(i, i + 1).matches("[a-zA-Z0-9]+"))
				count1++;
		}
		if (count1 >= Integer.parseInt(minChar))
			param5 = true;

		/**************** Verifying allowedChar of password = param6 *******************/
		String passSpecial = "";
		for (int i = 0; i < password.length(); i++) {

			if (!(password.substring(i, i + 1).matches("[a-zA-Z0-9]")))

				passSpecial = passSpecial.concat(password.substring(i, i + 1));

		}

		if (Integer.parseInt(minSpecialChar) != 0 || !(passSpecial.equals(""))) {

			for (int i = 0; i < passSpecial.length(); i++) {
				if ((allowedChar.indexOf(passSpecial.substring(i, i + 1)) == -1)) {
					param6 = false;
				}

				else {
					param6 = true;
				}

			}
		} else {
			param6 = true;

		}
		// System.out.println("param1 type: " + param1);
		// System.out.println("param2 minLength : " + param2);
		// System.out.println("param3 maxLength : " + param3);
		// System.out.println("param4 minSpecialChar : " + param4);
		// System.out.println("param5 minChar : " + param5);
		// System.out.println("param6 allowedChar : " + param6);
		/*
			if (!param1)
			{
				throw new OurException("0066", new Exception());
			} */
		if (param1 && param2 && param3 && param4 && param5 && param6)
		{
			return "0000";
		}
		else
		{
			throw new OurException("0018", new Exception());
		}
	}

	public User_passwords VoToEntity(ServiceContext ctx,
			User_passwordsVO userPasswordsVO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public User_passwordsVO entityToVO(ServiceContext ctx,
			User_passwords userPasswords, int lazyLevel, int lazyLevelCol)
					throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * determines if the newPassword is predictable.
	 * 
	 * @param oldPassword
	 * @param newPassword
	 * @return error code if the new password seems obvious or "0000" if the password is approved.
	 */
	private PwcStatusCode isPasswordPredictable(ServiceContext context, String newPassword) {
		
		// userName and userId
		PwcStatusCode passWordInCredentials = passwordInCredentials(context, newPassword);
		if (!passWordInCredentials.equals(PwcStatusCode.SUCCESS)) {
			return passWordInCredentials;
		}
		
		// if the password is one of his old passwords.
		if (isPasswordInOldPasswords(context, context.getUserId(), newPassword)) {
			return PwcStatusCode.PASSWORD_ALREADY_USED;
		}
		
		return PwcStatusCode.SUCCESS;
	}
	
	private boolean isPasswordInOldPasswords(ServiceContext ctx, String login, String newPassword) {
		
		// search for user's old passwords.
		User_passwordsVO user_passwordsVO = new User_passwordsVO();
		user_passwordsVO.setLogin(login);
		List<User_passwordsVO> oldPasswords = null;

		try {
			oldPasswords = searchUser_passwordsService(ctx, user_passwordsVO);
		} catch (Exception e) {
			logger.error("Failed to perform search user_passwords operation");
			return false;
		}
		
		// check the new password against the old passwords.
		return oldPasswords.stream()
				.map(User_passwordsVO::getPassword)
				.filter(Objects::nonNull)
				.anyMatch(password -> BCrypt.checkpw(newPassword, password));
	}
	
	private static PwcStatusCode passwordInCredentials(ServiceContext context, String password) {
		
		// password in userName
		if (context.getUsername() != null && StringUtils.getLevenshteinDistance(context.getUsername(), password) <= 1) {
			return PwcStatusCode.PASSWORD_LIKE_USERNAME;
		}
		
		// password in userId
		if (context.getUserId() != null && StringUtils.getLevenshteinDistance(context.getUserId(), password) <= 1) {
			return PwcStatusCode.PASSWORD_LIKE_USER_ID;
		}
		
		return PwcStatusCode.SUCCESS;
	}

	@Override
	public String changeForgottenPassword(ServiceContext ctx, String login, String newPassword) throws Exception {
		if (newPassword == null || newPassword.equals("")){
			throw new OurException("0107", new Exception());
		}
		verifyPassword(ctx, newPassword);
		final char[] encryptedPassword = this.getEncryptionService().encryptPassword(ctx, newPassword, login).toCharArray();
		UsersVO usersVO = new UsersVO();
		usersVO.setUser_code(login);
		List<UsersVO> listusers= this.getUsersService().searchUsersService(ctx, usersVO);    
		if (listusers == null || listusers.size()==0){
			throw new OurException("0107", new Exception());
		}
		
		PasswordVO passwordVO =new PasswordVO();
		List<PasswordVO> passConfigs = this.getPasswordService().searchPasswordService(ctx, passwordVO);
		if(passConfigs.size() == 0){
			throw new OurException("0107", new Exception());
		}
		final Optional<String> dictionaryStr = Optional.ofNullable(passConfigs.get(0).getDictionary());
		final String[] dictionary = GsonHelper.getGson().fromJson(dictionaryStr.orElse("[]"), String[].class);
		boolean matchesPassInDictionary = Arrays.stream(dictionary)
				.filter(Objects::nonNull)
				.anyMatch(word -> StringUtils.getLevenshteinDistance(word, new String(encryptedPassword)) <= 1);
		if (matchesPassInDictionary) {
			throw new OurException(PwcStatusCode.PASSWORD_IN_DICTIONARY.toString(), null);
		}		
		User_passwordsVO user_passwordsVO = new User_passwordsVO();
		Date date = new Date();
		user_passwordsVO.setDate_change(new Timestamp(date.getTime()));
		user_passwordsVO.setLogin(login);
		user_passwordsVO.setPassword(new String(encryptedPassword));
		user_passwordsVO.setUsers_fk(listusers.get(0).getUsers_id());
		this.createUser_passwordsService(ctx, user_passwordsVO);
		
		UsersVO user = listusers.get(0);
		user.setStatus("N");
		user.setPassword(new String(encryptedPassword));
		this.getUsersService().updateUsersService(ctx, user); 
		java.util.Arrays.fill(encryptedPassword, '*');
		
		return "0000";
	}
	
}
