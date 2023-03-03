package ma.hps.powercard.compliance.serviceimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.Columns_mask;
import ma.hps.powercard.compliance.domain.Mask_chain;
import ma.hps.powercard.compliance.domain.Mask_config;
import ma.hps.powercard.compliance.domain.Mask_configProperties;
import ma.hps.powercard.compliance.domain.Mask_profile;
import ma.hps.powercard.compliance.domain.Tables_mask;
import ma.hps.powercard.compliance.exception.Mask_configNotFoundException;
import ma.hps.powercard.compliance.repositoryimpl.Mask_configUtils;
import ma.hps.powercard.compliance.serviceapi.Mask_configVO;
import ma.hps.powercard.compliance.serviceapi.Mask_configSpecificVO;


import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of Mask_configService.
 */
@Lazy
@Service("mask_configService")
public class Mask_configServiceImpl extends Mask_configServiceImplBase {
	private static Logger logger = Logger.getLogger(Mask_configServiceImpl.class);

	public Mask_configServiceImpl() {
		

	}

	/**
	 * Persist a Mask_config entity .
	 * 
	 * @param Mask_configVO
	 *            ValueObject.
	 * 
	 * @return 0000 - if Success
	 * 
	 * @throws OurException
	 *             No.0024 If ever the object already exists.
	 * 
	 */
	public String createMask_configService(ServiceContext ctx,
			Mask_configVO mask_configVO) throws Exception {
		ServiceContextStore.set(ctx);

		Mask_config mask_config = new Mask_config();


		if (mask_configVO.getTable_mask_fk() != null
				&& !mask_configVO.getTable_mask_fk().equals("")) {
			mask_config.setFk_mask_config1(new Tables_mask(mask_configVO
					.getTable_mask_fk()));

		} else {
			mask_config.setFk_mask_config1(null);
		}

		if (mask_configVO.getColumn_mask_fk() != null
				&& !mask_configVO.getColumn_mask_fk().equals("")) {
			mask_config.setFk_mask_config2(new Columns_mask(mask_configVO
					.getColumn_mask_fk()));

		} else {
			mask_config.setFk_mask_config2(null);
		}

		if (mask_configVO.getMask_profile_fk() != null
				&& !mask_configVO.getMask_profile_fk().equals("")) {
			mask_config.setFk_mask_config3(new Mask_profile(mask_configVO
					.getMask_profile_fk()));

		} else {
			mask_config.setFk_mask_config3(null);
		}

		mask_config.setName(mask_configVO.getName());

		mask_config.setWording(mask_configVO.getWording());

		mask_config.setMask_flag(mask_configVO.getMask_flag());

		mask_config.setOwner(mask_configVO.getOwner());

		Mask_config mask_config1 = this.getMask_configRepository().save(
				mask_config);

		return "0000";

	}

	/**
	 * update a Mask_config entity .
	 * 
	 * @param Mask_configVO
	 *            ValueObject.
	 * 
	 * @return 0000 - if Success
	 * 
	 * @throws OurException
	 *             No.0001 If the object don't exists.
	 * 
	 */
	public String updateMask_configService(ServiceContext ctx,
			Mask_configVO mask_configVO) throws Exception {
		ServiceContextStore.set(ctx);

		Mask_config mask_config = null;

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(Mask_configProperties.mask_id(),
				mask_configVO.getMask_id()));

		List<Mask_config> list = this.getMask_configRepository()
				.findByCondition(con);

		if (list.size() > 0) {
			mask_config = list.get(0);
		} else {
			throw new OurException("0001", new Mask_configNotFoundException(""));
		}

		if (mask_configVO.getTable_mask_fk() != null
				&& !mask_configVO.getTable_mask_fk().equals("")) {
			mask_config.setFk_mask_config1(new Tables_mask(mask_configVO
					.getTable_mask_fk()));

		} else {
			mask_config.setFk_mask_config1(null);
		}

		if (mask_configVO.getColumn_mask_fk() != null
				&& !mask_configVO.getColumn_mask_fk().equals("")) {
			mask_config.setFk_mask_config2(new Columns_mask(mask_configVO
					.getColumn_mask_fk()));

		} else {
			mask_config.setFk_mask_config2(null);
		}

		if (mask_configVO.getMask_profile_fk() != null
				&& !mask_configVO.getMask_profile_fk().equals("")) {
			mask_config.setFk_mask_config3(new Mask_profile(mask_configVO
					.getMask_profile_fk()));

		} else {
			mask_config.setFk_mask_config3(null);
		}

		if (mask_configVO.getName() != null) {
			mask_config.setName(mask_configVO.getName());
		}

		if (mask_configVO.getWording() != null) {
			mask_config.setWording(mask_configVO.getWording());
		}

		if (mask_configVO.getMask_flag() != null) {
			mask_config.setMask_flag(mask_configVO.getMask_flag());
		}

		if (mask_configVO.getOwner() != null) {
			mask_config.setOwner(mask_configVO.getOwner());
		}

		Mask_config mask_config1 = this.getMask_configRepository().save(
				mask_config);

		return "0000";

	}

	/**
	 * delete a Mask_config entity .
	 * 
	 * @param Mask_configVO
	 *            ValueObject.
	 * 
	 * @return 0000 - if Success
	 * 
	 * @throws OurException
	 *             No.0001 If the object don't exists.
	 * 
	 */
	public String deleteMask_configService(ServiceContext ctx,
			Mask_configVO mask_configVO) throws Exception {
		ServiceContextStore.set(ctx);

		Mask_config mask_config = null;

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(Mask_configProperties.mask_id(),
				mask_configVO.getMask_id()));

		List<Mask_config> list = this.getMask_configRepository()
				.findByCondition(con);

		if (list.size() > 0) {
			mask_config = list.get(0);
		} else {
			throw new OurException("0001", new Mask_configNotFoundException(""));
		}

		if (mask_configVO.getTable_mask_fk() != null
				&& !mask_configVO.getTable_mask_fk().equals("")) {
			mask_config.setFk_mask_config1(new Tables_mask(mask_configVO
					.getTable_mask_fk()));

		} else {
			mask_config.setFk_mask_config1(null);
		}

		if (mask_configVO.getColumn_mask_fk() != null
				&& !mask_configVO.getColumn_mask_fk().equals("")) {
			mask_config.setFk_mask_config2(new Columns_mask(mask_configVO
					.getColumn_mask_fk()));

		} else {
			mask_config.setFk_mask_config2(null);
		}

		if (mask_configVO.getMask_profile_fk() != null
				&& !mask_configVO.getMask_profile_fk().equals("")) {
			mask_config.setFk_mask_config3(new Mask_profile(mask_configVO
					.getMask_profile_fk()));

		} else {
			mask_config.setFk_mask_config3(null);
		}

		this.getMask_configRepository().delete(mask_config);

		return "0000";

	}

	/**
	 * Find all entities of a specific type .
	 * 
	 * @return List of Mask_configVO
	 * 
	 */
	public List<Mask_configVO> getAllMask_configService(ServiceContext ctx)
			throws Exception {
		List<Mask_configVO> l = new ArrayList<Mask_configVO>();

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		if (ctx != null) {
			Mask_configUtils.dataFilter(ctx, con);
		}

		List<Mask_config> l_entity = new ArrayList<Mask_config>();

		l_entity = this.getMask_configRepository().findByCondition(con);

		l = Mask_configUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

		return l;

	}

	/**
	 * Find entities by conditions
	 * 
	 * @return List of Mask_configVO
	 * 
	 */
	public List<Mask_configVO> searchMask_configService(ServiceContext ctx,
			Mask_configVO mask_configVO) throws Exception {
		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		if (ctx != null) {
			Mask_configUtils.dataFilter(ctx, con);
		}

		Mask_configUtils.setListOfCriteria(ctx, con, mask_configVO);

		List<Mask_config> l_entity = new ArrayList<Mask_config>();
		List<Mask_configVO> l = new ArrayList<Mask_configVO>();

		l_entity = this.getMask_configRepository().findByCondition(con);

		l = Mask_configUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

		return l;

	}

	public String applyMask(ServiceContext ctx, String table,
			String columnName, String columnValue) throws Exception {
		
		String enable_default_mask = ctx.getProperty("enable_default_mask") != null ? ctx.getProperty("enable_default_mask").toString() : "N";
		
		char[] maskedString = columnValue.toCharArray();
		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();
		Collection<String> l = new ArrayList<String>();
		l.add(ctx.getUserId());
		l.add(ctx.getProfileCode());

		if (enable_default_mask.equals("Y")) {
			con.add(ConditionalCriteria.equal(Mask_configProperties.name(), "DEFAULT_MASK"));
			
			List<Mask_config> list = this.getMask_configRepository()
					.findByCondition(con);

			for (Mask_config mask_config : list) {
				maskedString = applyMaskToString(columnValue, mask_config
						.getFk_mask_config3().getMask_chains());
			}
		}else {
			
		con.add(ConditionalCriteria.equal(Mask_configProperties
				.fk_mask_config1().table_name(), table));
		con.add(ConditionalCriteria.equal(Mask_configProperties
				.fk_mask_config2().column_name(), columnName));
		con.add(ConditionalCriteria.in(Mask_configProperties.owner(), l));

		List<Mask_config> list = this.getMask_configRepository()
				.findByCondition(con);
		
		for (Mask_config mask_config : list) {
			if (mask_config.getMask_flag().equals("U")
					&& mask_config.getOwner().equals(ctx.getUserId())) {
				maskedString = applyMaskToString(columnValue, mask_config
						.getFk_mask_config3().getMask_chains());
				break;
			}
			if (mask_config.getMask_flag().equals("P")
					&& mask_config.getOwner().equals(ctx.getProfileCode())) {
				maskedString = applyMaskToString(columnValue, mask_config
						.getFk_mask_config3().getMask_chains());
			}
		}
		}
		String masked = new String (maskedString);
		java.util.Arrays.fill(maskedString, '*');
		return masked;
	}

	private char[] applyMaskToString(String columnValue,
			Set<Mask_chain> listMaskChains) throws Exception {

		StringBuffer buffStr = new StringBuffer(columnValue);
		StringBuffer maskedBuffStr = new StringBuffer(columnValue);
		
		for (int i=0 ; i< maskedBuffStr.length(); i++){
			maskedBuffStr.setCharAt(i, '*');
		}

		for (Mask_chain mask_chain : listMaskChains) {
			if (mask_chain.getPadding().equals("L")) {
				for (int i = mask_chain.getStart_index(); i < mask_chain
						.getAsterisk_number()+mask_chain.getStart_index()
						&& i < maskedBuffStr.length(); i++) {
					maskedBuffStr.setCharAt(i, buffStr.charAt(i));
				}
			}
			if (mask_chain.getPadding().equals("R")) {
				for (int i = maskedBuffStr.length()- mask_chain.getStart_index()- 1; i >= (maskedBuffStr
						.length() - mask_chain.getAsterisk_number()-mask_chain.getStart_index())
						&& i >= 0; i--) {
					maskedBuffStr.setCharAt(i, buffStr.charAt(i));
				}
			}
		}

		return maskedBuffStr.toString().toCharArray();

	}
	
	@Override
	public String updateDefault_maskService(ServiceContext ctx, Mask_configSpecificVO mask_configSpecificVO)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
