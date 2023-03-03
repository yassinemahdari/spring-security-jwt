package ma.hps.powercard.compliance.specificvo;

import ma.hps.powercard.compliance.serviceapi.Bank_groupVO;

public class BankGroupSpecificVo extends Bank_groupVO {

	private static final long serialVersionUID = 1L;
	private String bank_code;
	private String banks_codes;

	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}

	public String getBank_code() {
		return bank_code;
	}

	public String getBanks_codes() {
		return banks_codes;
	}

	public void setBanks_codes(String banks_codes) {
		this.banks_codes = banks_codes;
	}

}
