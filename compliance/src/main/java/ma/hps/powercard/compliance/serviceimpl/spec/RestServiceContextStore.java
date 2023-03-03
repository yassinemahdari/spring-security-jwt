package ma.hps.powercard.compliance.serviceimpl.spec;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;

import ma.hps.powercard.compliance.domain.User_context;
import ma.hps.powercard.compliance.serviceapi.MenuVO;
import ma.hps.powercard.compliance.serviceapi.User_contextVO;

public class RestServiceContextStore {
	private String jwt;
	private String code;
	private MenuVO[] menus;
	private String message;
	private String uuid;
	private Key key;
	HashMap<String, Collection<String>> modulesByWorkspaces;
	HashMap<String, Collection<Module>> menuGroupBymodulesAndWorkspaces;
	private List<User_contextVO> userContexts;
	private Date businessDate;
	private List<String> bankDataAccess;
	private boolean isLdapLogged;
	private String serverTimeZone;
	private ServiceContext serviceContext;


	public RestServiceContextStore() {
		super();
	}

	public RestServiceContextStore(String jwt, Key key, ServiceContext serviceContext, String uuid, MenuVO[] menus) {
		this.jwt = jwt;
		this.key = key;
		this.serviceContext = serviceContext;
		this.uuid = uuid;
		this.menus = menus;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public ServiceContext getServiceContext() {
		return serviceContext;
	}

	public void setServiceContext(ServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	public MenuVO[] getMenus() {
		return menus;
	}

	public void setMenus(MenuVO[] menus) {
		this.menus = menus;
	}

	public HashMap<String, Collection<String>> getModulesByWorkspaces() {
		return modulesByWorkspaces;
	}

	public void setModulesByWorkspaces(HashMap<String, Collection<String>> modulesByWorkspaces) {
		this.modulesByWorkspaces = modulesByWorkspaces;
	}

	public HashMap<String, Collection<Module>> getMenuGroupBymodulesAndWorkspaces() {
		return menuGroupBymodulesAndWorkspaces;
	}

	public void setMenuGroupBymodulesAndWorkspaces(
			HashMap<String, Collection<Module>> menuGroupBymodulesAndWorkspaces) {
		this.menuGroupBymodulesAndWorkspaces = menuGroupBymodulesAndWorkspaces;
	}

	public List<User_contextVO> getUserContexts() {
		return userContexts;
	}

	public void setUserContexts(List<User_contextVO> userContexts) {
		this.userContexts = userContexts;
	}

	public Date getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(Date businessDate) {
		this.businessDate = businessDate;
	}

	public boolean getIsLdapLogged() {
		return isLdapLogged;
	}

	public void setIsLdapLogged(boolean isLdapLogged) {
		this.isLdapLogged = isLdapLogged;
	}

	public List<String> getBankDataAccess() {
		return bankDataAccess;
	}

	public void setBankDataAccess(List<String> bankDataAccess) {
		this.bankDataAccess = bankDataAccess;
	}

	public String getServerTimeZone() {
		return serverTimeZone;
	}

	public void setServerTimeZone(String serverTimeZone) {
		this.serverTimeZone = serverTimeZone;
	}
	
}