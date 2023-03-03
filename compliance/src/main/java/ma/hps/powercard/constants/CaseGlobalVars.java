package ma.hps.powercard.constants;

public class CaseGlobalVars {

	/* ******************** CASE TYPE ******************** */ 
	public static String CONTROL_REJECT_CASE_TYPE            = "000001";
	public static String VALIDATE_MERCHANT_CASE_TYPE         = "000003";
	public static String VALIDATE_CONTRACT_CASE_TYPE         = "000005";
	public static String ACTIVATE_CONTRACT_CASE_TYPE         = "000006";
	public static String CHARGEBACK_CASE_TYPE                = "000007";
	public static String CHARGEBACK_ISS_CASE_TYPE            = "000017";
	public static String CARD_INVENTORY                      = "000018";
	public static String MAKER_CHECKER                       = "002121";
	public static String MEMO_FOR_POSTING_AGREEMENT          = "000742";
	public static String VALIDATION_HIEARCHIQUE              = "000008";
	public static String MERCHANT_CLAIMS_CASE_TYPE           = "000010";
	public static String SWITCH_MONITORING_CASE_TYPE         = "000011";
	public static String SWITCH_MON_ATM_CASE_TYPE            = "000014";
	public static String OFAC_MERCHANT_CASE_TYPE             = "000019";
	public static String RISK_FRAUD_CASE_TYPE                = "000021";
	public static String ACH_RETURN_CASE_TYPE                = "000020";
	public static String INST_ACH_RETURN_CASE_TYPE           = "000022";
	public static String REJECT_FILE_CASE_TYPE               = "000023";

	/* ******************** CASE REASON ******************** */ 
	public static String VALIDATE_MERCHANT_CASE_REASON       = "C04";
	public static String VALIDATE_CONTRACT_CASE_REASON       = "C05";
	public static String ACTIVATE_CONTRACT_CASE_REASON       = "C06";
	public static String MODIF_CARA_CASE_REASON              = "0002";
	public static String MODIF_CARA_PRS_CASE_REASON          = "0001";
	public static String MODIF_CARA_OPT_CASE_REASON          = "0003";
	public static String CLOSED_MERCHANT_CASE_REASON         = "0004";
	public static String CLOSED_PA_CASE_REASON               = "0005";
	public static String MODIF_ACTIVITY_PR_CASE_REASON       = "0006";
	public static String MODIF_ACTIVITY_SEC_CASE_REASON      = "0007";
	public static String MODIF_SIRET_CASE_REASON             = "0008";
	public static String VAL_HIERARCHIQUE_COND_TARIF         = "0009";
	public static String CLOSED_CH_ACCOUNT_CASE_REASON       = "0010";
	public static String CHARGEBACK_CASE_REASON              = "0001";// Impay�
	public static String INVAL_ADVICE_VISA_CASE_REASON       = "0006";// impay� invalid advice VISA 
	public static String RETRIEVAL_REQUEST_CASE_REASON       = "0002";// Demande de justificatif
	public static String FEES_CASE_REASON                    = "0003";// Frais
	public static String TECHNICAL_REJECT_CASE_REASON        = "0004";//Rejet technique
	public static String REVERSAL_CASE_REASON                = "0005";
	public static String SECOND_CHARGEBACK_CASE_REASON       = "0006";//2�me  impay�
	public static String INVALID_CHARGEBACK_CASE_REASON      = "0007";//Impay� invalide
	public static String INVALID_RET_REQ_CASE_REASON         = "0008";// Demande de justificatif Invalid
	public static String INVAL_ADVICE_VCR_CASE_REASON        = "0014"; 
	public static String INV_TEC_REJECT_CASE_REASON          = "0015";//Rejet technique invalid
	public static String REVERSAL_1ST_CHGBK_CASE_REASON      = "0010";// Reversal 1er chargeback
	public static String REVERSAL_2ND_CHGBK_CASE_REASON      = "0011";//Reversal 2eme chargeback
	public static String INVAL_REVERS_CHGBK_CASE_REASON      = "0012";// Annulation impay� invalid
	public static String INVAL_SECOND_CHGBK_CASE_REASON      = "0013";// 2�me impay� invalid
	public static String TERMINAL_OUT_OF_SERVICE        	 = "0014";
	public static String TERMINAL_REPLACEMENT            	 = "0015";
	public static String INVALID_TRANSACTION             	 = "0016";
	public static String MISSING_TRANSACTION             	 = "0017";
	public static String MISSING_REMITTANCE              	 = "0018";
	public static String TRANSACTION_CANCEL              	 = "0019";
	public static String DUPLICATE_TRANSACTION           	 = "0020";
	public static String STATEMENT_TRX_PROBLEM           	 = "0021";
	public static String MERCHANT_UPDATE_DATA            	 = "0022";
	public static String MERCHANT_STATUS_CHANGE          	 = "0023";
	public static String OUTLET_STATUS_CHANGE            	 = "0024";
	public static String MERCHANT_CONTRACT_CANCEL        	 = "0025";
	public static String OTHER_CLAIM                     	 = "0026";
	public static String ASSISTANCE_REQUEST              	 = "0027";
	public static String VISIT_REQUEST                   	 = "0028";
	public static String CUSTOMER_CLAIM                  	 = "0001";
	public static String POTENTIAL_CHARGEBACK            	 = "0002";
	public static String POT_CHGBK_AND_AUT_RESP          	 = "0003";
	public static String REPRESENTMENT_CASE_REASON       	 = "0004";// Representment
	public static String REVERS_REPRESENT_CASE_REASON    	 = "0005";
	public static String CUSTOMER_CLAIM_CR               	 = "0011";
	public static String POTENTIAL_CHARGEBACK_CR         	 = "0012";
	public static String POT_CHGBK_AND_AUT_RESP_CR       	 = "0013";
	public static String REPRESENTMENT_CASE_REASON_CR    	 = "0014";
	public static String REVERS_REPRES_CASE_REASON_CR    	 = "0015";
	
	public static String SWMON_CPU_USAGE_CASE_REASON      	 = "0001";// CPU usage
	public static String SWMON_DISK_FREE_CASE_REASON      	 = "0002";// Disk free
	public static String SWMON_LINE_STATUS_CASE_REASON       = "0003";// Line status
	public static String SWMON_KEY_STATUS_CASE_REASON      	 = "0004";// Key status
	public static String SWMON_APP_STATUS_CASE_REASON      	 = "0005";// App status
	public static String SWMON_ECHO_TESTS_CASE_REASON      	 = "0006";// Echo tests
	public static String SWMON_APPROVALS_CASE_REASON      	 = "0007";// Approvals
	public static String SWMON_DECLINES_CASE_REASON      	 = "0008";// Declines
	public static String SWMON_MALFUNCTIONS_CASE_REASON      = "0009";// Malfunctions
	public static String SWMON_AV_RT_ISS_CASE_REASON      	 = "0010";// Average response time issuer
	public static String SWMON_AV_RT_ACQ_CASE_REASON      	 = "0011";// Average response time acquirer
	public static String SWMON_CARD_CAPT_CASE_REASON      	 = "TCRF";// Capture card
	public static String SWMON_CASH_HNDL_CASE_REASON      	 = "TCDE";// Cash handler problem
	public static String SWMON_RECEIPT_DYSF_CASE_REASON   	 = "TCPE";// Receipt printer dysfunction
	public static String SWMON_JOURNAL_DYSF_CASE_REASON   	 = "TLPE";// Journal printer dysfonction

	public static String OFAC_MERCHANT_CASE_REASON      	 = "0029";// OFAC MERCHANT
	
	public static int RECYCLE_REMITTANCE_ACTION      		 = 1;//'Recycler remise'
	public static int RECYCLE_TRANSACTION_ACTION     		 =  2;//'Recycler Transaction'
	public static int RECYCLE_TRANS_OR_REMIT_ACTION  		 =  3;//recycle trx or remittance

	public static String OPEN_STATUS                         = "O";
	public static String CLOSE_STATUS                        = "F";
	public static String CLOSED_STATUS                       = "C";//LBN20160413 - #CEDIC00018173
	public static String REOPEN_STATUS                       = "R";//ATH03082012
	public static String MANUAL_SOURCE                       = "M";// MANUAL
	public static String AUTOMATIC_SOURCE                    = "A";// AUTOMATIC

	/* ******************** Liste des actions ******************** */
	//Acquiring Action
	//Manual
	public static String VIEW_INVALID_EVENT                  = "000705";//Consulter �venement invalide
	public static String VIEW_INVAL_EVENT                    = "000711";//Consulter impay� invalide
	
	public static String MANUAL_CHARGEBACK_POSTING           = "000729";//Imputation manuelle de l'impay�
	public static String MANUAL_CHARGEBACK_SEC_POSTING       = "000713";//Imputation manuelle de l'impay�
	public static String CANCELLATION_CHRGB_POSTING          = "000714";//Annulation imputation de l'impay�
	public static String CANC_MANUAL_CHARGEBACK_POSTING      = "000740";//Imputation manuelle de l'annulation de l'impay�
	public static String ENTRY_FEE_MERCHANT                  = "000725";//Saisie d''un frais commer�ant
	public static String CANCELLATION_MERCHANT_CHARGES       = "000727";//Annulation d''un frais commer�ant
	
	public static String MANUAL_SECOND_PRESENTMENT           = "000719";//Repr�sentation manuelle 
	public static String REVERSAL_SECOND_PRESENTMENT         = "000728";//Annulation repr�sentation
	public static String ARBITRATION                         = "000721";//Arbitrage 
	public static String ENTERING_VISA_FEES                  = "010722";//Saisie d'un frais r�seau VISA
	public static String MANUAL_VISA_FEES_CHRGBK             = "010737";//Imputation manuelle du frais VISA
	public static String MAN_ENTR_VISA_FEES_CHRGBK           = "010746";//Imputation manuelle du frais VISA saisi
	public static String NET_MASTERCARD_FEES                 = "020723";//Saisie d''un frais r�seau Mastercard
	public static String MASTERCARD_FEES                     = "020735";//Saisie d''un frais Mastercard
	public static String MAN_MASTERCRD_FEES_CHRGBK           = "020738";//Imputation manuelle du frais Mastercard
	public static String MAN_ENTR_MASTERCRD_FEES_CHRGBK      = "020746";//Imputation manuelle du frais Mastercard saisi
	
	
	
	
	public static String RES_REQUEST                         = "000706";//R�ponse � une demande de justificatif
	public static String REQ_TH_JUST_FROM_MERCHANT           = "000732";//Demande de justificatif au commer�ant
	public static String SENDING_DDJ_MAIL                    = "000743";//Envoi Courrier DDJ
	public static String RESPONSE_DDJ_MAIL                   = "000745";//R�ponse au courrier DDJ
	public static String AUTO_TICKET_RECON                   = "090709";//Reconstitution automatique du ticket
	public static String PRINTING_FRE                        = "090748";//Impression FRE
	public static String EXPORT_BEJ_DATA                     = "090747";//Export donn�es BEJ
	
	public static String REQ_RET_FROM_MERCHANT               = "000720";//Demande de justificatif au commer�ant
	public static String INVALID_TECHNICAL_REJECTS           = "000710";//Rejets techniques invalides
	public static String RECYCLE_VISA_TRX 					 = "010701";//Recycle Visa                     
	public static String RECYCLE_MCI_TRX 					 = "020702";//Recycle MasterCard   
	public static String RECYCLE_UPI_TRX 					 = "080701";//Recycle UPI
	public static String RECYCLE_CB_TRX 					 = "090703";//Recycle CB   
	public static String CB_DELETED_TECH_REJ                 = "090751";//CB - Rejets techniques supprim�s
	public static String PRIVATIF_DELETED_TECH_REJ           = "090751";//ONUS - Rejets techniques supprim�s
	public static String GENERIC_DELETED_TECH_REJ            = "000746";//Rejets techniques supprim�s
	public static String GENERIC_DELETED_INV_TECH_REJ        = "000747";//Rejets techniques invalides supprim�s
	public static String MEMO_FETCH                          = "000704";//Saisie d'un m�mo
	public static String OFAC_MERCHANT_ACTION                = "001704";//Check merchant OFAC
	public static String MERCHANT_MEMO_ACTION                = "100002";//Merchant memo action
	public static String ADD_FILE_ACTION                     = "100003";//Add file action
	//Automatique
	
	//public static String AUTOMATIC_VROL_REPRESENTMENT        = "000746";//Automatic Vrol representement 
	public static String AUTOMATIC_CHARGEBACK_POSTING        = "000716";//Imputation automatique de l'impay�
	public static String AUTOMATIC_SECOND_PRESENTMENT        = "000717";//Repr�sentation automatique
	public static String INVALID_CB_CHARGEBACK_PROCESS       = "010712";//Traiter impay� invalide CB
	public static String CB_RET_REQ_AUTOMATIC_CTRL           = "090708";//Contr�les automatiques CB � r�ception
	public static String CB_CHARGEBACK_AUTOMATIC_CTRL        = "090708";//Contr�les automatiques CB � r�ception
	public static String CB_SECOND_CHBCK_AUTOMATIC_CTRL      = "090731";//Contr�les automatiques � r�ception
	
	public static String RET_REQ_SENT_TO_MERCHANT            = "000707";//Envoi d'une demande de justificatif
	public static String MEMO_POSTING_REQUEST                = "000715";//M�mo demande d'accord d'imputation
	
	public static String CHAGEBACK_IMG_SENT_TO_MERCHANT      = "000718";//Image d'impay� transmise au commer�ant
	public static String REQ_SECOND_JUST_FROM_MERCHANT       = "000726";//Demande de justificatif au commer�ant
	public static String AUTOMATIC_SECOND_CHBCK_POSTING      = "000730";//Imputation automatique de l'impay�
	public static String ENVOIE_COURRIER_DDJ                 = "000744";//Envoie courrier DDJ
	public static String RET_REQ_RESPONSE                    = "090746";//R�ponse demande de justificatif < 1500 euros
	public static String RET_REQ_RESPONSE_MT_SUP             = "090752";//R�ponse demande de justificatif > 1500 euros
	public static String AUTOMATIC_VROL_PREARBITRATION       = "010738";//Automatic Vrol pre-arbitration 
	public static String AUTOMATIC_VROL_ARBITRATION          = "010739";//Automatic Vrol arbitration 
	public static String AUTOMATIC_VROL_REPRESENTMENT        = "010747";//Automatic Vrol representment 
	public static String AUTOMATIC_VROL_REPRESENTMENT_CANCEL = "010748";//Automatic Vrol representment cancel
	
	
	public static String MEMO_INVALID_CHGBK_REASON           = "090750";//M�mo pour Impay� invalide pour motif inconnu //ATH06082014
	
	  
	//Issuing Action
	public static String POT_CHGBK_AND_AUT_RESP_ACTION       = "001703";//Automatic chargeback en reponse d'un potentiel chargeback
	public static String AUTOMATIC_VROL_CANCEL               = "000747";//Automatic Vrol cancellation
	public static String FIRST_CHARGEBACK_REVERSAL           = "001706";//Automatic first chargeback reversal    
	public static String AUTOMATIC_MEMO                      = "001720";//Cr�ation automatique d'une action m�mo 
	
	
	
	/* ******************** Scenario ******************** */ 
	public static String VISA_RET_REQ_SENARIO                = "010702";//scenario demande de justificatif VISA
	public static String CB_RET_REQ_SENARIO                  = "090702";//scenario demande de justificatif CB
	public static String PRIVATIF_RET_REQ_SENARIO            = "000702";//scenario demande de justificatif CB  //LBN20160310 - #CEDIC00017964
	public static String MASTERCARD_RET_REQ_SENARIO          = "020702";//scenario demande de justificatif Mastercard
	public static String VISA_FEES_SENARIO                   = "010703";//scenarion frais VISA
	public static String MASTERCARD_FEES_SENARIO             = "020703";//scenarion frais Mastercard
	public static String CB_FEES_SENARIO                     = "090703";//scenarion frais CB
	public static String TECHNICAL_REJECT_SENARIO            = "000004";//scenario rejet technique
	public static String VISA_TECH_REJ_SCENARIO              = "010704";//Scenario rejet technique VISA
	public static String CB_TECH_REJ_SCENARIO                = "090704";//CB - Scenario rejet technique
	public static String PRIVATIF_TECH_REJ_SCENARIO          = "090704";//ONUS - Scenario rejet technique
	public static String MASTERCARD_TECH_REJ_SCENARIO        = "020704";//Scenario rejet technique Mastercard
	public static String CUP_TECH_REJ_SCENARIO               = "080704";//Scenario rejet technique CUP
	public static String INVALID_TECH_REJ_SCENARIO           = "000710";//Scenario rejet technique invalide
	public static String AOCT_SENARIO                        = "000005";//scenario AOCT
	public static String INVALID_RET_REQ_SENARIO             = "000008";//scenario demande de justificatif invalide
	public static String VISA_CHARGEBACK_SENARIO             = "010701";//scenario Impay� Visa
	public static String VISA_VROL_CHARGEBACK_SENARIO        = "010709";//scenario chargeback visa from vrol
	public static String MC_CHARGEBACK_SENARIO               = "020701";//scenario Impay� Mastercard
	public static String CB_CHARGEBACK_SENARIO               = "090701";//scenario Impay� CB
	public static String PRIVATIF_CHARGEBACK_SENARIO         = "000701";//scenario Impay� CB //LBN20160310 - #CEDIC00017964
	public static String DISCOVER_CHARGEBACK_SENARIO         = "070701";//scenario Impay� DISCOVER MFL16072016
	public static String VISA_SECOND_CHBCK_SENARIO           = "010706";//scenario 2�me  impay� VISA
	public static String MC_SECOND_CHBCK_SENARIO             = "020706";//scenario 2�me  impay� Mastercard
	public static String CB_SECOND_CHBCK_SENARIO             = "090706";//scenario 2�me  impay� CB
	public static String PRIVATIF_SECOND_CHBCK_SENARIO       = "000706";//scenario Impay� CB //LBN20160310 - #CEDIC00017964
	public static String VISA_INVALID_CHBCK_SENARIO          = "010707";//scenario impay� invalide VISA
	public static String MC_INVALID_CHBCK_SENARIO            = "020707";//scenario impay� invalide Mastercard
	public static String CB_INVALID_CHBCK_SENARIO            = "090707";//scenario impay� invalide CB
	public static String POT_CHGBK_AND_AUT_RESP_SENARIO      = "001703";//Automatic chargeback en reponse � un potentiel chargeback //SNO27062014
	public static String POT_CHGBK_AND_AUT_RESP_SENA_CR      = "001713";//Automatic chargeback en reponse � un potentiel chargeback carte cr�dit //SNO20092014
	public static String ACH_CARDHOLDER_RETURN_SCENARIO      = "909001";//ACH CARDHOLDER RETURN 
	public static String INVALID_CHBCK_SENARIO               = "000007";//scenario impay� invalide
	public static String INVALID_ADVICE_VCR_VISA             = "011716";//scenario impay� invalid advice VISA 

	public static String FLAG_MODIF_CARACTERISTIC            =  "MC"; // Modification caract�ristique
	public static String FLAG_MODIF_CARAC_PRS                =  "MS";//Modification caract�ristique  PRS
	public static String FLAG_MODIF_CARAC_OPT                =  "MO";//Modification caract�ristique OPT
	public static String FLAG_CLOSED_MERCHANT                =  "CM";//Cloture commer�ant
	public static String FLAG_CLOSED_PA                      =  "CP";//colture point accepteur
	public static String FLAG_MODIF_ACTIVITY_PR              =  "AP";//Modification Activit� principale
	public static String FLAG_MODIF_ACTIVITY_SEC             =  "AS";//Modification Activit� secondaire
	public static String FLAG_MODIF_SIRET                    =  "US";//MOU03072013 Modification SIRET
	public static String FLAG_VAL_HIERARCHIQ_COND_TARIF      =  "CT";//MOU18042014 Validation hierarchique de la conditiond de tarification
	public static String FLAG_CLOSED_CHAIN_ACCOUNT           =  "CA";//STI22102015 Cloture compte chaine
	
	public static String MANUAL_GEN_FLAG                     =  "0";
	public static String AUTOMATIC_GEN_FLAG                  =  "1";
	public static int 	 TECHNICAL_REJECT_POS                =  1;
	public static int	 CHARGEBACK_POS                      =  2;
	public static int	 INVALID_CHARGEBACK_POS              =  3;
	public static int	 SECOND_CHARGEBACK_POS               =  4;
	public static int 	 RETRIEVAL_REQUEST_POS               =  5;
	public static int	 INVALID_RET_REQ_POS                 =  6;
	public static int	 NETWORK_FEE_POS                     =  7;
	public static int	 REVERSAL_1ST_CHGBK_POS              =  8;
	public static int	 REVERSAL_2ND_CHGBK_POS              =  9;
	public static int	 REPRESENTMENT_POS                   =  11;
	public static int	 POTENTIAL_CHARGEBACK_POS            =  12;
	public static int 	 POT_CHGBK_AND_AUT_RESP_POS          =  13;
	public static int	 REPRESENTMENT_REVERS_POS            =  14;
	
	public static int 	 INV_TECHNICAL_REJ_POS               = 11;
	public static String AUTOMATIC_POTENTIAL_CHARGEBACK      = "AUPC";

}
