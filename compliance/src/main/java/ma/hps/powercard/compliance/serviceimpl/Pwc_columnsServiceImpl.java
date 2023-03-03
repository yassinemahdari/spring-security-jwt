package ma.hps.powercard.compliance.serviceimpl;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;

import java.util.ArrayList;
import java.util.List;

import ma.hps.powercard.compliance.domain.Pwc_columns;
import ma.hps.powercard.compliance.domain.Pwc_columnsProperties;
import ma.hps.powercard.compliance.serviceapi.Pwc_columnsVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of Pwc_columnsService.
 */
@Lazy
@Service("pwc_columnsService")
public class Pwc_columnsServiceImpl extends Pwc_columnsServiceImplBase {
    public Pwc_columnsServiceImpl() {
    }

    public List<Pwc_columnsVO> getAllPwc_columnsService(ServiceContext ctx)
        throws Exception {
        List<Pwc_columnsVO> l = new ArrayList<Pwc_columnsVO>();

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        List<Pwc_columns> l_entity =
            this.getPwc_columnsRepository().findByCondition(con);

        for (int i = 0; i < l_entity.size(); i++) {
            l.add(entityToVO(ctx, l_entity.get(i), 0,0));

        }

        return l;

    }

    public List<Pwc_columnsVO> searchPwc_columnsService(ServiceContext ctx,
        Pwc_columnsVO pwc_columnsVO) throws Exception {
        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (pwc_columnsVO.getPwc_columns_id() != null) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Pwc_columnsProperties.pwc_columns_id(),
                    pwc_columnsVO.getPwc_columns_id()));
        }

        if (pwc_columnsVO.getColumn_name() != null &&
              !pwc_columnsVO.getColumn_name().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Pwc_columnsProperties.column_name(),
                    pwc_columnsVO.getColumn_name()));
        }

        if (pwc_columnsVO.getWording() != null &&
              !pwc_columnsVO.getWording().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(Pwc_columnsProperties.wording(),
                    pwc_columnsVO.getWording()));
        }

        if (pwc_columnsVO.getColumn_type() != null &&
              !pwc_columnsVO.getColumn_type().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Pwc_columnsProperties.column_type(),
                    pwc_columnsVO.getColumn_type()));
        }

        if (pwc_columnsVO.getNaturalKey() != null &&
              !pwc_columnsVO.getNaturalKey().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Pwc_columnsProperties.naturalKey(),
                    pwc_columnsVO.getNaturalKey()));
        }

        if (pwc_columnsVO.getPwc_tables_fk() != null) {
            con.add(ConditionalCriteria.likeOrEqual(Pwc_columnsProperties.table_id()
                                                                  .pwc_tables_id(),
                    pwc_columnsVO.getPwc_tables_fk()));
        }

        List<Pwc_columns> l_entity =
            this.getPwc_columnsRepository().findByCondition(con);

        List<Pwc_columnsVO> l = new ArrayList<Pwc_columnsVO>();

        for (int i = 0; i < l_entity.size(); i++) {
            Pwc_columnsVO pwc_columnsVoTmp =
                entityToVO(ctx, l_entity.get(i), 0,0);

            l.add(pwc_columnsVoTmp);

        }

        return l;

    }

	public Pwc_columns VoToEntity(ServiceContext ctx, Pwc_columnsVO pwcColumnsVO)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Pwc_columnsVO entityToVO(ServiceContext ctx, Pwc_columns pwc_columns,
			int lazyLevel, int lazyLevelCol) throws Exception {
		// TODO Auto-generated method stub
        
		Pwc_columnsVO e = new Pwc_columnsVO();
		
		e.setPwc_columns_id(pwc_columns.getPwc_columns_id());

        e.setColumn_name(pwc_columns.getColumn_name());

        e.setWording(pwc_columns.getWording());

        e.setColumn_type(pwc_columns.getColumn_type());

        e.setNaturalKey(pwc_columns.getNaturalKey());

        if (pwc_columns.getTable_id() == null) {
            e.setPwc_tables_fk(0L);
        }
        else {
            e.setPwc_tables_fk(pwc_columns.getTable_id().getPwc_tables_id());
        }

        return e;
	}
}
