package ma.hps.powercard.compliance.serviceimpl.spec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.serviceapi.Mail_configService;
import ma.hps.powercard.compliance.serviceapi.Mail_configVO;
import ma.hps.powercard.compliance.serviceapi.PasswordService;
import ma.hps.powercard.compliance.serviceapi.PasswordVO;
import ma.hps.powercard.compliance.serviceapi.User_passwordsService;
import ma.hps.powercard.compliance.serviceapi.UsersService;
import ma.hps.powercard.compliance.serviceapi.UsersVO;
import ma.hps.powercard.compliance.utils.GsonHelper;
import ma.hps.powercard.dto.ErrorResponse;
import ma.hps.powercard.dto.SuccessResponse;

@RestController
public class PwcInitPasswordService {

	public static Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();
	private static final String USER_NOT_FOUND = "0107";
	private static final String INVALID_OTP = "0108";
	private static final String INVALID_TOKEN = "0109";

	@Autowired
	private UsersService usersService;

	@Autowired
	private User_passwordsService user_passwordsService;

	@Autowired
	private PasswordService passwordService;

	@Autowired
	private Mail_configService mail_configService;

	@CrossOrigin
	@PostMapping(value = "/forgotpassword", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String checkUserByLogin(@RequestParam("login") String login, HttpServletRequest request) {
		JsonObject json = new JsonObject();
		if (login == null || login.equals(""))
			return ErrorResponse.from(USER_NOT_FOUND).toJson();
		UsersVO usersVO = new UsersVO();
		usersVO.setUser_code(login);
		try {
			List<UsersVO> listUsers = this.usersService.searchUsersService(new ServiceContext(), usersVO);
			if (listUsers == null || (listUsers != null && listUsers.size() == 0)) {
				return SuccessResponse.from(json).toJson();
			}

			SessionKeyBean sessionKeyBean = SessionSign.getSessionKey(request);
			String token = sessionKeyBean.getToken();
			String otp = generate_otp();
			String email = listUsers.get(0).getMail();
			HttpSession session = CustomSessionListener.sessions.get(token);
			session.setAttribute("otp", otp);
			session.setAttribute("email", email);
			session.setAttribute("login", listUsers.get(0).getUser_code());
			session.setAttribute("username", listUsers.get(0).getUser_name());
			CustomSessionListener.sessions.put(token, session);
			json.addProperty("token", token);
			json.addProperty("publicKey", sessionKeyBean.getPublicKey());
			json.addProperty("email", email);
			Mail_configVO mail_configVO = new Mail_configVO();
			mail_configVO.setMail_config_id(12L);
			List<Mail_configVO> listMailConfig = this.mail_configService.searchMail_configService(new ServiceContext(),
					mail_configVO);
			if (listMailConfig == null || listMailConfig.size() == 0) {
				return ErrorResponse.from("Mail config not found !").toJson();
			}
			boolean use_smtps = listMailConfig.get(0).getEnable_smtps() != null && listMailConfig.get(0).getEnable_smtps().equalsIgnoreCase("Y");
			sendEmail(listMailConfig.get(0).getEmail_server(), listMailConfig.get(0).getPort_number(),
					listMailConfig.get(0).getEmail_sender(), email, "Your code is : " + otp, "Your code is : " + otp, use_smtps);
		} catch (Exception e) {
			return ErrorResponse.from(e.getMessage()).toJson();
		}
		return SuccessResponse.from(json).toJson();
	}

	@CrossOrigin
	@PostMapping(value = "/verify_otp", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String verify_otp(@RequestParam("otp") String otp, @RequestParam("token") String token,
			HttpServletRequest request) {
		JsonObject json = new JsonObject();
		try {
			if (otp == null || otp.equals(""))
				return ErrorResponse.from(INVALID_OTP).toJson();
			if (token == null || token.equals(""))
				return ErrorResponse.from(INVALID_TOKEN).toJson();
			HttpSession session = CustomSessionListener.sessions.get(token);
			if (session == null) {
				return ErrorResponse.from("Session is invalid !").toJson();
			}
			String session_otp = (String) session.getAttribute("otp");
			if (session_otp == null || !session_otp.equals(otp)) {
				return ErrorResponse.from(INVALID_OTP).toJson();
			}
			if (session_otp.equals(otp)) {
				List<PasswordVO> list = this.passwordService.searchPasswordService(new ServiceContext(),
						new PasswordVO());
				if (list == null || list.size() == 0)
					return ErrorResponse.from("Cannot verify otp !").toJson();
				json.addProperty("policy", GsonHelper.getGson().toJson(list.get(0)));
				return SuccessResponse.from(json).toJson();
			}
			return ErrorResponse.from("Cannot verify otp !").toJson();
		} catch (Exception e) {
			return ErrorResponse.from("Cannot verify otp !").toJson();
		}
	}

	@CrossOrigin
	@PostMapping(value = "/change_forgotten_password", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String change_forgotten_password(@RequestParam("token") String token,
			@RequestParam("new_password") String new_password) {
		try {
			HttpSession session = CustomSessionListener.sessions.get(token);
			if (session == null) {
				return ErrorResponse.from("Session is invalid !").toJson();
			}
			RestServiceContextStore restServiceContext = (RestServiceContextStore) session
					.getAttribute("RestServiceContext");
			if (restServiceContext == null) {
				return ErrorResponse.from("Null RestServiceContextStore is not allowed !").toJson();
			}
			String login = (String) session.getAttribute("login");
			char[] decryptedNewPass = SecurityKeysProvider.decrypt(SecurityKeysProvider.decodeBASE64(new_password),
					restServiceContext.getKey().getPrivateKey()).toCharArray();
			ServiceContext ctx = new ServiceContext(login, token, "pwc35");
			this.user_passwordsService.changeForgottenPassword(ctx, login, new String(decryptedNewPass));
		} catch (Exception e) {
			return ErrorResponse.from(e.getMessage()).toJson();
		}
		return SuccessResponse.from("0000").toJson();
	}

	private void sendEmail(String server_host, String server_port, String sender, String recipient,
			String message_subject, String message_body, boolean use_smtps) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.host", server_host);
		props.put("mail.smtp.port", server_port);
		props.put("mail.smtp.timeout", "60000");
		props.put("mail.smtp.connectiontimeout", "60000");
		if (use_smtps) {
			props.put("mail.transport.protocol", "smtps");
		}
		Session session = Session.getDefaultInstance(props);
		session.setDebug(false);
		Message message = new MimeMessage(session);
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		message.setFrom(new InternetAddress(sender));
		message.setSubject(message_subject);
		message.setText(message_body);
		try {
			// System.out.println("###### :::: " + message_body);
			if (use_smtps) {
				Transport transport = session.getTransport();
	        	transport.connect(server_host, Integer.parseInt(server_port), null, null);
	        	transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
	        	transport.close();
			} else {
				Transport.send(message);
			}
		} catch (MessagingException e) {
			throw new OurException("1259", new Exception(e.getMessage()));
		}
	}

	private String generate_otp() {
		Random random = new Random();
		Integer otp = new Integer(100000 + random.nextInt(900000));
		return otp.toString();
	}

}
