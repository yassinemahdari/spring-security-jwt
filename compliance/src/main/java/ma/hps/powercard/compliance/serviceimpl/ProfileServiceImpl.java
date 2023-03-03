package ma.hps.powercard.compliance.serviceimpl;

import org.apache.log4j.Logger;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.domain.PagedResult;
import org.fornax.cartridges.sculptor.framework.domain.PagingParameter;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;

import java.util.ArrayList;
import java.util.List; 

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.Bank;
import ma.hps.powercard.compliance.domain.Data_access;
import ma.hps.powercard.compliance.domain.Grants2profiles;
import ma.hps.powercard.compliance.domain.Password;
import ma.hps.powercard.compliance.domain.Profile;
import ma.hps.powercard.compliance.domain.ProfileProperties;
import ma.hps.powercard.compliance.domain.Role;
import ma.hps.powercard.compliance.exception.ProfileNotFoundException;
import ma.hps.powercard.compliance.repositoryimpl.Grants2profilesUtils;
import ma.hps.powercard.compliance.repositoryimpl.ProfileUtils;
import ma.hps.powercard.compliance.repositoryimpl.RoleUtils;
import ma.hps.powercard.compliance.serviceapi.Grants2profilesVO;
import ma.hps.powercard.compliance.serviceapi.ProfileVO;
import ma.hps.powercard.compliance.serviceapi.RoleVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of ProfileService.
 */
@Lazy
@Service("profileService")
public class ProfileServiceImpl extends ProfileServiceImplBase {
    private static Logger logger = Logger.getLogger(ProfileServiceImpl.class);

    public ProfileServiceImpl() {
    }

    /**
    * Persist a Profile entity .
    *
    * @param ProfileVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0024 If ever the object already exists.
    *
    */
    public String createProfileService(ServiceContext ctx, ProfileVO profileVO)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:createProfileService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Profile profile = null;
        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(ProfileProperties.profile_id(),
                profileVO.getProfile_id()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Profile> pagedResult =
            this.getProfileRepository().findByCondition(con, pagingParameter);
        List<Profile> list = pagedResult.getValues();

        if (list.size() > 0) {
            throw new OurException("0024", new Exception(""));
        }
        else {
            profile = new Profile(profileVO.getProfile_id());
        }

        if (profileVO.getInstitution_fk() != null &&
              !profileVO.getInstitution_fk().equals("")) {
        	
        	List<String> banks = (List<String>) ctx.getProperty("bankDataAccess");
        	
        	if (banks != null && banks.size() > 0 && !banks.contains(profileVO.getInstitution_fk())) {

        		throw new OurException("0403", new Exception("Bank data access restriction"));

        	} else {
        		profile.setBank_code(new Bank(profileVO.getInstitution_fk()));
        	}

        } else {
            if ("".equals(profileVO.getInstitution_fk())) {
                profile.setBank_code(null);
            }
        }

        if (profileVO.getData_access_fk() != null) {
            profile.setData_access_id(new Data_access(
                    profileVO.getData_access_fk()));

        } else {
        }

        if (profileVO.getPassword_complexity_fk() != null &&
              !profileVO.getPassword_complexity_fk().equals("")) {
            profile.setPassword_complexity_id(new Password(
                    profileVO.getPassword_complexity_fk()));

        } else {
            if ("".equals(profileVO.getPassword_complexity_fk())) {
                profile.setPassword_complexity_id(null);
            }
        }

        profile.setName(profileVO.getName());

        profile.setStatus(profileVO.getStatus());

        profile.setWording(profileVO.getWording());

        profile.setProfile_code(profileVO.getProfile_code());

        profile.setAdmin(profileVO.getAdmin());

        profile.setTimer_pwc_disconnection(profileVO.getTimer_pwc_disconnection());

        profile.setPwc_disconnection(profileVO.getPwc_disconnection());

        profile.setTimer_browser_disconnection(profileVO.getTimer_browser_disconnection());

        profile.setDis_notification_type(profileVO.getDis_notification_type());

        profile.setBrowser_disconnection(profileVO.getBrowser_disconnection());

        profile.setEmail(profileVO.getEmail());

        profile.setBank_data_access(profileVO.getBank_data_access());

        profile.setPan_visualization(profileVO.getPan_visualization());

        if (profileVO.getDelegatedRoles() != null) {
            for (RoleVO roleVO : profileVO.getDelegatedRoles()) {
                Role role = RoleUtils.VoToEntity(ctx, roleVO);
                profile.addRoles1(role);
            }
        }

        if (profileVO.getAssignedRoles() != null) {
            for (RoleVO roleVO : profileVO.getAssignedRoles()) {
                Role role = RoleUtils.VoToEntity(ctx, roleVO);
                profile.addRoles2(role);
            }
        }

        if (profileVO.getRestServices() != null) {
            for (Grants2profilesVO grants2profilesVO : profileVO.getRestServices()) {
                Grants2profiles grants2profiles =
                    Grants2profilesUtils.VoToEntity(ctx, grants2profilesVO);
                profile.addRestricted_service(grants2profiles);
            }
        }

        Profile profile1 = this.getProfileRepository().save(profile);

        return "0000";

    }

    /**
    * update a Profile entity .
    *
    * @param ProfileVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String updateProfileService(ServiceContext ctx, ProfileVO profileVO)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:updateProfileService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Profile profile = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(ProfileProperties.profile_id(),
                profileVO.getProfile_id()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Profile> pagedResult =
            this.getProfileRepository().findByCondition(con, pagingParameter);
        List<Profile> list = pagedResult.getValues();

        if (list.size() > 0) {
            profile = list.get(0);
        } else {
            throw new OurException("0001", new ProfileNotFoundException(""));
        }

        profile = new Profile(profileVO.getProfile_id());

        if (profileVO.getInstitution_fk() != null &&
              !profileVO.getInstitution_fk().equals("")) {
        	
        	List<String> banks = (List<String>) ctx.getProperty("bankDataAccess");

        	if (banks != null && banks.size() > 0 && !banks.contains(profileVO.getInstitution_fk())) {

        		throw new OurException("0403", new Exception("Bank data access restriction"));

        	} else {
        		profile.setBank_code(new Bank(profileVO.getInstitution_fk()));
        	}

        } else {
            if ("".equals(profileVO.getInstitution_fk())) {
                profile.setBank_code(null);
            }
        }

        if (profileVO.getData_access_fk() != null) {
            profile.setData_access_id(new Data_access(
                    profileVO.getData_access_fk()));

        } else {
        }

        if (profileVO.getPassword_complexity_fk() != null &&
              !profileVO.getPassword_complexity_fk().equals("")) {
            profile.setPassword_complexity_id(new Password(
                    profileVO.getPassword_complexity_fk()));

        } else {
            if ("".equals(profileVO.getPassword_complexity_fk())) {
                profile.setPassword_complexity_id(null);
            }
        }

        if (profileVO.getName() != null) {
            profile.setName(profileVO.getName());
        }

        if (profileVO.getStatus() != null) {
            profile.setStatus(profileVO.getStatus());
        }

        if (profileVO.getWording() != null) {
            profile.setWording(profileVO.getWording());
        }

        if (profileVO.getProfile_code() != null) {
            profile.setProfile_code(profileVO.getProfile_code());
        }

        if (profileVO.getAdmin() != null) {
            profile.setAdmin(profileVO.getAdmin());
        }

        if (profileVO.getTimer_pwc_disconnection() != null) {
            profile.setTimer_pwc_disconnection(profileVO.getTimer_pwc_disconnection());
        }

        if (profileVO.getPwc_disconnection() != null) {
            profile.setPwc_disconnection(profileVO.getPwc_disconnection());
        }

        if (profileVO.getTimer_browser_disconnection() != null) {
            profile.setTimer_browser_disconnection(profileVO.getTimer_browser_disconnection());
        }

        if (profileVO.getDis_notification_type() != null) {
            profile.setDis_notification_type(profileVO.getDis_notification_type());
        }

        if (profileVO.getBrowser_disconnection() != null) {
            profile.setBrowser_disconnection(profileVO.getBrowser_disconnection());
        }

        if (profileVO.getEmail() != null) {
            profile.setEmail(profileVO.getEmail());
        }

        if (profileVO.getBank_data_access() != null) {
            profile.setBank_data_access(profileVO.getBank_data_access());
        }

        if (profileVO.getPan_visualization() != null) {
            profile.setPan_visualization(profileVO.getPan_visualization());
        }

        if (profileVO.getDelegatedRoles() != null) {
            for (RoleVO roleVO : profileVO.getDelegatedRoles()) {
                Role role = RoleUtils.VoToEntity(ctx, roleVO);
                profile.addRoles1(role);
            }
        }

        if (profileVO.getAssignedRoles() != null) {
            for (RoleVO roleVO : profileVO.getAssignedRoles()) {
                Role role = RoleUtils.VoToEntity(ctx, roleVO);
                profile.addRoles2(role);
            }
        }

        if (profileVO.getRestServices() != null) {
            for (Grants2profilesVO grants2profilesVO : profileVO.getRestServices()) {
                Grants2profiles grants2profiles =
                    Grants2profilesUtils.VoToEntity(ctx, grants2profilesVO);
                profile.addRestricted_service(grants2profiles);
            }
        }

        Profile profile1 = this.getProfileRepository().save(profile);

        return "0000";

    }

    /**
    * delete a Profile entity .
    *
    * @param ProfileVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String deleteProfileService(ServiceContext ctx, ProfileVO profileVO)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:deleteProfileService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Profile profile = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(ProfileProperties.profile_id(),
                profileVO.getProfile_id()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Profile> pagedResult =
            this.getProfileRepository().findByCondition(con, pagingParameter);
        List<Profile> list = pagedResult.getValues();

        if (list.size() > 0) {
            profile = list.get(0);
        } else {
            throw new OurException("0001", new ProfileNotFoundException(""));
        }

        if (profileVO.getInstitution_fk() != null &&
              !profileVO.getInstitution_fk().equals("")) {
            profile.setBank_code(new Bank(profileVO.getInstitution_fk()));

        } else {
            if ("".equals(profileVO.getInstitution_fk())) {
                profile.setBank_code(null);
            }
        }

        if (profileVO.getData_access_fk() != null) {
            profile.setData_access_id(new Data_access(
                    profileVO.getData_access_fk()));

        } else {
        }

        if (profileVO.getPassword_complexity_fk() != null &&
              !profileVO.getPassword_complexity_fk().equals("")) {
            profile.setPassword_complexity_id(new Password(
                    profileVO.getPassword_complexity_fk()));

        } else {
            if ("".equals(profileVO.getPassword_complexity_fk())) {
                profile.setPassword_complexity_id(null);
            }
        }

        if (profileVO.getDelegatedRoles() != null) {
            for (RoleVO roleVO : profileVO.getDelegatedRoles()) {
                Role role = RoleUtils.VoToEntity(ctx, roleVO);
                profile.addRoles1(role);
            }
        }

        if (profileVO.getAssignedRoles() != null) {
            for (RoleVO roleVO : profileVO.getAssignedRoles()) {
                Role role = RoleUtils.VoToEntity(ctx, roleVO);
                profile.addRoles2(role);
            }
        }

        if (profileVO.getRestServices() != null) {
            for (Grants2profilesVO grants2profilesVO : profileVO.getRestServices()) {
                Grants2profiles grants2profiles =
                    Grants2profilesUtils.VoToEntity(ctx, grants2profilesVO);
                profile.addRestricted_service(grants2profiles);
            }
        }

        this.getProfileRepository().delete(profile);

        return "0000";

    }

    /**
    *  Find all entities of a specific type .
    *
    * @return List of ProfileVO
    *
    */
    public List<ProfileVO> getAllProfileService(ServiceContext ctx)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:getAllProfileService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        List<ProfileVO> l = new ArrayList<ProfileVO>();

        List<Profile> l_entity = new ArrayList<Profile>();

        l_entity = this.getProfileRepository().findAll();

        l = ProfileUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

        return l;

    }

    /**
    * Find entities by conditions
    *
     * @return List of ProfileVO
    *
    */
    public List<ProfileVO> searchProfileService(ServiceContext ctx,
        ProfileVO profileVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:searchProfileService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (ctx != null) {
        	ProfileUtils.dataFilter(ctx, con);
        }

        ProfileUtils.setListOfCriteria(ctx, con, profileVO);

        List<Profile> l_entity = new ArrayList<Profile>();
        List<ProfileVO> l = new ArrayList<ProfileVO>();

        int page = profileVO.getPage();
        int pageSize = profileVO.getPageSize();
        boolean countTotalPages = true;
        if (page > 0 && pageSize > 0) {
            PagingParameter pagingParameter =
                PagingParameter.pageAccess(pageSize, page, countTotalPages);

            PagedResult<Profile> pagedResult =
                this.getProfileRepository().findByCondition(con, pagingParameter);

            l_entity = pagedResult.getValues();

            ctx.setProperty("pagedResult", pagedResult);

        } else {
            l_entity = this.getProfileRepository().findByCondition(con);
        }

        l = ProfileUtils.mapListOfEntitiesToVO(ctx, l_entity, 0,
                profileVO.getLazy_level_col());

        l = ProfileUtils.filterMultiLang(profileVO, l);

        return l;

    }

    public String updateAllProfileService(ServiceContext ctx,
        List<ProfileVO> list_profile) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info(
                "PowerCardV3 : Operation:updateAllProfileService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        for (int i = 0; i < list_profile.size(); i++) {
            updateProfileService(ctx, list_profile.get(i));
        }

        return "0000";
    }

	@Override
	public String updateDataAccessProfile(ServiceContext ctx, ProfileVO profileVO) throws Exception {
		String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : updateDataAccessProfile , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        Profile profile = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(ProfileProperties.profile_id(),
                profileVO.getProfile_id()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Profile> pagedResult =
            this.getProfileRepository().findByCondition(con, pagingParameter);
        List<Profile> list = pagedResult.getValues();

        if (list.size() > 0) {
            profile = list.get(0);
        } else {
            throw new OurException("0001", new ProfileNotFoundException(""));
        }

        if (profileVO.getBank_data_access() != null) {
            profile.setBank_data_access(profileVO.getBank_data_access());
        }

        Profile profile1 = this.getProfileRepository().save(profile);

        return "0000";
	}
}
