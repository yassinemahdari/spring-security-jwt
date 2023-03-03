package ma.hps.powercard.compliance.serviceimpl;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;

import java.util.ArrayList;
import java.util.List;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.Notification_hist;
import ma.hps.powercard.compliance.domain.Notification_histProperties;
import ma.hps.powercard.compliance.exception.Notification_histNotFoundException;
import ma.hps.powercard.compliance.serviceapi.Notification_histVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of Notification_histService.
 */
@Lazy
@Service("notification_histService")
public class Notification_histServiceImpl
    extends Notification_histServiceImplBase {
    public Notification_histServiceImpl() {
    }

    public String createNotification_histService(ServiceContext ctx,
        Notification_histVO notification_histVO) throws Exception {
        ServiceContextStore.set(ctx);

        Notification_hist notification_hist = new Notification_hist();

        notification_hist.setNotification_code(notification_histVO.getNotification_code());

        notification_hist.setName(notification_histVO.getName());

        notification_hist.setWording(notification_histVO.getWording());

        notification_hist.setNotification_subject(notification_histVO.getNotification_subject());

        notification_hist.setContent(notification_histVO.getContent());

        notification_hist.setTrigger_name(notification_histVO.getTrigger_name());

        notification_hist.setNotification_type(notification_histVO.getNotification_type());

        Notification_hist notification_hist1 =
            this.getNotification_histRepository().save(notification_hist);

        return "0000";
    }

    public String updateNotification_histService(ServiceContext ctx,
        Notification_histVO notification_histVO) throws Exception {
        ServiceContextStore.set(ctx);

        Notification_hist notification_hist = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(
                Notification_histProperties.notification_hist_id(),
                notification_histVO.getNotification_hist_id()));

        List<Notification_hist> list =
            this.getNotification_histRepository().findByCondition(con);

        if (list.size() > 0) {
            notification_hist = list.get(0);
        } else {
            throw new OurException("0001",
                new Notification_histNotFoundException(""));
        }

        if (notification_histVO.getNotification_code() != null &&
              !notification_histVO.getNotification_code().equals("")) {
            notification_hist.setNotification_code(notification_histVO.getNotification_code());
        }

        if (notification_histVO.getName() != null &&
              !notification_histVO.getName().equals("")) {
            notification_hist.setName(notification_histVO.getName());
        }

        if (notification_histVO.getWording() != null &&
              !notification_histVO.getWording().equals("")) {
            notification_hist.setWording(notification_histVO.getWording());
        }

        if (notification_histVO.getNotification_subject() != null &&
              !notification_histVO.getNotification_subject().equals("")) {
            notification_hist.setNotification_subject(notification_histVO.getNotification_subject());
        }

        if (notification_histVO.getContent() != null &&
              !notification_histVO.getContent().equals("")) {
            notification_hist.setContent(notification_histVO.getContent());
        }

        if (notification_histVO.getTrigger_name() != null &&
              !notification_histVO.getTrigger_name().equals("")) {
            notification_hist.setTrigger_name(notification_histVO.getTrigger_name());
        }

        if (notification_histVO.getNotification_type() != null &&
              !notification_histVO.getNotification_type().equals("")) {
            notification_hist.setNotification_type(notification_histVO.getNotification_type());
        }

        Notification_hist notification_hist1 =
            this.getNotification_histRepository().save(notification_hist);

        return "0000";
    }

    public String deleteNotification_histService(ServiceContext ctx,
        Notification_histVO notification_histVO) throws Exception {
        ServiceContextStore.set(ctx);

        Notification_hist notification_hist = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(
                Notification_histProperties.notification_hist_id(),
                notification_histVO.getNotification_hist_id()));

        List<Notification_hist> list =
            this.getNotification_histRepository().findByCondition(con);

        if (list.size() > 0) {
            notification_hist = list.get(0);
        } else {
            throw new OurException("0001",
                new Notification_histNotFoundException(""));
        }

        notification_hist.setNotification_code(notification_histVO.getNotification_code());

        notification_hist.setName(notification_histVO.getName());

        notification_hist.setWording(notification_histVO.getWording());

        notification_hist.setNotification_subject(notification_histVO.getNotification_subject());

        notification_hist.setContent(notification_histVO.getContent());

        notification_hist.setTrigger_name(notification_histVO.getTrigger_name());

        notification_hist.setNotification_type(notification_histVO.getNotification_type());

        this.getNotification_histRepository().delete(notification_hist);

        return "0000";
    }

    public List<Notification_histVO> getAllNotification_histService(
        ServiceContext ctx) throws Exception {
        List<Notification_histVO> l = new ArrayList<Notification_histVO>();

        List<Notification_hist> l_entity =
            this.getNotification_histRepository().findAll();

        for (int i = 0; i < l_entity.size(); i++) {
            l.add(entityToVO(ctx, l_entity.get(i), 0));

        }

        return l;

    }

    public List<Notification_histVO> searchNotification_histService(
        ServiceContext ctx, Notification_histVO notification_histVO)
        throws Exception {
        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (notification_histVO.getNotification_hist_id() != null) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Notification_histProperties.notification_hist_id(),
                    notification_histVO.getNotification_hist_id()));
        }

        if (notification_histVO.getNotification_code() != null &&
              !notification_histVO.getNotification_code().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Notification_histProperties.notification_code(),
                    notification_histVO.getNotification_code()));
        }

        if (notification_histVO.getName() != null &&
              !notification_histVO.getName().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Notification_histProperties.name(),
                    notification_histVO.getName()));
        }

        if (notification_histVO.getWording() != null &&
              !notification_histVO.getWording().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Notification_histProperties.wording(),
                    notification_histVO.getWording()));
        }

        if (notification_histVO.getNotification_subject() != null &&
              !notification_histVO.getNotification_subject().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Notification_histProperties.notification_subject(),
                    notification_histVO.getNotification_subject()));
        }

        if (notification_histVO.getContent() != null &&
              !notification_histVO.getContent().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Notification_histProperties.content(),
                    notification_histVO.getContent()));
        }

        if (notification_histVO.getTrigger_name() != null &&
              !notification_histVO.getTrigger_name().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Notification_histProperties.trigger_name(),
                    notification_histVO.getTrigger_name()));
        }

        if (notification_histVO.getNotification_type() != null &&
              !notification_histVO.getNotification_type().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Notification_histProperties.notification_type(),
                    notification_histVO.getNotification_type()));
        }

        List<Notification_hist> l_entity =
            this.getNotification_histRepository().findByCondition(con);

        List<Notification_histVO> l = new ArrayList<Notification_histVO>();

        for (int i = 0; i < l_entity.size(); i++) {
            Notification_histVO notification_histVoTmp =
                entityToVO(ctx, l_entity.get(i), 0);

            l.add(notification_histVoTmp);

        }

        return l;

    }

    public Notification_histVO entityToVO(ServiceContext ctx,
        Notification_hist notification_hist, int lazy_level) throws Exception {
        Notification_histVO e = new Notification_histVO();

        e.setNotification_hist_id(notification_hist.getNotification_hist_id());

        e.setNotification_code(notification_hist.getNotification_code());

        e.setName(notification_hist.getName());

        e.setWording(notification_hist.getWording());

        e.setNotification_subject(notification_hist.getNotification_subject());

        e.setContent(notification_hist.getContent());

        e.setTrigger_name(notification_hist.getTrigger_name());

        e.setNotification_type(notification_hist.getNotification_type());

        return e;
    }

	public Notification_hist VoToEntity(ServiceContext ctx,
			Notification_histVO notificationHistVO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Notification_histVO entityToVO(ServiceContext ctx,
			Notification_hist notificationHist, int lazyLevel, int lazyLevelCol)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
