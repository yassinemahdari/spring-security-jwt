package ma.hps.powercard.compliance.serviceimpl.spec;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.util.ReflectionUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ma.hps.powercard.compliance.domain.BankProperties;
import ma.hps.powercard.dto.ResultEntity;
import org.apache.log4j.Logger;

@Service
public class LoadEntityServiceimpl {
	
	private final static Logger logger = Logger.getLogger(LoadEntityServiceimpl.class);

	@Autowired
	WebApplicationContext context;

	public LoadEntityServiceimpl() {
		super();
	}

	@Async
	public CompletableFuture<ResultEntity> loadEntity(Entry<String, JsonElement> entry, Gson gson, ServiceContext ctx) throws Exception {
		ResultEntity resultEntity = new ResultEntity();
		Object entityRepository = null;
        String cle = entry.getKey();
        Object valeurTmp = entry.getValue();
        //WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        
        JsonObject jsonObject = gson.fromJson("" + valeurTmp, JsonObject.class);
        String entityName = "" + jsonObject.get("entity");

		if (entityName.equals("null")) {
			entityName = cle;
		}

		String beanName = StringUtils.uncapitalize(entityName) + "Repository";
		if (context.containsBean(beanName)) {
			entityRepository = context.getBean(beanName);
		}

		String packageName = AopUtils.getTargetClass(entityRepository).getPackage().getName();
		String repositoryPackageName = packageName + "." + entityName + "Utils";
		String dataTransfertObjectFullName = packageName.replaceAll("repositoryimpl", "serviceapi") + "." + entityName
				+ "VO";

		Class<?> dataTransfertObjectClass = Class.forName(dataTransfertObjectFullName);

		Object objectVO = null;

		objectVO = gson.fromJson(jsonObject.get("criterias"), dataTransfertObjectClass);

		List<ConditionalCriteria> conEntity = new ArrayList<ConditionalCriteria>();
		if (ctx != null && !entityName.equals("Bank")) {
			Method dataFilterMethod = ReflectionUtils.getDataFilterMethod(repositoryPackageName);
			dataFilterMethod.invoke(null, ctx, conEntity);
		}
		if (entityName.equals("Bank")) {
			try {
				List<String> banks = (List<String>) ctx.getProperty("bankDataAccess");

				if (banks.size() > 0) {
					conEntity.add(ConditionalCriteria.in(BankProperties.bank_code(), banks));

				}
			} catch (Exception e) {
				logger.error("bank data access error");
				logger.error(e.getMessage());
			}
		}
		if (objectVO != null) {
			Method setListOfCriteriaMethod = ReflectionUtils.getSetListOfCriteriaMethod(repositoryPackageName,
					dataTransfertObjectClass);
			setListOfCriteriaMethod.invoke(null, ctx, conEntity, objectVO);
		}
		Method findByConditionMethod = ReflectionUtils.getFindByConditionMethod(entityRepository);
		List<?> lEntity = ReflectionUtils.uncheckedCast(findByConditionMethod.invoke(entityRepository, conEntity));

		Method mapListOfEntitiesMethod = ReflectionUtils.getMapListOfEntitiesMethod(repositoryPackageName);
		List<?> lEntityVO = ReflectionUtils.uncheckedCast(mapListOfEntitiesMethod.invoke(null, ctx, lEntity, 0, 0));
		resultEntity.setKey(cle);
		resultEntity.setListEntities(lEntityVO);
		return CompletableFuture.completedFuture(resultEntity);
	}
	
}
