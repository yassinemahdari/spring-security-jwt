package ma.hps.powercard.compliance.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.domain.PagedResult;
import org.fornax.cartridges.sculptor.framework.domain.PagingParameter;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.Multi_lang_tables;
import ma.hps.powercard.compliance.domain.Multi_lang_tablesProperties;
import ma.hps.powercard.compliance.exception.Multi_lang_tablesNotFoundException;
import ma.hps.powercard.compliance.repositoryimpl.Multi_lang_tablesUtils;
import ma.hps.powercard.compliance.serviceapi.Multi_lang_tablesVO;

/**
 * Implementation222 of Multi_lang_tablesService.
 */
@Lazy
@Service("multi_lang_tablesService")
public class Multi_lang_tablesServiceImpl extends Multi_lang_tablesServiceImplBase {
	private static Logger logger = Logger.getLogger(Multi_lang_tablesServiceImpl.class);

	public Multi_lang_tablesServiceImpl() {
	}

	/**
	 * Persist a Multi_lang_tables entity .
	 *
	 * @param Multi_lang_tablesVO
	 *            ValueObject.
	 *
	 * @return 0000 - if Success
	 *
	 * @throws OurException
	 *             No.0024 If ever the object already exists.
	 *
	 */
	public String createMulti_lang_tablesService(ServiceContext ctx, Multi_lang_tablesVO multi_lang_tablesVO)
			throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:createMulti_lang_tablesService , USER :" + ctx.getUserId()
					+ " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		Multi_lang_tables multi_lang_tables = null;
		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(Multi_lang_tablesProperties.table_name(),
				multi_lang_tablesVO.getTable_name()));

		PagingParameter pagingParameter = PagingParameter.pageAccess(1, 1, false);
		PagedResult<Multi_lang_tables> pagedResult = this.getMulti_lang_tablesRepository().findByCondition(con,
				pagingParameter);
		List<Multi_lang_tables> list = pagedResult.getValues();

		if (list.size() > 0) {
			throw new OurException("0024", new Exception(""));
		} else {

			multi_lang_tables = new Multi_lang_tables(multi_lang_tablesVO.getTable_name());
		}

		// verify : bank data access
		try {
			List<String> banks_access = new ArrayList<String>((List<String>) ctx.getProperty("bankDataAccess"));
			List<String> banks_vb = null;

		} catch (Exception e) {
			if (!e.getMessage().equals("0403")) {
				logger.error("Verifying bank data access error");
				logger.error(e.getMessage());
			} else {
				logger.error("Bank data access restriction");
				throw e;
			}
		}

		Multi_lang_tables multi_lang_tables1 = this.getMulti_lang_tablesRepository().save(multi_lang_tables);

		return "0000";

	}

	/**
	 * update a Multi_lang_tables entity .
	 *
	 * @param Multi_lang_tablesVO
	 *            ValueObject.
	 *
	 * @return 0000 - if Success
	 *
	 * @throws OurException
	 *             No.0001 If the object don't exists.
	 *
	 */
	public String updateMulti_lang_tablesService(ServiceContext ctx, Multi_lang_tablesVO multi_lang_tablesVO)
			throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:updateMulti_lang_tablesService , USER :" + ctx.getUserId()
					+ " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		Multi_lang_tables multi_lang_tables = null;

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(Multi_lang_tablesProperties.table_name(),
				multi_lang_tablesVO.getTable_name()));

		PagingParameter pagingParameter = PagingParameter.pageAccess(1, 1, false);
		PagedResult<Multi_lang_tables> pagedResult = this.getMulti_lang_tablesRepository().findByCondition(con,
				pagingParameter);
		List<Multi_lang_tables> list = pagedResult.getValues();

		if (list.size() > 0) {

			multi_lang_tables = list.get(0);
		} else {
			throw new OurException("0001", new Multi_lang_tablesNotFoundException(""));
		}

		// verify : bank data access
		try {
			List<String> banks_access = new ArrayList<String>((List<String>) ctx.getProperty("bankDataAccess"));
			List<String> banks_vb = null;

		} catch (Exception e) {
			if (!e.getMessage().equals("0403")) {
				logger.info("Verifying bank data access error");
				logger.error(e.getMessage());
			} else {
				logger.info("Bank data access restriction");
				throw e;
			}
		}

		Multi_lang_tables multi_lang_tables1 = this.getMulti_lang_tablesRepository().save(multi_lang_tables);

		return "0000";

	}

	/**
	 * delete a Multi_lang_tables entity .
	 *
	 * @param Multi_lang_tablesVO
	 *            ValueObject.
	 *
	 * @return 0000 - if Success
	 *
	 * @throws OurException
	 *             No.0001 If the object don't exists.
	 *
	 */
	public String deleteMulti_lang_tablesService(ServiceContext ctx, Multi_lang_tablesVO multi_lang_tablesVO)
			throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:deleteMulti_lang_tablesService , USER :" + ctx.getUserId()
					+ " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		Multi_lang_tables multi_lang_tables = null;

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(Multi_lang_tablesProperties.table_name(),
				multi_lang_tablesVO.getTable_name()));

		PagingParameter pagingParameter = PagingParameter.pageAccess(1, 1, false);
		PagedResult<Multi_lang_tables> pagedResult = this.getMulti_lang_tablesRepository().findByCondition(con,
				pagingParameter);
		List<Multi_lang_tables> list = pagedResult.getValues();

		if (list.size() > 0) {

			multi_lang_tables = list.get(0);
		} else {
			throw new OurException("0001", new Multi_lang_tablesNotFoundException(""));
		}

		// verify : bank data access
		try {
			List<String> banks_access = new ArrayList<String>((List<String>) ctx.getProperty("bankDataAccess"));
			List<String> banks_vb = null;

		} catch (Exception e) {
			if (!e.getMessage().equals("0403")) {
				logger.info("Verifying bank data access error");
				logger.error(e.getMessage());
			} else {
				logger.info("Bank data access restriction");
				throw e;
			}
		}

		this.getMulti_lang_tablesRepository().delete(multi_lang_tables);

		return "0000";

	}

	/**
	 * Find all entities of a specific type .
	 *
	 * @return List of Multi_lang_tablesVO
	 *
	 */
	public List<Multi_lang_tablesVO> getAllMulti_lang_tablesService(ServiceContext ctx) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:getAllMulti_lang_tablesService , USER :" + ctx.getUserId()
					+ " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}

		List<Multi_lang_tablesVO> l = new ArrayList<Multi_lang_tablesVO>();

		List<Multi_lang_tables> l_entity = new ArrayList<Multi_lang_tables>();

		l_entity = this.getMulti_lang_tablesRepository().findAll();

		l = Multi_lang_tablesUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

		return l;

	}

	/**
	 * Find entities by conditions
	 *
	 * @return List of Multi_lang_tablesVO
	 *
	 */
	public List<Multi_lang_tablesVO> searchMulti_lang_tablesService(ServiceContext ctx,
			Multi_lang_tablesVO multi_lang_tablesVO) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:searchMulti_lang_tablesService , USER :" + ctx.getUserId()
					+ " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		if (ctx != null) {
			Multi_lang_tablesUtils.dataFilter(ctx, con);
		}

		Multi_lang_tablesUtils.setListOfCriteria(ctx, con, multi_lang_tablesVO);

		List<Multi_lang_tables> l_entity = new ArrayList<Multi_lang_tables>();
		List<Multi_lang_tablesVO> l = new ArrayList<Multi_lang_tablesVO>();

		int page = multi_lang_tablesVO.getPage();
		int pageSize = multi_lang_tablesVO.getPageSize();
		boolean countTotalPages = true;
		if (page > 0 && pageSize > 0) {
			PagingParameter pagingParameter = PagingParameter.pageAccess(pageSize, page, countTotalPages);

			PagedResult<Multi_lang_tables> pagedResult = this.getMulti_lang_tablesRepository().findByCondition(con,
					pagingParameter);

			l_entity = pagedResult.getValues();

			ctx.setProperty("pagedResult", pagedResult);

		} else {
			l_entity = this.getMulti_lang_tablesRepository().findByCondition(con);
		}

		l = Multi_lang_tablesUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

		l = Multi_lang_tablesUtils.filterMultiLang(multi_lang_tablesVO, l);

		return l;
	}

	@Cacheable(value = "cachedMultiLangTables", key = "#root.method.name")
	public List<Multi_lang_tablesVO> getCachedMulti_lang_tables(ServiceContext ctx) throws Exception {

		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : getCachedMulti_lang_tablesService , USER :" + ctx.getUserId() + " , SessionID :"
					+ sessionID + " , RemoteAddress:" + remoteAddress);
		}

		List<Multi_lang_tablesVO> l = new ArrayList<Multi_lang_tablesVO>();

		List<Multi_lang_tables> l_entity = new ArrayList<Multi_lang_tables>();

		l_entity = this.getMulti_lang_tablesRepository().findAll();

		l = Multi_lang_tablesUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

		return l;

	}

}