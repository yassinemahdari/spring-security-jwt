package ma.hps.powercard.compliance.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.User_context;
import ma.hps.powercard.compliance.domain.User_contextPK;
import ma.hps.powercard.compliance.domain.User_contextProperties;
import ma.hps.powercard.compliance.domain.Users;
import ma.hps.powercard.compliance.exception.User_contextNotFoundException;
import ma.hps.powercard.compliance.repositoryimpl.User_contextUtils;
import ma.hps.powercard.compliance.serviceapi.User_contextVO;
import ma.hps.powercard.compliance.serviceimpl.UserPreferencesServiceImplBase;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of UserPreferencesService.
 */
@Lazy
@Service("userPreferencesService")
public class UserPreferencesServiceImpl extends UserPreferencesServiceImplBase {
	
	private static Logger logger = Logger.getLogger(UserPreferencesServiceImpl.class);

	public UserPreferencesServiceImpl() {
	}

	/**
	 * Persist a User_context entity .
	 *
	 * @param User_contextVO
	 *            ValueObject.
	 *
	 * @return 0000 - if Success
	 *
	 * @throws OurException
	 *             No.0024 If ever the object already exists.
	 *
	 */
	public String createUserContext(ServiceContext ctx, User_contextVO user_contextVO) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:createUser_contextService , USER :" + ctx.getUserId() + " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		User_contextPK user_contextPK = new User_contextPK(user_contextVO.getContext_key(), user_contextVO.getUser_id());

		User_context user_context = null;
		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(User_contextProperties.user_contextPK().context_key(), user_contextVO.getContext_key()));

		con.add(ConditionalCriteria.equal(User_contextProperties.user_contextPK().users_id(), user_contextVO.getUser_id()));

		List<User_context> list = this.getUser_contextRepository().findByCondition(con);

		if (list.size() > 0) {
			throw new OurException("0024", new Exception(""));
		} else {
			user_context = new User_context(user_contextPK);
		}

		if (user_contextVO.getUser_id() != null) {
			user_context.setFk_user_context(new Users(user_contextVO.getUser_id()));

		} else {
		}

		user_context.setContext_value(user_contextVO.getContext_value());

		User_context user_context1 = this.getUser_contextRepository().save(user_context);

		return "0000";

	}

	/**
	 * update a User_context entity .
	 *
	 * @param User_contextVO
	 *            ValueObject.
	 *
	 * @return 0000 - if Success
	 *
	 * @throws OurException
	 *             No.0001 If the object don't exists.
	 *
	 */
	public String updateUserContext(ServiceContext ctx, User_contextVO user_contextVO) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:updateUserContext , USER :" + ctx.getUserId() + " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		User_contextPK user_contextPK = new User_contextPK(user_contextVO.getContext_key(), user_contextVO.getUser_id());

		User_context user_context = null;

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(User_contextProperties.user_contextPK().context_key(), user_contextVO.getContext_key()));

		con.add(ConditionalCriteria.equal(User_contextProperties.user_contextPK().users_id(), user_contextVO.getUser_id()));

		List<User_context> list = this.getUser_contextRepository().findByCondition(con);

		if (list.size() > 0) {
			user_context = list.get(0);
		} else {
			throw new OurException("0001", new User_contextNotFoundException(""));
		}

		if (user_contextVO.getUser_id() != null) {
			user_context.setFk_user_context(new Users(user_contextVO.getUser_id()));

		} else {
		}

		if (user_contextVO.getContext_value() != null) {
			user_context.setContext_value(user_contextVO.getContext_value());
		}

		User_context user_context1 = this.getUser_contextRepository().save(user_context);

		return "0000";

	}



	/**
	 * Find entities by conditions
	 *
	 * @return List of User_contextVO
	 *
	 */
	public List<User_contextVO> searchUserPreferences(ServiceContext ctx, User_contextVO user_contextVO) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:searchUserPreferences , USER :" + ctx.getUserId() + " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		if (ctx != null) {
			User_contextUtils.dataFilter(ctx, con);
		}

		User_contextUtils.setListOfCriteria(ctx, con, user_contextVO);
		
		// Filter by context keys which belong to user preferences.
		List<String> contextKeys = new ArrayList<String>();
		contextKeys.add("bank");
		contextKeys.add("dt_row_count");
		contextKeys.add("language");
		con.add(ConditionalCriteria.in(User_contextProperties.user_contextPK().context_key(), contextKeys));

		List<User_context> l_entity = new ArrayList<User_context>();
		List<User_contextVO> l = new ArrayList<User_contextVO>();

		l_entity = this.getUser_contextRepository().findByCondition(con);

		l = User_contextUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

		return l;

	}

	public List<User_contextVO> saveUserPreferences(ServiceContext ctx, List<User_contextVO> entities) throws Exception {
		if (entities != null && !entities.isEmpty()) {
			for (User_contextVO uc : entities) {
				if (uc.getUser_id() == null || uc.getUser_id() == 0L) {
					uc.setUser_id(ctx.getUser_id());
					createUserContext(ctx, uc);
				} else {
					updateUserContext(ctx, uc);
				}
			}
		}
		return entities;
	}
	
}