package ma.hps.powercard.compliance.serviceimpl;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;

import java.util.ArrayList;
import java.util.List;

import ma.hps.powercard.compliance.domain.Reason_request;
import ma.hps.powercard.compliance.domain.Reason_requestProperties;
import ma.hps.powercard.compliance.serviceapi.Reason_requestVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of Reason_requestService.
 */
@Lazy
@Service("reason_requestService")
public class Reason_requestServiceImpl extends Reason_requestServiceImplBase {
    public Reason_requestServiceImpl() {
    }

    public List<Reason_requestVO> getAllReason_requestService(
            ServiceContext ctx) throws Exception {
            List<Reason_requestVO> l = new ArrayList<Reason_requestVO>();
    		
    		 List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

            List<Reason_request> l_entity =
                this.getReason_requestRepository().findByCondition(con);

            for (int i = 0; i < l_entity.size(); i++) {
                l.add(entityToVO(ctx, l_entity.get(i), 0));

            }

            return l;

        }

    public List<Reason_requestVO> searchReason_requestService(
        ServiceContext ctx, Reason_requestVO reason_requesVO) throws Exception {
        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (reason_requesVO.getReason_request_id() != null) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Reason_requestProperties.reason_request_id(),
                    reason_requesVO.getReason_request_id()));
        }

        if (reason_requesVO.getResaon_code() != null &&
              !reason_requesVO.getResaon_code().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Reason_requestProperties.resaon_code(),
                    reason_requesVO.getResaon_code()));
        }

        if (reason_requesVO.getWording() != null &&
              !reason_requesVO.getWording().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Reason_requestProperties.wording(),
                    reason_requesVO.getWording()));
        }

        if (reason_requesVO.getName() != null &&
              !reason_requesVO.getName().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(Reason_requestProperties.name(),
                    reason_requesVO.getName()));
        }

        List<Reason_request> l_entity =
            this.getReason_requestRepository().findByCondition(con);

        List<Reason_requestVO> l = new ArrayList<Reason_requestVO>();

        for (int i = 0; i < l_entity.size(); i++) {
            Reason_requestVO reason_requestVoTmp =
                entityToVO(ctx, l_entity.get(i), 0);

            l.add(reason_requestVoTmp);

        }

        return l;

    }

    public Reason_requestVO entityToVO(ServiceContext ctx,
        Reason_request reason_request, int lazy_level) throws Exception {
        Reason_requestVO e = new Reason_requestVO();

        e.setReason_request_id(reason_request.getReason_request_id());

        e.setResaon_code(reason_request.getResaon_code());

        e.setWording(reason_request.getWording());

        e.setName(reason_request.getName());

        return e;
    }

	public Reason_request VoToEntity(ServiceContext ctx,
			Reason_requestVO reasonRequestVO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Reason_requestVO entityToVO(ServiceContext ctx,
			Reason_request reasonRequest, int lazyLevel, int lazyLevelCol)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
