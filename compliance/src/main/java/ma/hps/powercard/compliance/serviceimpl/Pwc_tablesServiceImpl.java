package ma.hps.powercard.compliance.serviceimpl;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ma.hps.powercard.compliance.domain.Pwc_tables;
import ma.hps.powercard.compliance.domain.Pwc_tablesProperties;
import ma.hps.powercard.compliance.serviceapi.Data_columns_filterVO;
import ma.hps.powercard.compliance.serviceapi.Data_columns_filter_valuesVO;
import ma.hps.powercard.compliance.serviceapi.Pwc_tablesVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of Pwc_tablesService.
 */
@Lazy
@Service("pwc_tablesService")
public class Pwc_tablesServiceImpl extends Pwc_tablesServiceImplBase {
    public Pwc_tablesServiceImpl() {
    }

    public List<Pwc_tablesVO> getAllPwc_tablesService(ServiceContext ctx)
        throws Exception {
        List<Pwc_tablesVO> l = new ArrayList<Pwc_tablesVO>();

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        List<Pwc_tables> l_entity =
            this.getPwc_tablesRepository().findByCondition(con);

        for (int i = 0; i < l_entity.size(); i++) {
            l.add(entityToVO(ctx, l_entity.get(i), 0 ,0));

        }

        return l;

    }

    public List<Pwc_tablesVO> searchPwc_tablesService(ServiceContext ctx,
        Pwc_tablesVO pwc_tablesVO) throws Exception {
        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (pwc_tablesVO.getPwc_tables_id() != null) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Pwc_tablesProperties.pwc_tables_id(),
                    pwc_tablesVO.getPwc_tables_id()));
        }

        if (pwc_tablesVO.getTable_name() != null &&
              !pwc_tablesVO.getTable_name().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Pwc_tablesProperties.table_name(),
                    pwc_tablesVO.getTable_name()));
        }

        if (pwc_tablesVO.getWording() != null &&
              !pwc_tablesVO.getWording().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(Pwc_tablesProperties.wording(),
                    pwc_tablesVO.getWording()));
        }

        if (pwc_tablesVO.getEntity_name() != null &&
              !pwc_tablesVO.getEntity_name().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Pwc_tablesProperties.entity_name(),
                    pwc_tablesVO.getEntity_name()));
        }

        if (pwc_tablesVO.getApply_filter() != null &&
              !pwc_tablesVO.getApply_filter().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Pwc_tablesProperties.apply_filter(),
                    pwc_tablesVO.getApply_filter()));
        }

        List<Pwc_tables> l_entity =
            this.getPwc_tablesRepository().findByCondition(con);

        List<Pwc_tablesVO> l = new ArrayList<Pwc_tablesVO>();

        for (int i = 0; i < l_entity.size(); i++) {
            Pwc_tablesVO pwc_tablesVoTmp = entityToVO(ctx, l_entity.get(i), 0,0);

            l.add(pwc_tablesVoTmp);

        }

        return l;

    }

    
//    public List<String> findbyProjection(ServiceContext ctx,
//        String entity_name, String column) throws Exception {
//
//        return this.getPwc_tablesRepository().findbyProjection(entity_name, column);
//
//    }

	public Pwc_tables VoToEntity(ServiceContext ctx, Pwc_tablesVO pwcTablesVO)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Pwc_tablesVO entityToVO(ServiceContext ctx, Pwc_tables pwc_tables,
			int lazyLevel, int lazyLevelCol) throws Exception {
		
        Pwc_tablesVO e = new Pwc_tablesVO();

        e.setPwc_tables_id(pwc_tables.getPwc_tables_id());

        e.setTable_name(pwc_tables.getTable_name());

        e.setWording(pwc_tables.getWording());

        e.setEntity_name(pwc_tables.getEntity_name());

        e.setApply_filter(pwc_tables.getApply_filter());

        return e;
	}
}
