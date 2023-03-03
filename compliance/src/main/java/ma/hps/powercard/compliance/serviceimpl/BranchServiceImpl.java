package ma.hps.powercard.compliance.serviceimpl;

import org.apache.log4j.Logger;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.domain.PagedResult;
import org.fornax.cartridges.sculptor.framework.domain.PagingParameter;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.Bank;
import ma.hps.powercard.compliance.domain.Branch;
import ma.hps.powercard.compliance.domain.BranchPK;
import ma.hps.powercard.compliance.domain.BranchProperties;
import ma.hps.powercard.compliance.exception.BranchNotFoundException;
import ma.hps.powercard.compliance.repositoryimpl.BranchUtils;
import ma.hps.powercard.compliance.serviceapi.BranchVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of BranchService.
 */
@Lazy
@Service("branchService")
public class BranchServiceImpl extends BranchServiceImplBase {
    private static Logger logger = Logger.getLogger(BranchServiceImpl.class);

    public BranchServiceImpl() {
    }

    /**
    * Persist a Branch entity .
    *
    * @param BranchVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0024 If ever the object already exists.
    *
    */
    public String createBranchService(ServiceContext ctx, BranchVO branchVO)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:createBranchService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        BranchPK branchPK =
            new BranchPK(branchVO.getBranch_code(), branchVO.getBank_code());

        Branch branch = null;
        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(BranchProperties.branchPK()
                                                          .branch_code(),
                branchVO.getBranch_code()));

        con.add(ConditionalCriteria.equal(BranchProperties.branchPK().bank_code(),
                branchVO.getBank_code()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Branch> pagedResult =
            this.getBranchRepository().findByCondition(con, pagingParameter);
        List<Branch> list = pagedResult.getValues();

        if (list.size() > 0) {
            throw new OurException("0024", new Exception(""));
        }
        else {
           
            branch = new Branch(branchPK);
        }

        if (branchVO.getBank_code() != null &&
              !branchVO.getBank_code().equals("")) {
            branch.setFk_branch_01(new Bank(branchVO.getBank_code()));

        } else {
            if ("".equals(branchVO.getBank_code())) {
                branch.setFk_branch_01(null);
            }
        }

        branch.setBranch_name(branchVO.getBranch_name());
        
        branch.setAbrev_name(branchVO.getAbrev_name());

        branch.setGrouping_code(branchVO.getGrouping_code());

        branch.setAddress_1(branchVO.getAddress_1());

        branch.setAddress_2(branchVO.getAddress_2());

        branch.setAddress_3(branchVO.getAddress_3());

        branch.setAddress_4(branchVO.getAddress_4());

        branch.setZip_code(branchVO.getZip_code());

        branch.setCity_code(branchVO.getCity_code());

        branch.setRegion_code(branchVO.getRegion_code());

        branch.setCountry_code(branchVO.getCountry_code());

        branch.setPhone_1(branchVO.getPhone_1());

        branch.setPhone_2(branchVO.getPhone_2());

        branch.setFax_number(branchVO.getFax_number());

        branch.setMail(branchVO.getMail());

        branch.setWeb_url(branchVO.getWeb_url());

        branch.setContact_name(branchVO.getContact_name());

        branch.setContact_phone(branchVO.getContact_phone());

        branch.setRenew_print_delay(branchVO.getRenew_print_delay());

        branch.setAccount_number(branchVO.getAccount_number());

        branch.setKey_account(branchVO.getKey_account());

        branch.setCurrency_code(branchVO.getCurrency_code());

        branch.setStatus(branchVO.getStatus());

        branch.setStatus_date(branchVO.getStatus_date());

        Branch branch1 = this.getBranchRepository().save(branch);

        return "0000";

    }

    /**
    * update a Branch entity .
    *
    * @param BranchVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String updateBranchService(ServiceContext ctx, BranchVO branchVO)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:updateBranchService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        BranchPK branchPK =
            new BranchPK(branchVO.getBranch_code(), branchVO.getBank_code());

        Branch branch = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(BranchProperties.branchPK()
                                                          .branch_code(),
                branchVO.getBranch_code()));

        con.add(ConditionalCriteria.equal(BranchProperties.branchPK().bank_code(),
                branchVO.getBank_code()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Branch> pagedResult =
            this.getBranchRepository().findByCondition(con, pagingParameter);
        List<Branch> list = pagedResult.getValues();

        if (list.size() > 0) {
       
            branch = list.get(0);
        } else {
            throw new OurException("0001", new BranchNotFoundException(""));
        }

        if (branchVO.getBank_code() != null &&
              !branchVO.getBank_code().equals("")) {
            branch.setFk_branch_01(new Bank(branchVO.getBank_code()));

        } else {
            if ("".equals(branchVO.getBank_code())) {
                branch.setFk_branch_01(null);
            }
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("fk_branch_01"))) {
            branch.setFk_branch_01(null);
        }

        if (branchVO.getBranch_name() != null) {
            branch.setBranch_name(branchVO.getBranch_name());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("branch_name"))) {
            branch.setBranch_name(null);
        }
        
        if (branchVO.getAbrev_name() != null) {
        	branch.setAbrev_name(branchVO.getAbrev_name());
        }
        
        if ((branchVO.getBranchColVO() != null) &&
        		(branchVO.getBranchColVO().contains("abrev_name"))) {
        	branch.setAbrev_name(null);
        }

        if (branchVO.getGrouping_code() != null) {
            branch.setGrouping_code(branchVO.getGrouping_code());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("grouping_code"))) {
            branch.setGrouping_code(null);
        }

        if (branchVO.getAddress_1() != null) {
            branch.setAddress_1(branchVO.getAddress_1());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("address_1"))) {
            branch.setAddress_1(null);
        }

        if (branchVO.getAddress_2() != null) {
            branch.setAddress_2(branchVO.getAddress_2());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("address_2"))) {
            branch.setAddress_2(null);
        }

        if (branchVO.getAddress_3() != null) {
            branch.setAddress_3(branchVO.getAddress_3());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("address_3"))) {
            branch.setAddress_3(null);
        }

        if (branchVO.getAddress_4() != null) {
            branch.setAddress_4(branchVO.getAddress_4());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("address_4"))) {
            branch.setAddress_4(null);
        }

        if (branchVO.getZip_code() != null) {
            branch.setZip_code(branchVO.getZip_code());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("zip_code"))) {
            branch.setZip_code(null);
        }

        if (branchVO.getCity_code() != null) {
            branch.setCity_code(branchVO.getCity_code());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("city_code"))) {
            branch.setCity_code(null);
        }

        if (branchVO.getRegion_code() != null) {
            branch.setRegion_code(branchVO.getRegion_code());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("region_code"))) {
            branch.setRegion_code(null);
        }

        if (branchVO.getCountry_code() != null) {
            branch.setCountry_code(branchVO.getCountry_code());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("country_code"))) {
            branch.setCountry_code(null);
        }

        if (branchVO.getPhone_1() != null) {
            branch.setPhone_1(branchVO.getPhone_1());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("phone_1"))) {
            branch.setPhone_1(null);
        }

        if (branchVO.getPhone_2() != null) {
            branch.setPhone_2(branchVO.getPhone_2());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("phone_2"))) {
            branch.setPhone_2(null);
        }

        if (branchVO.getFax_number() != null) {
            branch.setFax_number(branchVO.getFax_number());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("fax_number"))) {
            branch.setFax_number(null);
        }

        if (branchVO.getMail() != null) {
            branch.setMail(branchVO.getMail());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("mail"))) {
            branch.setMail(null);
        }

        if (branchVO.getWeb_url() != null) {
            branch.setWeb_url(branchVO.getWeb_url());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("web_url"))) {
            branch.setWeb_url(null);
        }

        if (branchVO.getContact_name() != null) {
            branch.setContact_name(branchVO.getContact_name());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("contact_name"))) {
            branch.setContact_name(null);
        }

        if (branchVO.getContact_phone() != null) {
            branch.setContact_phone(branchVO.getContact_phone());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("contact_phone"))) {
            branch.setContact_phone(null);
        }

        if (branchVO.getRenew_print_delay() != null) {
            branch.setRenew_print_delay(branchVO.getRenew_print_delay());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("renew_print_delay"))) {
            branch.setRenew_print_delay(null);
        }

        if (branchVO.getAccount_number() != null) {
            branch.setAccount_number(branchVO.getAccount_number());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("account_number"))) {
            branch.setAccount_number(null);
        }

        if (branchVO.getKey_account() != null) {
            branch.setKey_account(branchVO.getKey_account());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("key_account"))) {
            branch.setKey_account(null);
        }

        if (branchVO.getCurrency_code() != null) {
            branch.setCurrency_code(branchVO.getCurrency_code());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("currency_code"))) {
            branch.setCurrency_code(null);
        }

        if (branchVO.getStatus() != null) {
            branch.setStatus(branchVO.getStatus());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("status"))) {
            branch.setStatus(null);
        }

        if (branchVO.getStatus_date() != null) {
            branch.setStatus_date(branchVO.getStatus_date());
        }

        if ((branchVO.getBranchColVO() != null) &&
              (branchVO.getBranchColVO().contains("status_date"))) {
            branch.setStatus_date(null);
        }

        Branch branch1 = this.getBranchRepository().save(branch);

        return "0000";

    }

    /**
    * delete a Branch entity .
    *
    * @param BranchVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String deleteBranchService(ServiceContext ctx, BranchVO branchVO)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:deleteBranchService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        ServiceContextStore.set(ctx);

        BranchPK branchPK =
            new BranchPK(branchVO.getBranch_code(), branchVO.getBank_code());

        Branch branch = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(BranchProperties.branchPK()
                                                          .branch_code(),
                branchVO.getBranch_code()));

        con.add(ConditionalCriteria.equal(BranchProperties.branchPK().bank_code(),
                branchVO.getBank_code()));

        PagingParameter pagingParameter =
            PagingParameter.pageAccess(1, 1, false);
        PagedResult<Branch> pagedResult =
            this.getBranchRepository().findByCondition(con, pagingParameter);
        List<Branch> list = pagedResult.getValues();

        if (list.size() > 0) {
           
            branch = list.get(0);
        } else {
            throw new OurException("0001", new BranchNotFoundException(""));
        }

        if (branchVO.getBank_code() != null &&
              !branchVO.getBank_code().equals("")) {
            branch.setFk_branch_01(new Bank(branchVO.getBank_code()));

        } else {
            if ("".equals(branchVO.getBank_code())) {
                branch.setFk_branch_01(null);
            }
        }

        this.getBranchRepository().delete(branch);

        return "0000";

    }

    /**
    *  Find all entities of a specific type .
    *
    * @return List of BranchVO
    *
    */
    public List<BranchVO> getAllBranchService(ServiceContext ctx)
        throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:getAllBranchService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        List<BranchVO> l = new ArrayList<BranchVO>();

        List<Branch> l_entity = new ArrayList<Branch>();

        l_entity = this.getBranchRepository().findAll();

        l = BranchUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

        return l;

    }

    /**
    * Find entities by conditions
    *
     * @return List of BranchVO
    *
    */
    public List<BranchVO> searchBranchService(ServiceContext ctx,
        BranchVO branchVO) throws Exception {
        String sessionID = null;
        String remoteAddress = null;
        if (ctx != null) {
            if (ctx.getDetails() != null) {
                sessionID = ctx.getDetails().getSessionId();
                remoteAddress = ctx.getDetails().getRemoteAddress();
            }
            logger.info("PowerCardV3 : Operation:searchBranchService , USER :" +
                ctx.getUserId() + " , SessionID :" + sessionID +
                " , RemoteAddress:" + remoteAddress);
        }

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (ctx != null) {
        	if(branchVO.getSkip_da() != null && branchVO.getSkip_da().equalsIgnoreCase("Y")) {
        		//not applying dataFilter
        	} else {
        		BranchUtils.dataFilter(ctx, con);        		
        	}
        }

        BranchUtils.setListOfCriteria(ctx, con, branchVO);

        List<Branch> l_entity = new ArrayList<Branch>();
        List<BranchVO> l = new ArrayList<BranchVO>();

        int page = branchVO.getPage();
        int pageSize = branchVO.getPageSize();
        boolean countTotalPages = true;
        if (page > 0 && pageSize > 0) {
            PagingParameter pagingParameter =
                PagingParameter.pageAccess(pageSize, page, countTotalPages);

            PagedResult<Branch> pagedResult =
                this.getBranchRepository().findByCondition(con, pagingParameter);

            l_entity = pagedResult.getValues();

            ctx.setProperty("pagedResult", pagedResult);

        } else {
            l_entity = this.getBranchRepository().findByCondition(con);
        }

        l = BranchUtils.mapListOfEntitiesToVO(ctx, l_entity, 0,
                branchVO.getLazy_level_col());

        l = BranchUtils.filterMultiLang(branchVO, l);

        return l;

    }

   
  
  //spec touch
}