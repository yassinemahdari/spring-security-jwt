package ma.hps.powercard.constants;

import java.util.Optional;

public class GlobalVars {

	public static String YES									 =  "Y";
	public static String NO										 =  "N";
	
	public static String NOT_REVERSAL							 =  "N";
	public static String REVERSAL								 =  "R";
	
	public static String NETWORK_VISA                        	 =  "01";
	public static String NETWORK_MASTERCARD                  	 =  "02";
	public static String NETWORK_EUROPAY                     	 =  "03";
	public static String NETWORK_AMEX                        	 =  "04";
	public static String NETWORK_DINERS                      	 =  "05";
	public static String NETWORK_JCB                         	 =  "06";
	public static String NETWORK_DISCOVER                   	 =  "07";
	public static String NETWORK_CTMI                       	 =  "21";
	public static String NETWORK_ACH                         	 =  "90";
	public static String NETWORK_V_BANKSERV                 	 =  "22";
	public static String NETWORK_M_BANKSERV                 	 =  "23";
	public static String NETWORK_CREDIBANCO                 	 =  "31";
	public static String NETWORK_REDEBAN                    	 =  "41";
	public static String NETWORK_SERVIBANCA                 	 =  "51";
	public static String NETWORK_CUP                        	 =  "08";
	public static String NETWORK_CB                         	 =  "09";
	public static String NETWORK_PRIVATIF                   	 =  "00";
	
	//Entity codes
	public static String EC_BANK                                  =  "BK";
	public static String EC_BRANCH                                =  "BR";
	public static String EC_CHANNEL                               =  "NL";
	public static String EC_PARTNER_BRAND                         =  "PB";
	public static String EC_CARDHOLDER                            =  "CH";
	public static String EC_CLIENT                                =  "CL";
	public static String EC_SHADOW_ACCOUNT                        =  "SA";
	public static String EC_CORPORATE                             =  "CO";
	public static String EC_CHAIN                                 =  "CA";
	public static String EC_MERCHANT                              =  "MC";
	public static String EC_CONTRAT_ITEM                          =  "CR";//EL � enlever
	public static String EC_OUTLET                                =  "OL";
	public static String EC_TERMINAL_ATM                          =  "TA";
	public static String EC_TERMINAL_POS                          =  "TP";
	public static String EC_INSTITUTION_ACCOUNT                   =  "IT";
	public static String EC_CHAIN_ACCOUNT                         =  "CT";
	public static String EC_MERCHANT_ACCOUNT                      =  "MT";
	public static String EC_SERVICE                               =  "SV";

	
	
	public static String INTRA_BANK                                  =  "0";
	public static String INTRA_GROUP                                 =  "1";
	public static String INTRA_PLATEFORM                             =  "2" ;
	public static String INTER_BANK                                  =  "3";
	
	public static String EURO_CCY	                                 =  "978";
	public static String USD_CCY	                                 =  "840";
	
	// Security
	public static final String FIRST_LOGIN_CHANGE_PASS               = "FIRST_LOGIN_CHANGE_PASS";
	public static final String PASSWORD_DICT_PATH                    = "META-INF/pass.dict.conf";
	public static final String SECURE_PREFIX = ")]}',\n";

	// Other
	public static Optional<String> timeZone = Optional.empty();
	
}
