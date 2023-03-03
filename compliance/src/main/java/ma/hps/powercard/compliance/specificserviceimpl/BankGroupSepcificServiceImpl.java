package ma.hps.powercard.compliance.specificserviceimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ma.hps.powercard.compliance.serviceapi.Bank_groupService;
import ma.hps.powercard.compliance.serviceapi.Bank_groupVO;
import ma.hps.powercard.compliance.serviceapi.Bank_group_associationService;
import ma.hps.powercard.compliance.serviceapi.Bank_group_associationVO;
import ma.hps.powercard.compliance.specificvo.BankGroupSpecificVo;

@Component
public class BankGroupSepcificServiceImpl {

	@Autowired
	private Bank_groupService bankGroupService;

	@Autowired
	private Bank_group_associationService bankGroupAssocService;

	public List<Bank_groupVO> searchSpecficBankGroupService(ServiceContext ctx, BankGroupSpecificVo bankGroupSpecificVo)
			throws Exception {
		Bank_groupVO bank_groupVO = new Bank_groupVO();
		bank_groupVO.setAbrv_wording(bankGroupSpecificVo.getAbrv_wording());
		bank_groupVO.setGroup_code(bankGroupSpecificVo.getGroup_code());
		List<Bank_groupVO> bankGroupList = new ArrayList<Bank_groupVO>();
		Bank_group_associationVO bank_group_associationVO = new Bank_group_associationVO();
		if (bankGroupSpecificVo != null) {
			if (bankGroupSpecificVo.getBank_code() != null && !bankGroupSpecificVo.getBank_code().equals("")) {
				
				bank_group_associationVO.setBank_code(bankGroupSpecificVo.getBank_code());
				bank_group_associationVO.setGroup_code(bankGroupSpecificVo.getGroup_code());
				List<Bank_group_associationVO> bankGroupAssociationList = bankGroupAssocService
						.searchBank_group_associationService(ctx, bank_group_associationVO);
						
						
				if (bankGroupAssociationList != null && bankGroupAssociationList.size() > 0) {
					Set<String> group_codes = new HashSet<String>();
					for (Bank_group_associationVO element : bankGroupAssociationList) {
						group_codes.add(element.getGroup_code());
					}
					bank_groupVO.setGroup_codeCollection(group_codes);
					bankGroupList = bankGroupService.searchBank_groupService(ctx, bank_groupVO);
				}
			} else {
				List<String> banks = (List<String>) ctx.getProperty("bankDataAccess");
				
				if (banks.size() > 0) {
				
					for (String bank : banks ){
						
						bank_group_associationVO.setBank_code(bank);
						bank_group_associationVO.setGroup_code(bankGroupSpecificVo.getGroup_code());
						List<Bank_group_associationVO> bankGroupAssociationList = bankGroupAssocService
								.searchBank_group_associationService(ctx, bank_group_associationVO);
								
								
						if (bankGroupAssociationList != null && bankGroupAssociationList.size() > 0) {
							
							Set<String> group_codes = new HashSet<String>();
							
																				   
	  
													  
	  
							for (Bank_group_associationVO element : bankGroupAssociationList) {
								group_codes.add(element.getGroup_code());
							}
							bank_groupVO.setGroup_codeCollection(group_codes);
							bankGroupList.addAll(bankGroupService.searchBank_groupService(ctx, bank_groupVO));
						}					
					}
				}else {
					bankGroupList = bankGroupService.searchBank_groupService(ctx, bank_groupVO);
					//getBankGroupAssociation(bankGroupList, ctx);
				}
	

			}
			getBankGroupAssociation(bankGroupList, ctx);
		}
		return bankGroupList;
	}

	private void getBankGroupAssociation(List<Bank_groupVO> bankGroupList, ServiceContext ctx) throws Exception {
		Bank_group_associationVO bank_group_associationVO = new Bank_group_associationVO();
		if (bankGroupList != null && bankGroupList.size() > 0) {
			for (Bank_groupVO bank_group : bankGroupList) {
				bank_group_associationVO.setGroup_code(bank_group.getGroup_code());
				List<Bank_group_associationVO> bankGroupAssociationListTmp = bankGroupAssocService
						.searchBank_group_associationService(ctx, bank_group_associationVO);
				bank_group.setBank_group_associations_col(bankGroupAssociationListTmp);
			}
		}
	}

}