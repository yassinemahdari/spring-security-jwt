package ma.hps.powercard.compliance.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.domain.PagedResult;
import org.fornax.cartridges.sculptor.framework.domain.PagingParameter;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.Ldap_param_by_bank;
import ma.hps.powercard.compliance.domain.Ldap_param_by_bankProperties;
import ma.hps.powercard.compliance.exception.Ldap_param_by_bankNotFoundException;
import ma.hps.powercard.compliance.repositoryimpl.Ldap_param_by_bankUtils;
import ma.hps.powercard.compliance.serviceapi.Ldap_param_by_bankVO;

/**
 * Implementation222 of Ldap_param_by_bankService.
 */
@Lazy
@Service("ldap_param_by_bankService")
public class Ldap_param_by_bankServiceImpl extends Ldap_param_by_bankServiceImplBase {
	private static Logger logger = Logger.getLogger(Ldap_param_by_bankServiceImpl.class);

	public Ldap_param_by_bankServiceImpl() {
	}

	/**
	 * Persist a Ldap_param_by_bank entity .
	 *
	 * @param Ldap_param_by_bankVO
	 *            ValueObject.
	 *
	 * @return 0000 - if Success
	 *
	 * @throws OurException
	 *             No.0024 If ever the object already exists.
	 *
	 */
	public String createLdap_param_by_bankService(ServiceContext ctx, Ldap_param_by_bankVO ldap_param_by_bankVO)
			throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:createLdap_param_by_bankService , USER :" + ctx.getUserId()
					+ " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		Ldap_param_by_bank ldap_param_by_bank = null;
		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(Ldap_param_by_bankProperties.bank_code(),
				ldap_param_by_bankVO.getBank_code()));

		PagingParameter pagingParameter = PagingParameter.pageAccess(1, 1, false);
		PagedResult<Ldap_param_by_bank> pagedResult = this.getLdap_param_by_bankRepository().findByCondition(con,
				pagingParameter);
		List<Ldap_param_by_bank> list = pagedResult.getValues();

		if (list.size() > 0) {
			throw new OurException("0024", new Exception(""));
		} else {
			ldap_param_by_bank = new Ldap_param_by_bank(ldap_param_by_bankVO.getBank_code());
		}

		ldap_param_by_bank.setStatus_ldap(ldap_param_by_bankVO.getStatus_ldap());

		ldap_param_by_bank.setLdap_url(ldap_param_by_bankVO.getLdap_url());

		ldap_param_by_bank.setLdap_base(ldap_param_by_bankVO.getLdap_base());

		ldap_param_by_bank.setDomaine_name(ldap_param_by_bankVO.getDomaine_name());

		ldap_param_by_bank.setGroup_search_base(ldap_param_by_bankVO.getGroup_search_base());

		ldap_param_by_bank.setManager_id(ldap_param_by_bankVO.getManager_id());

		ldap_param_by_bank.setManager_password(ldap_param_by_bankVO.getManager_password());

		ldap_param_by_bank.setMapped_profiles(ldap_param_by_bankVO.getMapped_profiles());

		Ldap_param_by_bank ldap_param_by_bank1 = this.getLdap_param_by_bankRepository().save(ldap_param_by_bank);

		return "0000";

	}

	/**
	 * update a Ldap_param_by_bank entity .
	 *
	 * @param Ldap_param_by_bankVO
	 *            ValueObject.
	 *
	 * @return 0000 - if Success
	 *
	 * @throws OurException
	 *             No.0001 If the object don't exists.
	 *
	 */
	public String updateLdap_param_by_bankService(ServiceContext ctx, Ldap_param_by_bankVO ldap_param_by_bankVO)
			throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:updateLdap_param_by_bankService , USER :" + ctx.getUserId()
					+ " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		Ldap_param_by_bank ldap_param_by_bank = null;

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(Ldap_param_by_bankProperties.bank_code(),
				ldap_param_by_bankVO.getBank_code()));

		PagingParameter pagingParameter = PagingParameter.pageAccess(1, 1, false);
		PagedResult<Ldap_param_by_bank> pagedResult = this.getLdap_param_by_bankRepository().findByCondition(con,
				pagingParameter);
		List<Ldap_param_by_bank> list = pagedResult.getValues();

		if (list.size() > 0) {

			ldap_param_by_bank = list.get(0);
		} else {
			throw new OurException("0001", new Ldap_param_by_bankNotFoundException(""));
		}

		if (ldap_param_by_bankVO.getStatus_ldap() != null) {
			ldap_param_by_bank.setStatus_ldap(ldap_param_by_bankVO.getStatus_ldap());
		}

		if (ldap_param_by_bankVO.getLdap_url() != null) {
			ldap_param_by_bank.setLdap_url(ldap_param_by_bankVO.getLdap_url());
		}

		if (ldap_param_by_bankVO.getLdap_base() != null) {
			ldap_param_by_bank.setLdap_base(ldap_param_by_bankVO.getLdap_base());
		}

		if (ldap_param_by_bankVO.getDomaine_name() != null) {
			ldap_param_by_bank.setDomaine_name(ldap_param_by_bankVO.getDomaine_name());
		}

		if (ldap_param_by_bankVO.getGroup_search_base() != null) {
			ldap_param_by_bank.setGroup_search_base(ldap_param_by_bankVO.getGroup_search_base());
		}

		if (ldap_param_by_bankVO.getManager_id() != null) {
			ldap_param_by_bank.setManager_id(ldap_param_by_bankVO.getManager_id());
		}

		if (ldap_param_by_bankVO.getManager_password() != null) {
			ldap_param_by_bank.setManager_password(ldap_param_by_bankVO.getManager_password());
		}

		if (ldap_param_by_bankVO.getMapped_profiles() != null) {
			ldap_param_by_bank.setMapped_profiles(ldap_param_by_bankVO.getMapped_profiles());
		}

		Ldap_param_by_bank ldap_param_by_bank1 = this.getLdap_param_by_bankRepository().save(ldap_param_by_bank);

		return "0000";

	}

	/**
	 * delete a Ldap_param_by_bank entity .
	 *
	 * @param Ldap_param_by_bankVO
	 *            ValueObject.
	 *
	 * @return 0000 - if Success
	 *
	 * @throws OurException
	 *             No.0001 If the object don't exists.
	 *
	 */
	public String deleteLdap_param_by_bankService(ServiceContext ctx, Ldap_param_by_bankVO ldap_param_by_bankVO)
			throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:deleteLdap_param_by_bankService , USER :" + ctx.getUserId()
					+ " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		Ldap_param_by_bank ldap_param_by_bank = null;

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(Ldap_param_by_bankProperties.bank_code(),
				ldap_param_by_bankVO.getBank_code()));

		PagingParameter pagingParameter = PagingParameter.pageAccess(1, 1, false);
		PagedResult<Ldap_param_by_bank> pagedResult = this.getLdap_param_by_bankRepository().findByCondition(con,
				pagingParameter);
		List<Ldap_param_by_bank> list = pagedResult.getValues();

		if (list.size() > 0) {

			ldap_param_by_bank = list.get(0);
		} else {
			throw new OurException("0001", new Ldap_param_by_bankNotFoundException(""));
		}

		this.getLdap_param_by_bankRepository().delete(ldap_param_by_bank);

		return "0000";

	}

	/**
	 * Find all entities of a specific type .
	 *
	 * @return List of Ldap_param_by_bankVO
	 *
	 */
	public List<Ldap_param_by_bankVO> getAllLdap_param_by_bankService(ServiceContext ctx) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:getAllLdap_param_by_bankService , USER :" + ctx.getUserId()
					+ " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}

		List<Ldap_param_by_bankVO> l = new ArrayList<Ldap_param_by_bankVO>();

		List<Ldap_param_by_bank> l_entity = new ArrayList<Ldap_param_by_bank>();

		l_entity = this.getLdap_param_by_bankRepository().findAll();

		l = Ldap_param_by_bankUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

		return l;

	}

	/**
	 * Find all ldap profiles based on manager credentials.
	 *
	 * @return List of Profile_id strings.
	 *
	 */
	public List<String> findAllLdapProfilesService(ServiceContext ctx, String manager_id, String manager_password,
			String ldap_url, String ldap_base) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		List<String> result = new ArrayList<>();
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:findAllLdap_param_by_bankService , USER :" + ctx.getUserId()
					+ " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}
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
			String filter = "(objectClass=group)";
			SearchControls profileControls = new SearchControls();
			profileControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			profileControls.setReturningAttributes(new String[] { "cn" });
			NamingEnumeration profileValue = context.search(ldap_base, filter, profileControls);
			while (profileValue.hasMore()) {
				SearchResult sr = (SearchResult) profileValue.next();
				NamingEnumeration ne = sr.getAttributes().get("cn").getAll();
				List<String> list = Collections.list(ne);
				list.removeAll(Collections.singleton(null));
				for (String ldapProfile : list) {
					if (!result.contains(ldapProfile)) {
						result.add(ldapProfile);
					}
				}

			}
			context.close();
		} catch (NamingException e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * Find entities by conditions
	 *
	 * @return List of Ldap_param_by_bankVO
	 *
	 */
	public List<Ldap_param_by_bankVO> searchLdap_param_by_bankService(ServiceContext ctx,
			Ldap_param_by_bankVO ldap_param_by_bankVO) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:searchLdap_param_by_bankService , USER :" + ctx.getUserId()
					+ " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		if (ctx != null) {
			Ldap_param_by_bankUtils.dataFilter(ctx, con);
		}

		Ldap_param_by_bankUtils.setListOfCriteria(ctx, con, ldap_param_by_bankVO);

		List<Ldap_param_by_bank> l_entity = new ArrayList<Ldap_param_by_bank>();
		List<Ldap_param_by_bankVO> l = new ArrayList<Ldap_param_by_bankVO>();

		int page = ldap_param_by_bankVO.getPage();
		int pageSize = ldap_param_by_bankVO.getPageSize();
		boolean countTotalPages = true;
		if (page > 0 && pageSize > 0) {
			PagingParameter pagingParameter = PagingParameter.pageAccess(pageSize, page, countTotalPages);

			PagedResult<Ldap_param_by_bank> pagedResult = this.getLdap_param_by_bankRepository().findByCondition(con,
					pagingParameter);

			l_entity = pagedResult.getValues();

			ctx.setProperty("pagedResult", pagedResult);

		} else {
			l_entity = this.getLdap_param_by_bankRepository().findByCondition(con);
		}

		l = Ldap_param_by_bankUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

		l = Ldap_param_by_bankUtils.filterMultiLang(ldap_param_by_bankVO, l);

		return l;

	}

}
