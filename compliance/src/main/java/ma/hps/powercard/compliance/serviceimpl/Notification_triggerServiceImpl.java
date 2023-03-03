package ma.hps.powercard.compliance.serviceimpl;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;

import java.util.ArrayList;
import java.util.List;

import ma.hps.powercard.compliance.domain.Notification_trigger;
import ma.hps.powercard.compliance.domain.Notification_triggerProperties;
import ma.hps.powercard.compliance.serviceapi.Notification_triggerVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of Notification_triggerService.
 */
@Lazy
@Service("notification_triggerService")
public class Notification_triggerServiceImpl
    extends Notification_triggerServiceImplBase {
    public Notification_triggerServiceImpl() {
    }

    public List<Notification_triggerVO> getAllNotification_triggerService(
        ServiceContext ctx) throws Exception {
        List<Notification_triggerVO> l =
            new ArrayList<Notification_triggerVO>();

        List<Notification_trigger> l_entity =
            this.getNotification_triggerRepository().findAll();

        for (int i = 0; i < l_entity.size(); i++) {
            l.add(entityToVO(ctx, l_entity.get(i), 0));

        }

        return l;

    }

    public List<Notification_triggerVO> searchNotification_triggerService(
        ServiceContext ctx, Notification_triggerVO notification_triggerVO)
        throws Exception {
        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (notification_triggerVO.getNotification_trigger_id() != null) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Notification_triggerProperties.notification_trigger_id(),
                    notification_triggerVO.getNotification_trigger_id()));
        }

        if (notification_triggerVO.getName() != null &&
              !notification_triggerVO.getName().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Notification_triggerProperties.name(),
                    notification_triggerVO.getName()));
        }

        if (notification_triggerVO.getWording() != null &&
              !notification_triggerVO.getWording().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Notification_triggerProperties.wording(),
                    notification_triggerVO.getWording()));
        }

        if (notification_triggerVO.getTrigger_code() != null &&
              !notification_triggerVO.getTrigger_code().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(
                    Notification_triggerProperties.trigger_code(),
                    notification_triggerVO.getTrigger_code()));
        }

        List<Notification_trigger> l_entity =
            this.getNotification_triggerRepository().findByCondition(con);

        List<Notification_triggerVO> l =
            new ArrayList<Notification_triggerVO>();

        for (int i = 0; i < l_entity.size(); i++) {
            Notification_triggerVO notification_triggerVoTmp =
                entityToVO(ctx, l_entity.get(i), 0);

            l.add(notification_triggerVoTmp);

        }

        return l;

    }

    public Notification_triggerVO entityToVO(ServiceContext ctx,
        Notification_trigger notification_trigger, int lazy_level)
        throws Exception {
        Notification_triggerVO e = new Notification_triggerVO();

        e.setNotification_trigger_id(notification_trigger.getNotification_trigger_id());

        e.setName(notification_trigger.getName());

        e.setWording(notification_trigger.getWording());

        e.setTrigger_code(notification_trigger.getTrigger_code());

        return e;
    }

	public Notification_trigger VoToEntity(ServiceContext ctx,
			Notification_triggerVO notificationTriggerVO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Notification_triggerVO entityToVO(ServiceContext ctx,
			Notification_trigger notificationTrigger, int lazyLevel,
			int lazyLevelCol) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
