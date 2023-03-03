package ma.hps.powercard.compliance.serviceimpl;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.log4j.Logger;
import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.domain.PagedResult;
import org.fornax.cartridges.sculptor.framework.domain.PagingParameter;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.McEscalation;
import ma.hps.powercard.compliance.domain.McFieldCriteria;
import ma.hps.powercard.compliance.domain.McOperation;
import ma.hps.powercard.compliance.domain.McOperationPar;
import ma.hps.powercard.compliance.domain.McOperationProperties;
import ma.hps.powercard.compliance.domain.McParDependency;
import ma.hps.powercard.compliance.exception.McOperationNotFoundException;
import ma.hps.powercard.compliance.exception.McParDependencyNotFoundException;
import ma.hps.powercard.compliance.repositoryimpl.McEscalationUtils;
import ma.hps.powercard.compliance.repositoryimpl.McOperationUtils;
import ma.hps.powercard.compliance.serviceapi.McEscalationVO;
import ma.hps.powercard.compliance.serviceapi.McFieldCriteriaVO;
import ma.hps.powercard.compliance.serviceapi.McOperationParService;
import ma.hps.powercard.compliance.serviceapi.McOperationParVO;
import ma.hps.powercard.compliance.serviceapi.McOperationVO;
import ma.hps.powercard.compliance.serviceapi.McParDependencyService;
import ma.hps.powercard.compliance.serviceapi.UsersService;

import ma.hps.powercard.compliance.serviceapi.UsersVO;
import ma.hps.powercard.compliance.utils.GsonHelper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation222 of McOperationService.
 */
@Lazy
@Service("mcOperationService")
public class McOperationServiceImpl extends McOperationServiceImplBase {
	private static Logger logger = Logger.getLogger(McOperationServiceImpl.class);

	@Lazy
	@Autowired
	private McOperationParService mcOperationParService;

	@Lazy
	@Autowired
	private McParDependencyService mcParDependencyService;

	@Lazy
	@Autowired
	private UsersService usersService;
	
	private static final String NO_ACTION_TAKEN = "NO_ACTION_TAKEN";
	private static final String READY_FOR_CHECKING = "READY_FOR_CHECKING";
	private static final String WAITING_FOR_PRIOR_CHECKER = "WAITING_FOR_PRIOR_CHECKER";
	private static final String ACTION_REVIEWED = "ACTION_REVIEWED";
	private static final String ACTION_REVIEWED_BY = "ACTION_REVIEWED_BY : ";
	private static final String ACTION_REVIEWED_BEFORE = "ACTION_REVIEWED_BEFORE";
	
	private static final String SECURE_PREFIX = ")]}',\n";

	public McOperationServiceImpl() {
	}

	/**
	 * Persist a McOperation entity .
	 *
	 * @param McOperationVO
	 *            ValueObject.
	 *
	 * @return 0000 - if Success
	 *
	 * @throws OurException
	 *             No.0024 If ever the object already exists.
	 *
	 */
	public String createMcOperationService(ServiceContext ctx, McOperationVO mcOperationVO) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:createMcOperationService , USER :" + ctx.getUserId() + " , SessionID :"
					+ sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		McOperation mcOperation = new McOperation();

		
		if (mcOperationVO.getMc_operationpar_fk() != null) {
			mcOperation.setMc_operationpar_id(new McOperationPar(mcOperationVO.getMc_operationpar_fk()));

		} else {
		}

		mcOperation.setId_maker(ctx.getUserId());
		// mcOperation.setId_maker(mcOperationVO.getId_maker());

		mcOperation.setStatus("O");

		mcOperation.setCreation_date(new Date());

		//mcOperation.setCreation_date(mcOperationVO.getCreation_date());

		mcOperation.setVo(mcOperationVO.getVo());


		mcOperation.setScreen_config(mcOperationVO.getScreen_config());

		mcOperation.setScreen_url(mcOperationVO.getScreen_url());

		mcOperation.setMc_comment(mcOperationVO.getMc_comment());


		McOperationPar mc_par = mcOperationParService.findById(ctx, mcOperationVO.getMc_operationpar_fk());
		//Check if mc_operationpar require_snap
		if(mc_par.getRequire_snap().equals("Y")) {
			
			mcOperation.setOld_snapshot(mcOperationVO.getOld_snapshot());

			mcOperation.setNew_snapshot(mcOperationVO.getNew_snapshot());
		} else {
			String mergedSnapshots = mergeJsonStrings(mcOperationVO.getOld_snapshot(),
					mcOperationVO.getNew_snapshot());

			mcOperation.setOld_snapshot(mergedSnapshots);
	
			mcOperation.setNew_snapshot(mergedSnapshots);
		}	
		if(mc_par.getUse_storedKey() != null && mc_par.getUse_storedKey().equals("Y")) {
			mcOperation.setStoredKey(ctx.getStoredKey());
		}
		
		McOperation mcOperation1 = this.getMcOperationRepository().save(mcOperation);

		return "0000";

	}

	/**
	 * update a McOperation entity .
	 *
	 * @param McOperationVO
	 *            ValueObject.
	 *
	 * @return 0000 - if Success
	 *
	 * @throws OurException
	 *             No.0001 If the object don't exists.
	 *
	 */
	public String updateMcOperationService(ServiceContext ctx, McOperationVO mcOperationVO) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:updateMcOperationService , USER :" + ctx.getUserId() + " , SessionID :"
					+ sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		McOperation mcOperation = null;

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(McOperationProperties.mc_operation_id(), mcOperationVO.getMc_operation_id()));

		PagingParameter pagingParameter = PagingParameter.pageAccess(1, 1, false);
		PagedResult<McOperation> pagedResult = this.getMcOperationRepository().findByCondition(con, pagingParameter);
		List<McOperation> list = pagedResult.getValues();

		if (list.size() > 0) {
		
			mcOperation = list.get(0);
		} else {
			throw new OurException("0001", new McOperationNotFoundException(""));
		}

		if (mcOperationVO.getMc_operationpar_fk() != null) {
			mcOperation.setMc_operationpar_id(new McOperationPar(mcOperationVO.getMc_operationpar_fk()));

		} else {
		}

		if (mcOperationVO.getId_maker() != null) {
			mcOperation.setId_maker(mcOperationVO.getId_maker());
		}

		if (mcOperationVO.getStatus() != null) {
			mcOperation.setStatus(mcOperationVO.getStatus());
		}

		if (mcOperationVO.getCreation_date() != null) {
			mcOperation.setCreation_date(mcOperationVO.getCreation_date());
		}

		if (mcOperationVO.getVo() != null) {
			mcOperation.setVo(mcOperationVO.getVo());
		}

		if (mcOperationVO.getOld_snapshot() != null) {
			mcOperation.setOld_snapshot(mcOperationVO.getOld_snapshot());
		}

		if (mcOperationVO.getNew_snapshot() != null) {
			mcOperation.setNew_snapshot(mcOperationVO.getNew_snapshot());
		}

		if (mcOperationVO.getScreen_config() != null) {
			mcOperation.setScreen_config(mcOperationVO.getScreen_config());
		}

		if (mcOperationVO.getScreen_url() != null) {
			mcOperation.setScreen_url(mcOperationVO.getScreen_url());
		}

		if (mcOperationVO.getId_checker() != null) {
			mcOperation.setId_checker(mcOperationVO.getId_checker());
		}

		if (mcOperationVO.getMc_comment() != null) {
			mcOperation.setMc_comment(mcOperationVO.getMc_comment());
		}

		McOperation mcOperation1 = this.getMcOperationRepository().save(mcOperation);

		return "0000";

	}

	/**
	 * delete a McOperation entity .
	 *
	 * @param McOperationVO
	 *            ValueObject.
	 *
	 * @return 0000 - if Success
	 *
	 * @throws OurException
	 *             No.0001 If the object don't exists.
	 *
	 */
	public String deleteMcOperationService(ServiceContext ctx, McOperationVO mcOperationVO) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:deleteMcOperationService , USER :" + ctx.getUserId() + " , SessionID :"
					+ sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		McOperation mcOperation = null;

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(McOperationProperties.mc_operation_id(), mcOperationVO.getMc_operation_id()));

		PagingParameter pagingParameter = PagingParameter.pageAccess(1, 1, false);
		PagedResult<McOperation> pagedResult = this.getMcOperationRepository().findByCondition(con, pagingParameter);
		List<McOperation> list = pagedResult.getValues();

		if (list.size() > 0) {
			mcOperation = list.get(0);
		} else {
			throw new OurException("0001", new McOperationNotFoundException(""));
		}

		if (mcOperationVO.getMc_operationpar_fk() != null) {
			mcOperation.setMc_operationpar_id(new McOperationPar(mcOperationVO.getMc_operationpar_fk()));

		} else {
		}

		this.getMcOperationRepository().delete(mcOperation);

		return "0000";

	}

	/**
	 * decline a McOperation entity .
	 *
	 * @param McOperationVO
	 *            ValueObject.
	 *
	 * @return 0000 - if Success
	 *
	 * @throws OurException
	 *             No.0001 If the object don't exists.
	 *
	 */
	public String declineMcOperationService(ServiceContext ctx, McOperationVO mcOperationVO) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:declineMcOperationService , USER :" + ctx.getUserId()
					+ " , SessionID :" + sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		McOperation mcOperation = null;

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		con.add(ConditionalCriteria.equal(McOperationProperties.mc_operation_id(), mcOperationVO.getMc_operation_id()));

		PagingParameter pagingParameter = PagingParameter.pageAccess(1, 1, false);
		PagedResult<McOperation> pagedResult = this.getMcOperationRepository().findByCondition(con, pagingParameter);
		List<McOperation> list = pagedResult.getValues();

		if (list.size() > 0) {
			
			mcOperation = list.get(0);
		} else {
			throw new OurException("0001", new McOperationNotFoundException(""));
		}

		if (mcOperationVO.getMc_operationpar_fk() != null) {
			mcOperation.setMc_operationpar_id(new McOperationPar(mcOperationVO.getMc_operationpar_fk()));

		} else {
		}

		mcOperation.setMc_comment(mcOperationVO.getMc_comment());
		mcOperation.setStatus("D");
		mcOperation.setId_checker(ctx.getUserId());
		mcOperation.setDecision_date(new Date());
		McOperation mcOperation1 = this.getMcOperationRepository().save(mcOperation);
		
		//Storing EscHistory
		if(mcOperationVO.getTimeZone() != null) {
			mcOperationVO.setId_checker(ctx.getUserId());
			storeEscHistory(ctx, mcOperationVO, mcOperation1.getCreation_date(),mcOperation1.getDecision_date());
		}
		
		return "0000";

	}
	
	private void storeEscHistory(ServiceContext ctx, McOperationVO mcOperationVO, Date cr_date, Date dc_date) throws Exception{
		
		String clientTimeZone = mcOperationVO.getTimeZone();
		String serverTimeZone = java.util.TimeZone.getDefault().getID();
			
		LocalDateTime dt = LocalDateTime.now();
        ZonedDateTime fromZonedDateTime = dt.atZone(ZoneId.of(clientTimeZone));
        ZonedDateTime toZonedDateTime = dt.atZone(ZoneId.of(serverTimeZone));
        long diff = Duration.between(fromZonedDateTime, toZonedDateTime).toMinutes();
        
        Calendar cal = Calendar.getInstance();
        
        cal.setTime(cr_date);
    	cal.add(Calendar.MINUTE, (int)diff);
    	mcOperationVO.setCreation_date(cal.getTime());
    	
		cal.setTime(dc_date);
		cal.add(Calendar.MINUTE, (int)diff);
		mcOperationVO.setDecision_date(cal.getTime());
		
		setEscHistory(mcOperationVO, ctx);	
	}
	
	private static RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
	
//		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		TrustStrategy acceptingTrustStrategy = new TrustSelfSignedStrategy();

		SSLContext sslContext = SSLContexts.custom()
										   .loadTrustMaterial(null, acceptingTrustStrategy)
										   .build();

//		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

		HttpComponentsClientHttpRequestFactory requestFactory =	new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}
	
	/**
	 * accept a McOperation entity .
	 *
	 * @param McOperationVO
	 *            ValueObject.
	 *
	 * @return result of called service (mcOperation url) - if Success
	 * 
	 * @throws OurException
	 *             No.0001 If the object don't exists.
	 *
	 * @throws OurException
	 *             No.0002 If runtime error when calling service operation
	 *
	 */
	public String acceptMcOperationService(ServiceContext ctx, McOperationVO mcOperationVO, String jwt, String baseUri)
			throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:acceptMcOperationService , USER :" + ctx.getUserId() + " , SessionID :"
					+ sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		McOperation mcOperation = this.getMcOperationRepository().findById(mcOperationVO.getMc_operation_id());

		if (mcOperationVO.getMc_operationpar_fk() != null) {
			mcOperation.setMc_operationpar_id(new McOperationPar(mcOperationVO.getMc_operationpar_fk()));

		} else {
		}

		McOperationPar mcOperationPar = mcOperationParService.findById(ctx,
				mcOperation.getMc_operationpar_id().getMc_operationpar_id());
		
		if(mcOperation.getScreen_config() != null) {
			
			JsonObject config = new Gson().fromJson(mcOperation.getScreen_config(), JsonObject.class);
			String expected = config.get("expected").toString();

			String result = this.checkMcValidation(ctx, config, jwt, baseUri);
			
			if(!expected.equals(result)) {
				throw new OurException(result+"!="+expected,
						new Exception("checkMcValidation didn't return the expected value ( "+expected+" )"));
			}
		}
		
		RestTemplate client = restTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(jwt);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("jwt", jwt);
		if(mcOperationPar.getSpecific_vo_name() != null ) {
			params.add(mcOperationPar.getSpecific_vo_name(), mcOperation.getVo());
		} else {
			params.add(StringUtils.uncapitalize(mcOperationPar.getScreen()) + "VO", mcOperation.getVo());
		}
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params, headers);

		String checkerStoredKey = null;
		if(mcOperationPar.getUse_storedKey() != null && mcOperationPar.getUse_storedKey().equals("Y")) {
			if(mcOperation.getStoredKey() != null && !mcOperation.getStoredKey().equals("")) {
				checkerStoredKey = ctx.getStoredKey();
				ctx.setStoredKey(mcOperation.getStoredKey());
			}
		}
		
		ResponseEntity<String> response = client.postForEntity(baseUri + mcOperationPar.getOperation_url(), request, String.class);

		if(checkerStoredKey != null) ctx.setStoredKey(checkerStoredKey);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			throw new OurException("0002", new Exception("acceptMcOperationService : RuntimeException when calling "
					+ mcOperationPar.getOperation_url() + " HTTP error code : " + response.getStatusCodeValue()));
		}

		String output = escapeResponse(response.getBody(), SECURE_PREFIX);

		JsonObject jsonResponse = new Gson().fromJson(output, JsonObject.class);

		if (jsonResponse.get("EXCEPTION").getAsBoolean()) {
			throw new OurException(jsonResponse.get("RESULT").getAsString(),
					new Exception("acceptMcOperationService (operation : " + mcOperationPar.getOperation_url()
							+ ") couldn't be performed"));
		} else {
			mcOperation.setStatus("A");
			mcOperation.setMc_comment(mcOperationVO.getMc_comment());
			mcOperation.setId_checker(ctx.getUserId());
			mcOperation.setDecision_date(new Date());
			McOperation mcOperation1 = this.getMcOperationRepository().save(mcOperation);
			
			//Storing EscHistory
			if(mcOperationVO.getTimeZone() != null) {
				mcOperationVO.setId_checker(ctx.getUserId());
				storeEscHistory(ctx, mcOperationVO, mcOperation1.getCreation_date(),mcOperation1.getDecision_date());
			}
			
			String result = jsonResponse.get("RESULT").getAsString();
			//result = result.replaceAll("[^0-9]", "");
			return result.replaceAll("[\"\\\\]", "");
		}
	}

	public String checkMcValidation(ServiceContext ctx, JsonObject config, String jwt, String baseUri) throws Exception {
		
		String service = config.get("service").getAsString();//toString().replaceAll("^\"|\"$", "");
		String vo = config.get("vo").getAsString();//.toString().replaceAll("^\"|\"$", "");
		String payload = config.get("payload").toString();

		RestTemplate client = restTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setBearerAuth(jwt);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("jwt", jwt);
		params.add(vo, payload);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params, headers);
		
		String url = baseUri + service;
		ResponseEntity<String> response = client.postForEntity(url, request, String.class);

		if (response.getStatusCode() != HttpStatus.OK) {
			throw new OurException("0002", new Exception("checkMcValidation : RuntimeException when calling "
					+service + " HTTP error code : " + response.getStatusCodeValue()));
		}

		String output = escapeResponse(response.getBody(), SECURE_PREFIX);
		
		JsonObject jsonResponse = new Gson().fromJson(output, JsonObject.class);

		if (jsonResponse.get("EXCEPTION").getAsBoolean()) {
			throw new OurException(jsonResponse.get("RESULT").getAsString(),
					new Exception("checkMcValidation couldn't be performed"));
		}

		String result = jsonResponse.get("RESULT").getAsString();
		return result;//.replaceAll("[\"\\\\]", "");
				
	}
	/**
	 * check McOperation entity .
	 *
	 * @param McOperationVO
	 *            ValueObject.
	 * 
	 * @param Map<Long,List<McFieldCriteriaVO>>.
	 * 
	 * @param int
	 *            numRestrictedFields.
	 *
	 * @return 1111 - no mcField violation - should continue
	 * 
	 */
	public String checkMcFieldCriteriaService(ServiceContext ctx, McOperationVO mcOperationVO,
			Map<Long, List<McFieldCriteriaVO>> hm, int numRestrictedFields) throws Exception {

		JsonObject jsonMcOpeVO = new Gson().fromJson(mcOperationVO.getVo(), JsonObject.class);
		Boolean violation = false;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		if (numRestrictedFields > 0) {

			for (Long key : hm.keySet()) {
				List<McFieldCriteriaVO> mcFieldCriterias = hm.get(key);
				if (mcFieldCriterias.size() > 0) {
					violation = false;
					for (McFieldCriteriaVO mcFieldCriteria : mcFieldCriterias) {
						String operator = mcFieldCriteria.getOperator().toString();
						String type = mcFieldCriteria.getField_type().toString().toLowerCase();
						int compare = 0;
						
						if(type.equals("date")) {
							Date voFieldVal = formatter.parse(jsonMcOpeVO.get(mcFieldCriteria.getField_key()).getAsString());
							Date operand = formatter.parse(mcFieldCriteria.getOperand());
							compare = voFieldVal.compareTo(operand);		
						
						} else if(type.equals("number")) {
							Double voFieldVal = jsonMcOpeVO.get(mcFieldCriteria.getField_key()).getAsDouble();
							Double operand = Double.parseDouble(mcFieldCriteria.getOperand());
							compare = voFieldVal.compareTo(operand);
						} else {
							String voFieldVal = jsonMcOpeVO.get(mcFieldCriteria.getField_key()).getAsString();
							String operand = mcFieldCriteria.getOperand().toString();
							compare = voFieldVal.compareTo(operand);
						}

						violation = (operator.equals("=") && compare == 0) || (operator.equals("!=") && compare != 0) 
				        		|| (operator.equals(">") && compare>0) || (operator.equals("<") && compare<0);
						
						if (!violation)
							break;
					}
					if (violation) {
						mcOperationVO.setMc_operationpar_fk(key);
						//if(mcOperationVO.getScreen_config() != null) {
						//	return checkMcValidation(ctx, mcOperationVO);
						//}
						return this.createMcOperationService(ctx, mcOperationVO);
					}
				}
			}
			return "1111";

		} else {

			return this.createMcOperationService(ctx, mcOperationVO);
		}
	}

	/**
	 * check McOperation entity .
	 *
	 * @param McOperationVO
	 *            ValueObject.
	 *
	 * @return 0000 - add mcOperation : if doesn't exists or exists but no
	 *         longer locked
	 *
	 * @return 0001 - mcOperation exists and locked
	 *
	 * @throws OurException
	 *             No.0001 If the object don't exists.
	 *
	 */
	public String checkMcOperationService(ServiceContext ctx, McOperationVO mcOperationVO) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:checkMcOperationService , USER :" + ctx.getUserId() + " , SessionID :"
					+ sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		McOperation mcOperation = null;

		McOperationPar mcOperationPar = mcOperationParService.findById(ctx, mcOperationVO.getMc_operationpar_fk());
		List<McFieldCriteriaVO> mcFieldCriterias = new ArrayList<>();

		Map<Long, List<McFieldCriteriaVO>> hm = new HashMap<Long, List<McFieldCriteriaVO>>();
		int numRestrictedFields = 0;

		McOperationParVO mcOperationParVO = new McOperationParVO();
		mcOperationParVO.setOperation_url(mcOperationPar.getOperation_url());
      	mcOperationParVO.setEnabled("Y");
		mcOperationParVO.setLazy_level_col(1);
		List<McOperationParVO> pars = mcOperationParService.searchMcOperationParService(ctx, mcOperationParVO);

		if (pars.size() > 0) {
			boolean hasSpecificKeys = false;
			for (McOperationParVO mcOperationParVOTmp : pars) {
				mcFieldCriterias = new ArrayList(mcOperationParVOTmp.getMcFieldCriterias_col());
				numRestrictedFields += mcFieldCriterias.size();
				hm.put(mcOperationParVOTmp.getMc_operationpar_id(), mcFieldCriterias);
				
				//Should add flag to mcOperationPar
				if(mcOperationParVOTmp.getSpecific() != null && mcOperationParVOTmp.getSpecific().equals("Y")) {
					if(mcOperationParVOTmp.getOperation_keys().contains("[") && mcOperationParVOTmp.getOperation_keys().contains("]")) {
						hasSpecificKeys = true;
					}
				}
			}
			
			if(hasSpecificKeys) {
				return checkSpecificMcOperationService(ctx,mcOperationVO,mcOperationPar,hm);
			}
		}
		con.add(ConditionalCriteria.equal(McOperationProperties.mc_operationpar_id().operation_url(),
				mcOperationParVO.getOperation_url()));

		con.add(ConditionalCriteria.equal(McOperationProperties.status(), "O"));

		List<McOperation> list = this.getMcOperationRepository().findByCondition(con);

		if (list.size() > 0) {
			JsonObject jsonMcOpeVO = new Gson().fromJson(mcOperationVO.getVo(), JsonObject.class);

			String[] keys = null;
			if (mcOperationPar.getOperation_keys().contains(";")) {
				keys = mcOperationPar.getOperation_keys().split(";");
			} else {
				String key = mcOperationPar.getOperation_keys();
				keys = key != null && !key.equals("") ? new String[] { key } : null;
			}

			boolean same = false;

			for (McOperation mcOperationTmp : list) {
				JsonObject jsonMcOpeVOTmp = new Gson().fromJson(mcOperationTmp.getVo(), JsonObject.class);
				if (keys != null) {
					for (String key : keys) {
						same = jsonMcOpeVO.get(key).toString().equals(jsonMcOpeVOTmp.get(key).toString());
						if(!same) break;
					}
				} else {
					same = jsonMcOpeVO.equals(jsonMcOpeVOTmp);
				}

				if (same) {
					mcOperation = mcOperationTmp;
					break;
				}
			}
			if (!same) {
				return this.checkMcFieldCriteriaService(ctx, mcOperationVO, hm, numRestrictedFields);

			}
		} else {
			return this.checkMcFieldCriteriaService(ctx, mcOperationVO, hm, numRestrictedFields);
		}

		if (mcOperation.getStatus() != null && !mcOperation.getStatus().equals("O")) {
			return this.checkMcFieldCriteriaService(ctx, mcOperationVO, hm, numRestrictedFields);
		}

		// VO Locked!
		return "0001";
	}
	
	
	
	private boolean checkSpecificMcFieldCriteria(McOperationVO mcOperationVO, List<McFieldCriteriaVO> fieldCriterias) throws ParseException {
		
		boolean violation = false;
		String operator,field_key,field_type;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		JsonObject jsonMcOpeVO = new Gson().fromJson(mcOperationVO.getVo(), JsonObject.class);

		for (McFieldCriteriaVO mcFieldCriteriaVO : fieldCriterias) {
			operator = mcFieldCriteriaVO.getOperator();
			field_key = mcFieldCriteriaVO.getField_key();
			field_type = mcFieldCriteriaVO.getField_type().toLowerCase();
			if(!field_key.contains("[")) {
				
				if(field_type.equalsIgnoreCase("array") && operator.equals("null")) {
					String operand = mcFieldCriteriaVO.getOperand();
					violation = operand.equalsIgnoreCase("true") ? jsonMcOpeVO.get(field_key).isJsonNull() 
							  : !jsonMcOpeVO.get(field_key).isJsonNull();
				} else {
					
					int compare = 0;
					
					if(field_type.equals("date")) {
						Date voFieldVal = formatter.parse((jsonMcOpeVO.get(field_key)).getAsString());
						Date operand = formatter.parse(mcFieldCriteriaVO.getOperand());
						compare = voFieldVal.compareTo(operand);		
					
					} else if(field_type.equals("number")) {
						Double voFieldVal = jsonMcOpeVO.get(field_key).getAsDouble();
						Double operand = Double.parseDouble(field_key);
						compare = voFieldVal.compareTo(operand);
					} else {
						String voFieldVal = jsonMcOpeVO.get(field_key).getAsString();
						String operand = mcFieldCriteriaVO.getOperand().toString();
						compare = voFieldVal.compareTo(operand);
					}
	
					violation = (operator.equals("=") && compare == 0) || (operator.equals("!=") && compare != 0) 
			        		|| (operator.equals(">") && compare>0) || (operator.equals("<") && compare<0);
					
				}
				
				if(!violation) break;
			
			}
			
		}
		
		return violation;
		
	}

	//Specific MakerChecker calls goes here
	private String checkSpecificMcOperationService(ServiceContext ctx, McOperationVO mcOperationVO,
			McOperationPar mcOperationPar, Map<Long, List<McFieldCriteriaVO>> hm) throws Exception {

		String result = null;
		Long par_id = null;
		for (Map.Entry<Long, List<McFieldCriteriaVO>> entry : hm.entrySet()) {

			if(checkSpecificMcFieldCriteria(mcOperationVO,entry.getValue())) {
				par_id = entry.getKey();
				break;
			}
		}
		
		//Check firstCondition to see if should intercept at all or not!
		boolean shouldIntercept = false;
		
//		Set<McFieldCriteria> fielCriterias = mcOperationPar.getMcFieldCriterias();
//		for (McFieldCriteria mcFieldCriteria : fielCriterias) {
//			if(mcFieldCriteria.getField_type().equalsIgnoreCase("special")) {
//				shouldIntercept = shouldInterceptSpecific(mcOperationVO,mcFieldCriteria);
//				if(!shouldIntercept) break;
//			}
//		}
		
		if(par_id != null) {
			mcOperationPar = mcOperationParService.findById(ctx, par_id);
			mcOperationVO.setMc_operationpar_fk(par_id);
			Gson gson = new Gson();
			Pattern pattern = Pattern.compile("\\[(.*?)\\]");
			
			//Operation keys 
			String op_keys[] = null;
			String pr_keys[] = null;
			String listKey = null;
			String childKey = null;
			if (mcOperationPar.getOperation_keys().contains(";")) {
				op_keys = mcOperationPar.getOperation_keys().split(";");
			}
			if(op_keys != null) {
				pr_keys = Arrays.stream(op_keys).filter(k -> !pattern.matcher(k).find()).toArray(String[]::new);
			
				for (String k : op_keys) {
					Matcher m = pattern.matcher(k);
					if(m.find()) {
						listKey = k.substring(0,k.indexOf("["));
						childKey = m.group(1);
					}
				}
			}
			//Splitting to several operations
			JsonObject jsonMcOpVo = new Gson().fromJson(mcOperationVO.getVo(), JsonObject.class);
			if(jsonMcOpVo.has(listKey)) {
				JsonArray parentList = gson.fromJson(jsonMcOpVo.get(listKey), JsonArray.class);

				JsonArray vos = new JsonArray();

				if(parentList.size() > 0) {
					for (int i = 0; i < parentList.size(); i++) {
						//cloning vo
						JsonObject obj = gson.fromJson(gson.toJson(jsonMcOpVo, JsonObject.class), JsonObject.class);
						//getting one child per vo
						JsonObject childObj = parentList.get(i).getAsJsonObject();
						String parentNewArray = "["+childObj.toString()+"]";
						
						obj.add(listKey,gson.fromJson(parentNewArray, JsonArray.class));
						vos.add(obj);
					}
				}
			
			//Looking for opened operations
			List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();
			
			con.add(ConditionalCriteria.equal(McOperationProperties.mc_operationpar_id().operation_url(),
					mcOperationPar.getOperation_url()));

			con.add(ConditionalCriteria.equal(McOperationProperties.status(), "O"));

			List<McOperation> list = this.getMcOperationRepository().findByCondition(con);

			
			if (vos.size() > 0) {
				
				HashMap<String, String> msg_vos = new HashMap<>();
				
		        McFieldCriteria childFieldCriteria = null;
		        for (McFieldCriteria mcFieldCriteria : mcOperationPar.getMcFieldCriterias()) {
					Matcher m = pattern.matcher(mcFieldCriteria.getField_key());
					if(m.find()) {
						childFieldCriteria = mcFieldCriteria;
						break;
					}
				}
		        
				for (JsonElement vo : vos) {
					Boolean lockedVO = false;
					String childValue = vo.getAsJsonObject().get(listKey)
							.getAsJsonArray().get(0).getAsJsonObject().get(childKey).getAsString();
					
					msg_vos.put(childValue, "0000");
					
					if(list.size() > 0) {
						for (McOperation tmpMcOperation : list) {
							JsonObject tmpJsonMcOpeVO = new Gson().fromJson(tmpMcOperation.getVo(), JsonObject.class);
							Boolean same = false;
							//verifying primary keys first
							for(String pr_key : pr_keys) {
								same = jsonMcOpVo.get(pr_key).getAsString().equals(tmpJsonMcOpeVO.get(pr_key).getAsString());
								if(!same) break;
							}
							if(same) {
								JsonArray tmpParentList = gson.fromJson(tmpJsonMcOpeVO.get(listKey), JsonArray.class);
								if(tmpParentList.size() > 0) {
									JsonObject tmpChildObj = tmpParentList.get(0).getAsJsonObject();
									String tmpChildValue = tmpChildObj.get(childKey).getAsString();
									
									if(childValue.equals(tmpChildValue)) {
										//same in primary keys and in childValue
										lockedVO = true;
										break;
									}
								}
								
							}
							
						}
					}
					if(!lockedVO) {//should create operation for it 
						//direct creation 
						McOperationVO newOp = spreadVO(mcOperationVO);
						newOp.setVo(vo.toString());
						String msg = null;
						if(childFieldCriteria != null) {
							String field_k = childFieldCriteria.getField_key();
							Matcher m = pattern.matcher(field_k);
							if(m.find()) {
								String parentListKey = field_k.substring(0,field_k.indexOf("["));
								String childFieldKey = m.group(1);
								
								String operator = childFieldCriteria.getOperator();
								String operand = childFieldCriteria.getOperand();
								String type = childFieldCriteria.getField_type();
								if(type.equalsIgnoreCase("string")) {
									int compare = vo.getAsJsonObject().get(parentListKey).getAsJsonArray().get(0).getAsJsonObject()
											.get(childFieldKey).getAsString().compareTo(operand);
									
									Boolean violation = (operator.equals("=") && compare == 0) || (operator.equals("!=") && compare != 0);
									if(violation) {
										msg = createMcOperationService(ctx, newOp);
									} else {
										msg = "1111";
									}
								}
							}
						} else {
							msg = createMcOperationService(ctx, newOp);
						}
//						if(!vo.getAsJsonObject().get(listKey).getAsJsonArray().get(0).getAsJsonObject().get("operation").getAsString().equals("X")) {
//							msg = createMcOperationService(ctx, newOp);
//						}
						
						//msg = createMcOperationService(ctx, newOp);
						msg_vos.put(childValue, msg);
						
					} else {//lockedVO
						msg_vos.put(childValue, "0001");
					}
					
				}
				
				result = "[";
		        
		        for (Map.Entry entry : msg_vos.entrySet()) {
		        	result+=entry.getKey()+":"+entry.getValue()+",";
		          }
		        result=result.substring(0, result.length() - 1);
		        result+="]";
		        
				//result = msg_vos.toString();
				} else {
					result = "should have vos";
				}
			} else {
				result = "should have "+listKey;
			}
			
			return result;
			
		} else {//should'nt intercept at all
			return "1111";
		}
	}

	private boolean shouldInterceptSpecific(McOperationVO mcOperationVO, McFieldCriteria mcFieldCriteria) {
		
		Boolean violation = false;

		String operator = mcFieldCriteria.getOperator().toString();
		String operand = mcFieldCriteria.getOperand().toString();
		
		JsonObject jsonMcOpeVO = new Gson().fromJson(mcOperationVO.getVo(), JsonObject.class);
				
		String voFieldVal = jsonMcOpeVO.get(mcFieldCriteria.getField_key()).getAsString();
		
		int compare = voFieldVal.compareTo(operand);
	
		violation = (operator.equals("=") && compare == 0) || (operator.equals("!=") && compare != 0) ;
		
		return violation;
	}

	/**
	 * checkMcParDependencyService
	 *
	 * @return 0001 - vo locked (parent under processing)
	 *
	 * @return 1111 - parent operations exists but all different to this vo or none found
	 *
	 *
	 */
	public String checkMcParDependencyService(ServiceContext ctx, McOperationVO mcOperationVO) throws Exception {
		
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:checkMcParDependencyService , USER :" + ctx.getUserId() + " , SessionID :"
					+ sessionID + " , RemoteAddress:" + remoteAddress);
		}

		ServiceContextStore.set(ctx);

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		McOperationPar mcOperationPar = null;
		
		McParDependency mcParDependency = mcParDependencyService.findById(ctx, mcOperationVO.getMc_operationpar_fk());
		
		if(mcParDependency.getFk_mc_operationpar_id()!=null) {
			mcOperationPar = mcOperationParService.findById(ctx, mcParDependency.getFk_mc_operationpar_id().getMc_operationpar_id());

			if(mcOperationPar.getOperation_url() != null) {
				
				con.add(ConditionalCriteria.equal(McOperationProperties.mc_operationpar_id().operation_url(),
						mcOperationPar.getOperation_url()));

				con.add(ConditionalCriteria.equal(McOperationProperties.status(), "O"));

				List<McOperation> list = this.getMcOperationRepository().findByCondition(con);
				
				if (list.size() > 0) {//parent operations exists
					
					//check if vo locked
					JsonObject jsonMcOpeVO = new Gson().fromJson(mcOperationVO.getVo(), JsonObject.class);

					String[] keys = null;
					if (mcOperationPar.getOperation_keys().contains(";")) {
						keys = mcOperationPar.getOperation_keys().split(";");
					}

					boolean same = false;

					for (McOperation mcOperationTmp : list) {
						JsonObject jsonMcOpeVOTmp = new Gson().fromJson(mcOperationTmp.getVo(), JsonObject.class);
						if (keys != null) {
							for (String key : keys) {
								if (jsonMcOpeVO.get(key).toString().equals(jsonMcOpeVOTmp.get(key).toString())) {
									same = true;
									break;
								}
							}
						} else {
							same = jsonMcOpeVO.equals(jsonMcOpeVOTmp);
						}

						if (same) {//vo locked
							return "0001";
						}
					}
					//parent operations exists but all different to this vo
					return "1111";
					
				} else {//no parent operation under processing
					return "1111";
				}

			}
		} else {
			throw new OurException("0001", new McParDependencyNotFoundException(""));
		}
		return "1111";
	}
	
	/**
	 * Find all entities of a specific type .
	 *
	 * @return List of McOperationVO
	 *
	 */
	public List<McOperationVO> getAllMcOperationService(ServiceContext ctx) throws Exception {
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:getAllMcOperationService , USER :" + ctx.getUserId() + " , SessionID :"
					+ sessionID + " , RemoteAddress:" + remoteAddress);
		}

		List<McOperationVO> l = new ArrayList<McOperationVO>();

		List<McOperation> l_entity = new ArrayList<McOperation>();

		l_entity = this.getMcOperationRepository().findAll();

		l = McOperationUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

		return l;

	}

	/**
	 * Find entities by conditions
	 *
	 * @return List of McOperationVO
	 *
	 */
	public List<McOperationVO> searchMcOperationService(ServiceContext ctx, McOperationVO mcOperationVO)
			throws Exception {		
		String sessionID = null;
		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:searchMcOperationService , USER :" + ctx.getUserId() + " , SessionID :"
					+ sessionID + " , RemoteAddress:" + remoteAddress);
		}

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		if (ctx != null) {
			McOperationUtils.dataFilter(ctx, con);
		}
	    List<String> dataAccessBanks =  (List<String>) ctx.getProperty("bankDataAccess");
        if(dataAccessBanks!=null && !dataAccessBanks.isEmpty())
        {
        	con.add(ConditionalCriteria.in(
        			McOperationProperties.mc_operationpar_id()
                    .bank_code(),
                    dataAccessBanks));
        	
        }
      
		if(mcOperationVO.getOp_name() != null && !mcOperationVO.getOp_name().equals("")) {
			
			con.add(ConditionalCriteria.equal(McOperationProperties.mc_operationpar_id()
			                    .operation_name(), mcOperationVO.getOp_name()));
			
			/* to verify if should include only enabled operation_pars
			 * con.add(ConditionalCriteria.equal(McOperationProperties.mc_operationpar_id()
             *      .enabled(), "Y"));
			 */
			mcOperationVO.setMc_operationpar_fk(null);
		}
		
		if(mcOperationVO.getOp_bank() != null && !mcOperationVO.getOp_bank().equals("")) {
			
			con.add(ConditionalCriteria.equal(McOperationProperties.mc_operationpar_id()
			                    .bank_code(), mcOperationVO.getOp_bank()));
		}
		McOperationUtils.setListOfCriteria(ctx, con, mcOperationVO);

		List<McOperation> l_entity = new ArrayList<McOperation>();
		List<McOperationVO> l = new ArrayList<McOperationVO>();

		int page = mcOperationVO.getPage();
		int pageSize = mcOperationVO.getPageSize();
		boolean countTotalPages = true;
		if (page > 0 && pageSize > 0) {
			PagingParameter pagingParameter = PagingParameter.pageAccess(pageSize, page, countTotalPages);

			PagedResult<McOperation> pagedResult = this.getMcOperationRepository().findByCondition(con,
					pagingParameter);

			l_entity = pagedResult.getValues();

			ctx.setProperty("pagedResult", pagedResult);

		} else {
			l_entity = this.getMcOperationRepository().findByCondition(con);
		}

		l = McOperationUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);

		
		if(mcOperationVO.getTimeZone() != null) {
			List<McOperationVO> l_vos_decal = new ArrayList<McOperationVO>();
			
			String clientTimeZone = mcOperationVO.getTimeZone();
			String serverTimeZone = java.util.TimeZone.getDefault().getID();
			
			LocalDateTime dt = LocalDateTime.now();
	        ZonedDateTime fromZonedDateTime = dt.atZone(ZoneId.of(clientTimeZone));
	        ZonedDateTime toZonedDateTime = dt.atZone(ZoneId.of(serverTimeZone));
	        long diff = Duration.between(fromZonedDateTime, toZonedDateTime).toMinutes();
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(new Date());
        	cal.add(Calendar.MINUTE, (int)diff);
        	Date now = cal.getTime();
        	
	        for(int i=0; i<l.size();i++) {
	        	
	        	McOperationVO mcOpVo = spreadVO(l.get(i));	        	
	        	
	        	cal.setTime(mcOpVo.getCreation_date());
	        	cal.add(Calendar.MINUTE, (int)diff);
	        	mcOpVo.setCreation_date(cal.getTime());
	        	
	        	if(mcOpVo.getStatus() != null && !mcOpVo.getStatus().equalsIgnoreCase("O")
	        			&& mcOpVo.getDecision_date() != null) {
	        		
	        		cal.setTime(mcOpVo.getDecision_date());
	        		cal.add(Calendar.MINUTE, (int)diff);
		        	mcOpVo.setDecision_date(cal.getTime());
	        	}
	        	l_vos_decal.add(mcOpVo);
	        }
	        
	        return getMyMcOperationService(ctx, l_vos_decal,now);
		}
		
		return getMyMcOperationService(ctx, l, new Date());

	}
	McOperationVO spreadVO(McOperationVO vo){
		McOperationVO e = new McOperationVO();

        e.setMc_operation_id(vo.getMc_operation_id());

        e.setId_maker(vo.getId_maker());

        e.setId_checker(vo.getId_checker());

        e.setStatus(vo.getStatus());

        e.setCreation_date(vo.getCreation_date());

        e.setDecision_date(vo.getDecision_date());

        e.setVo(vo.getVo());

        e.setOld_snapshot(vo.getOld_snapshot());

        e.setNew_snapshot(vo.getNew_snapshot());

        e.setScreen_config(vo.getScreen_config());

        e.setScreen_url(vo.getScreen_url());

        e.setMc_comment(vo.getMc_comment());
        
        if (vo.getMc_operationpar_fk() != null) {
        	
        	e.setMc_operationpar_fk(vo.getMc_operationpar_fk());
        }
        if(vo.getEsc_history() != null) {
        	e.setEsc_history(vo.getEsc_history());
        }
        
        if (vo.getOp_name() != null && !vo.getOp_name().equals("")) {
        	e.setOp_name(vo.getOp_name());
        }
        
        return e;
	}
	/**
	 * Find entities by conditions
	 *
	 * @return List of McOperationVO
	 *
	 */
	public List<McOperationVO> getMyMcOperationService(ServiceContext ctx, List<McOperationVO> list, Date currentDate) throws Exception {

		List<McOperationVO> l = new ArrayList<McOperationVO>();
		Set<McEscalation> escalations = new HashSet<McEscalation>();
		McOperationVO mcOpVo = new McOperationVO();

		String userId = ctx.getUserId();
		String profileCode = ctx.getProfileCode();

		Boolean lastChecker = false;

		for (McOperationVO mcOperationVo : list) {
			
			ArrayList<McEscalationVO> escalationsStatus = new ArrayList<>();
			if(mcOperationVo.getStatus() != null && !mcOperationVo.getStatus().equalsIgnoreCase("O") 
					&& mcOperationVo.getEsc_history() == null) {
				//Setting escHistory for old verified operations
				escalationsStatus = setEscHistory(mcOperationVo, ctx);
			} else {
				escalationsStatus = getEscalations(mcOperationVo, userId, profileCode, ctx, currentDate); 
			}

			//mcOpVo = McOperationUtils.entityToVO(ctx, mcOperation, 0, 0);
			mcOpVo = spreadVO(mcOperationVo);
			mcOpVo.setMcEscalations(escalationsStatus);
			
			if (mcOperationVo.getId_maker().equals(userId)) {
				mcOpVo.setCanValidate("N");
				l.add(mcOpVo);
				continue;
			} else if (mcOperationVo.getStatus() != null && !mcOperationVo.getStatus().equals("O")
					) {
//					&& mcOperationVo.getId_checker().equals(userId)) {
				mcOpVo.setCanValidate("N");
				l.add(mcOpVo);
				continue;
			} else if (mcOperationVo.getStatus() != null && mcOperationVo.getStatus().equals("O")) {
			
				escalations = getMcOperationRepository().findById(mcOperationVo.getMc_operation_id()).getMc_operationpar_id().getMcMcEscalations();
				int escSize = escalations.size();
				
				McEscalation[] tab = escalations.toArray(new McEscalation[escSize]);
				
				Arrays.sort(tab,(McEscalation a, McEscalation b) -> a.getOrder() - b.getOrder());
				
				lastChecker = escSize > 0 && ((tab[escSize - 1].getAccessType().equals("U")
						&& tab[escSize - 1].getChecker_id().equals(userId))
						|| (tab[escSize - 1].getAccessType().equals("P")
								&& tab[escSize - 1].getChecker_id().equals(profileCode)));
				
				//Date currentDate = new Date();
				
				Calendar beforeCal =  Calendar.getInstance();
				Calendar afterCal = Calendar.getInstance();
				beforeCal.setTime(mcOperationVo.getCreation_date());
				
//				if(escSize>0) {
//					tab = Arrays.copyOf(tab,escSize-1);
//				}
				int iterator=-1;
				 for (McEscalation mcEscalation : tab) {
					 iterator++;
					 if(iterator<(escSize-1)) { 
						//mcOpVo = McOperationUtils.entityToVO(ctx, mcOperationVo, 0, 0);
						mcOpVo = spreadVO(mcOperationVo);
						mcOpVo.setCanValidate("N");
						
						afterCal = addEscalationDuration(beforeCal, mcEscalation);
						
						if ((mcEscalation.getAccessType().equals("U") && mcEscalation.getChecker_id().equals(userId))
								|| (mcEscalation.getAccessType().equals("P") && mcEscalation.getChecker_id().equals(profileCode))){
						
							if (afterCal.getTime().after(currentDate) && currentDate.after(beforeCal.getTime())) {
								mcOpVo.setCanValidate("Y");
							}
							mcOpVo.setMcEscalations(escalationsStatus);
							l.add(mcOpVo);
						    break;
						}
						
						beforeCal.setTime(afterCal.getTime());
					}
					 else if(iterator==(escSize-1) && lastChecker) { // 
							//mcOpVo = McOperationUtils.entityToVO(ctx, mcOperation, 0, 0);
							mcOpVo = spreadVO(mcOperationVo);
							mcOpVo.setMcEscalations(escalationsStatus);
							mcOpVo.setCanValidate("N");
							if((escSize > 1 && currentDate.after(afterCal.getTime())) || escSize==1) {
								mcOpVo.setCanValidate("Y");
							}
							l.add(mcOpVo);
						}
					 else if(iterator==(escSize-1) && !lastChecker){
							mcOpVo.setMcEscalations(escalationsStatus);
							mcOpVo.setCanValidate("N");
							l.add(mcOpVo);
					 }
						 
				 }
					
				
			}
			
		}

		return l;

	}
	

	private ArrayList<McEscalationVO> getEscalations(McOperationVO mcOperationVo, String userId, String profileCode, ServiceContext ctx, Date currentDate) throws Exception {
		ArrayList<McEscalationVO> tabRes = new  ArrayList<McEscalationVO>();
		
		if(mcOperationVo.getStatus() != null) {
			if(mcOperationVo.getStatus().equals("O")) {
				Set<McEscalation> escalations = getMcOperationRepository().findById(mcOperationVo.getMc_operation_id()).getMc_operationpar_id().getMcMcEscalations();
				int escSize = escalations.size();
				
				McEscalation[] tab = escalations.toArray(new McEscalation[escSize]);
				
				Arrays.sort(tab,(McEscalation a, McEscalation b) -> a.getOrder() - b.getOrder());
				
				Calendar beforeCal =  Calendar.getInstance();
				Calendar afterCal = Calendar.getInstance();
				beforeCal.setTime(mcOperationVo.getCreation_date());
				int index = 1;
				boolean beingChecked = false;
				
				for (McEscalation mcEscalation : tab) {
					
					McEscalationVO mcEscalationVo = McEscalationUtils.entityToVO(ctx, mcEscalation, 0, 0);
		
					afterCal = addEscalationDuration(beforeCal, mcEscalation);
					//mcEscalationVo.setChecking_action("waiting for prior checker");
					mcEscalationVo.setChecking_action(WAITING_FOR_PRIOR_CHECKER);
					
					if(index < escSize) {
						mcEscalationVo.setChecking_limit(afterCal.getTime());
						if(currentDate.after(afterCal.getTime())) {//expired
							
							//mcEscalationVo.setChecking_action("no action taken");
							mcEscalationVo.setChecking_action(NO_ACTION_TAKEN);
						}else if (afterCal.getTime().after(currentDate) && currentDate.after(beforeCal.getTime())) {
							//mcEscalationVo.setChecking_action("ready for checking");
							mcEscalationVo.setChecking_action(READY_FOR_CHECKING);
							beingChecked = true;
						}
					} else {//lastChecker
						if(!beingChecked) {
							//mcEscalationVo.setChecking_action("ready for checking");
							mcEscalationVo.setChecking_action(READY_FOR_CHECKING);
						}
					}
					
					beforeCal.setTime(afterCal.getTime());
					tabRes.add(mcEscalationVo);
					index++;
				}
				//return tabRes;
				
			} else {
				if(mcOperationVo.getEsc_history() != null) {
					Gson gson = GsonHelper.getGson();
			        //Gson gs = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

					JsonArray escHisto_array = gson.fromJson(mcOperationVo.getEsc_history(), JsonArray.class);
					for (JsonElement jsonElement : escHisto_array) {
						McEscalationVO mcEscalationVo = gson.fromJson(jsonElement, McEscalationVO.class);
						tabRes.add(mcEscalationVo);
					}
				}
			}
		}
		
		return tabRes;
		
	}
	
	private ArrayList<McEscalationVO> setEscHistory(McOperationVO mcOperationVo, ServiceContext ctx) throws Exception {
		
		McOperation mcOperation_entity = getMcOperationRepository().findById(mcOperationVo.getMc_operation_id());
		
		Set<McEscalation> escalations = mcOperation_entity.getMc_operationpar_id().getMcMcEscalations();
		int escSize = escalations.size();
		
		McEscalation[] tab = escalations.toArray(new McEscalation[escSize]);
		Arrays.sort(tab,(McEscalation a, McEscalation b) -> a.getOrder() - b.getOrder());
		ArrayList<McEscalationVO> tabRes = new  ArrayList<McEscalationVO>();

		Calendar beforeCal =  Calendar.getInstance();
		Calendar afterCal = Calendar.getInstance();
		beforeCal.setTime(mcOperationVo.getCreation_date());
		int index = 1;
		
		for (McEscalation mcEscalation : tab) {
			
			McEscalationVO mcEscalationVo = McEscalationUtils.entityToVO(ctx, mcEscalation, 0, 0);

			afterCal = addEscalationDuration(beforeCal, mcEscalation);

			//mcEscalationVo.setChecking_action("action reviewed before");
			mcEscalationVo.setChecking_action(ACTION_REVIEWED_BEFORE);
			
			Date decisionDate = mcOperationVo.getDecision_date();
			if(index < escSize) {
				mcEscalationVo.setChecking_limit(afterCal.getTime());
			}
			if((decisionDate.after(beforeCal.getTime()) && (afterCal.getTime().after(decisionDate)) || index==escSize)) {
				
				if(mcEscalationVo.getAccessType().equalsIgnoreCase("U") && mcEscalationVo.getChecker_id().equals(mcOperationVo.getId_checker())) {
					//mcEscalationVo.setChecking_action("action reviewed");
					mcEscalationVo.setChecking_action(ACTION_REVIEWED);

				}else if(mcEscalationVo.getAccessType().equalsIgnoreCase("P") && hasCheckerProfile(ctx,mcEscalationVo.getChecker_id(),mcOperationVo.getId_checker())) {
					//mcEscalationVo.setChecking_action("action reviewed by : "+mcOperationVo.getId_checker());
					mcEscalationVo.setChecking_action(ACTION_REVIEWED_BY+mcOperationVo.getId_checker());

				}
			} else {
				//mcEscalationVo.setChecking_action("no action taken");
				mcEscalationVo.setChecking_action(NO_ACTION_TAKEN);
			}
			
			beforeCal.setTime(afterCal.getTime());
			tabRes.add(mcEscalationVo);
			index++;
		}
		
		mcOperation_entity.setEsc_history(GsonHelper.getGson().toJson(tabRes));
		this.getMcOperationRepository().save(mcOperation_entity);
		
		return tabRes;
	}
	
	public boolean hasCheckerProfile(ServiceContext ctx, String profile, String checker) throws Exception{
		UsersVO usersVO = new UsersVO();
		usersVO.setUser_code(checker);
		
		List<UsersVO> users = usersService.searchUsersService(ctx, usersVO);
		if(users.size()>0) {
			if(users.get(0).getProfile_fk().equals(profile)) {
				return true;
			}
		}
		return false;
	}

	public Calendar addEscalationDuration(Calendar cal, McEscalation mcEscalation) {

		Calendar after = Calendar.getInstance();
		after.setTime(cal.getTime());
		int periodDays = Integer.parseInt(mcEscalation.getPeriod().split(":")[0]);
		int periodHours = Integer.parseInt(mcEscalation.getPeriod().split(":")[1]);
		int periodMinutes = 0;

		if (mcEscalation.getPeriod().split(":").length > 2) {
			periodMinutes = Integer.parseInt(mcEscalation.getPeriod().split(":")[2]);
		}
		after.add(Calendar.DATE, periodDays);
		after.add(Calendar.HOUR_OF_DAY, periodHours);
		after.add(Calendar.MINUTE, periodMinutes);

		return after;
	}

	/**
	 * Find entities to be checked
	 *
	 * @return List of McOperationVO
	 *
	 */
	public List<McOperationVO> getCheckableOperationService(ServiceContext ctx, McOperationVO mcOperationVO)
			throws Exception {

		String sessionID = null;

		String remoteAddress = null;
		if (ctx != null) {
			if (ctx.getDetails() != null) {
				sessionID = ctx.getDetails().getSessionId();
				remoteAddress = ctx.getDetails().getRemoteAddress();
			}
			logger.info("PowerCardV3 : Operation:getCheckableOperationService , USER :" + ctx.getUserId() + " , SessionID :"
					+ sessionID + " , RemoteAddress:" + remoteAddress);
		}

		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		if (ctx != null) {
			McOperationUtils.dataFilter(ctx, con);
		}

		McOperationUtils.setListOfCriteria(ctx, con, mcOperationVO);

		List<McOperation> l_entity = new ArrayList<McOperation>();
		List<McOperation> l_entity_out = new ArrayList<McOperation>();
		List<McOperationVO> l = new ArrayList<McOperationVO>();

		int page = mcOperationVO.getPage();
		int pageSize = mcOperationVO.getPageSize();
		boolean countTotalPages = true;
		if (page > 0 && pageSize > 0) {
			PagingParameter pagingParameter = PagingParameter.pageAccess(pageSize, page, countTotalPages);

			PagedResult<McOperation> pagedResult = this.getMcOperationRepository().findByCondition(con,
					pagingParameter);

			l_entity = pagedResult.getValues();

			ctx.setProperty("pagedResult", pagedResult);

		} else {
			l_entity = this.getMcOperationRepository().findByCondition(con);
		}

		l = McOperationUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);


		Set<McEscalation> escalations = new HashSet<McEscalation>();

		String userId = ctx.getUserId();
		String profileCode = ctx.getProfileCode();
		Calendar cal = Calendar.getInstance();

		for (McOperation mcOperation : l_entity) {
			escalations = mcOperation.getMc_operationpar_id().getMcMcEscalations();
			int escSize = escalations.size();
			int periodDays = 0;
			int periodHours = 0;
			int index = 0;
			Boolean lastChecker = false;
			for (McEscalation mcEscalation : escalations) {
				if (index++ > 0 && index < escSize) {
					periodDays += Integer.parseInt(mcEscalation.getPeriod().split(":")[0]);
					periodHours += Integer.parseInt(mcEscalation.getPeriod().split(":")[1]);
				} else if (index == 1 && escSize > 1) {
					periodDays = Integer.parseInt(mcEscalation.getPeriod().split(":")[0]);
					periodHours = Integer.parseInt(mcEscalation.getPeriod().split(":")[1]);
				} else {
					lastChecker = true;
				}
				if ((mcEscalation.getAccessType().equals("U") && mcEscalation.getChecker_id().equals(userId))
						|| (mcEscalation.getAccessType().equals("P")
								&& mcEscalation.getChecker_id().equals(profileCode))) {

					cal.setTime(mcOperation.getCreation_date());
					cal.add(Calendar.DATE, periodDays);
					cal.add(Calendar.HOUR_OF_DAY, periodHours);

					if (cal.getTime().after(new Date()) && !lastChecker) {
						l_entity_out.add(mcOperation);
						break;
					} else if (lastChecker) {
						l_entity_out.add(mcOperation);
					}
				}
			}
		}
		l = McOperationUtils.mapListOfEntitiesToVO(ctx, l_entity_out, 0, 0);
		return l;

	}


	public List<McOperationVO> getObjMcOperationService(ServiceContext ctx, String obj) throws Exception {

		Gson gson = GsonHelper.getGson();
		List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

		List<McOperation> l_entity = new ArrayList<McOperation>();
		List<McOperationVO> l = new ArrayList<McOperationVO>();
		List<McOperationVO> listOfPending = new ArrayList<McOperationVO>();
		
		String clientTimeZone = null;
		
		try {

			JsonObject object = gson.fromJson(obj, JsonObject.class);
			JsonArray pars_urls = new JsonArray();
			JsonObject keysValuesObj = null;
			List<String> pk_keys = null;
			
			if(object.has("clientTimeZone")){
				clientTimeZone = object.get("clientTimeZone").getAsString();
			}
			
			if(object.has("pk_keys") && object.get("pk_keys").getClass().toString().equals("class com.google.gson.JsonObject")) {
				keysValuesObj = object.get("pk_keys").getAsJsonObject();
				Set<Map.Entry<String, JsonElement>> entries = keysValuesObj.entrySet();
				if(!entries.isEmpty()) {
					pk_keys = new ArrayList<>();
					for(Map.Entry<String, JsonElement> entry: entries) {
						pk_keys.add(entry.getKey());
					}
				}
			}

			if(object.has("list_pars_by_url")) {
				if(object.get("list_pars_by_url").getClass().toString().equals("class com.google.gson.JsonArray")){
					pars_urls = gson.fromJson(object.get("list_pars_by_url"), JsonArray.class);				
				} else {
					pars_urls.add(object.get("list_pars_by_url").getAsString());
				}
			}
			if(pars_urls.size() > 0) {
				String[] urls = new Gson().fromJson(pars_urls, String[].class);
				con.add(ConditionalCriteria.in(McOperationProperties.mc_operationpar_id().operation_url(),urls));
				con.add(ConditionalCriteria.equal(McOperationProperties.status(), "O"));
				
				l_entity = this.getMcOperationRepository().findByCondition(con);
				
				//l = McOperationUtils.mapListOfEntitiesToVO(ctx, l_entity, 0, 0);
				if(l_entity.size() > 0 ) {
					for (int i = 0; i < l_entity.size(); i++)
				    {
					  McOperation mcOperationTmp = (McOperation)l_entity.get(i);
				      McOperationVO mcOperationVoTmp = McOperationUtils.entityToVO(ctx, mcOperationTmp,0, 0);
				      mcOperationVoTmp.setOp_name(mcOperationTmp.getMc_operationpar_id().getOperation_name());
				      l.add(mcOperationVoTmp);
				    }
				}
				
				if(keysValuesObj != null && pk_keys != null && pk_keys.size() > 0) {
					if(l.size() > 0) {
						
						JsonArray lAsJsonArray = gson.toJsonTree(l).getAsJsonArray();
												
						for (JsonElement jsonElement : lAsJsonArray) {
							boolean same = false;
							for (String key : pk_keys) {
								String voStr = jsonElement.getAsJsonObject().get("vo").getAsString();
								JsonObject vo = gson.fromJson(voStr, JsonObject.class);
								same = vo.get(key).getAsString().equals(keysValuesObj.get(key).getAsString());
								if(!same) break;
							}
							
							if(same) {
								listOfPending.add(gson.fromJson(jsonElement, McOperationVO.class));
							}
						}
					}
				}			
			}
			
			
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
		
		if(listOfPending.size() > 0) {
			//check timeZone before
			if(clientTimeZone != null && !clientTimeZone.equals("")) {
				Date now = null;
				try {
					String serverTimeZone = java.util.TimeZone.getDefault().getID();
					LocalDateTime dt = LocalDateTime.now();
					ZonedDateTime fromZonedDateTime = dt.atZone(ZoneId.of(clientTimeZone));
					ZonedDateTime toZonedDateTime = dt.atZone(ZoneId.of(serverTimeZone));
					long diff = Duration.between(fromZonedDateTime, toZonedDateTime).toMinutes();
					Calendar cal = Calendar.getInstance();
					cal.setTime(new Date());
					cal.add(Calendar.MINUTE, (int)diff);
					now = cal.getTime();
				} catch(Exception e) {
					logger.error("error getting timezone "+e.getMessage());
					now = new Date();
				}
				return getMyMcOperationService(ctx, listOfPending, now);
				
			} else {
				return getMyMcOperationService(ctx, listOfPending, new Date());
			}
		}
		return listOfPending;
		
	}
	
	public static String mergeJsonStrings(String json1, String json2) {
		JsonObject mergedJSON = new JsonObject();
		try {
			JsonObject old = new Gson().fromJson(json1, JsonObject.class);
			
			mergedJSON = new Gson().fromJson(json2, JsonObject.class);
			
			for (String key : old.keySet()) {
				if(mergedJSON.has(key)) {
					mergedJSON.add(key, old.get(key));
				}
			}
 
		} catch (JsonIOException e) {
			logger.error("MC - JSON Merging Exception "+e.getMessage());
		}
		return mergedJSON.toString();
	}
	
	
	private String escapeResponse(String responseStr, String prefix) {
		return responseStr.startsWith(SECURE_PREFIX) ? responseStr.substring(SECURE_PREFIX.length()) : responseStr;
	}

	
}
