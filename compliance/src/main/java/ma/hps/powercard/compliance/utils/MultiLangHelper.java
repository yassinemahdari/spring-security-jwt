package ma.hps.powercard.compliance.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonObject;

import ma.hps.powercard.compliance.domain.Locale;
import ma.hps.powercard.compliance.domain.LocaleRepository;
import ma.hps.powercard.compliance.domain.Multi_lang_tables;
import ma.hps.powercard.compliance.domain.Multi_lang_tablesRepository;
import ma.hps.powercard.compliance.domain.Multi_lang_values;
import ma.hps.powercard.compliance.domain.Multi_lang_valuesPK;
import ma.hps.powercard.compliance.domain.Multi_lang_valuesProperties;
import ma.hps.powercard.compliance.domain.Multi_lang_valuesRepository;
import ma.hps.powercard.compliance.serviceapi.Multi_lang_tablesService;
import ma.hps.powercard.compliance.serviceapi.Multi_lang_tablesVO;
import ma.hps.powercard.compliance.serviceapi.Multi_lang_valuesService;

public class MultiLangHelper {


	public static Multi_lang_valuesRepository multi_lang_valuesRepository = null;
	public static LocaleRepository localeRepository = null;
	public static Multi_lang_tablesRepository multi_lang_tablesRepository = null;
	public static Multi_lang_tablesService multi_lang_tablesService = null;
	public static Multi_lang_valuesService multi_lang_valuesService = null;
	public static List<String> multiLangTables = null;
	public static HashMap<String, List<Multi_lang_values>> multi_lang_valuesMap = null;
	public static HashMap<String, HashMap<String, List<Multi_lang_values>>> multi_lang_valuesMapByLocale = null;
	public static HashMap<String, List<Multi_lang_values>> fastAccessMultiLangValueMap = null;

	public static void createMultiLangValue(ServiceContext ctx, String columnName, HashMap<String, String> columnValue,
			String multiLangValue, String tableName) {

		Multi_lang_valuesPK multi_lang_valuesPK = new Multi_lang_valuesPK(columnName, getColumnValue(columnValue),
				(String) ctx.getProperty("userLocale"), tableName);
		Multi_lang_values multiLangDefault = new Multi_lang_values(multi_lang_valuesPK);
		multiLangDefault.setFk_multi_lang_values_01(new Locale((String) ctx.getProperty("userLocale")));
		multiLangDefault.setMulti_lang_value(multiLangValue);
		multiLangDefault.setFk_multi_lang_values_02(new Multi_lang_tables(tableName));
		multiLangDefault = getMulti_lang_valuesRepository().insert(multiLangDefault);

	}

	public static String getColumnValue(HashMap<String, String> properties) {

		JsonObject o = new JsonObject();
		for (String key : properties.keySet()) {
			o.addProperty(key, properties.get(key));
		}
		return GsonHelper.gson.toJson(o);
	}

	public static void updateMultiLangValue(ServiceContext ctx, String columnName, HashMap<String, String> columnValue,
			String tableName, String multiLangValue) {
		String locale = (String) ctx.getProperty("userLocale");
		String hash = locale + tableName + columnName;

		if (columnValue != null && MultiLangHelper.getMultiLangTables(ctx) != null
				&& MultiLangHelper.getMultiLangTables(ctx).contains(tableName)) {

			List<Multi_lang_values> searchResult = getMultiLangValuesByLocale(ctx, tableName, columnName);// getMultiLangValues(tableName);

			Multi_lang_values entryToUpdate = null;

			for (Multi_lang_values entry : searchResult) {

				if (columnName.equals(entry.getMulti_lang_valuesPK().getColumn_name())
						&& ((String) ctx.getProperty("userLocale")).equals(entry.getMulti_lang_valuesPK().getLocale())
						&& compareValue(columnValue, entry.getMulti_lang_valuesPK().getColumn_value()))

				{
					entryToUpdate = entry;
					break;
				}
			}
			if (entryToUpdate != null) {
				entryToUpdate.setMulti_lang_value(multiLangValue);
				getMulti_lang_valuesRepository().save(entryToUpdate);
			} else {
				createMultiLangValue(ctx, columnName, columnValue, multiLangValue, tableName);
			}

			try {
				getMulti_lang_valuesService().updateCachedMultiLangValues(ctx, tableName, columnName, locale);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void deleteMultiLangValue(ServiceContext ctx, String columnName, HashMap<String, String> columnValue,
			String tableName) {

		if (columnValue != null && MultiLangHelper.getMultiLangTables(ctx) != null
				&& MultiLangHelper.getMultiLangTables(ctx).contains(tableName)) {
			
			List<ConditionalCriteria> confMultiLang = new ArrayList<ConditionalCriteria>();

			confMultiLang.add(ConditionalCriteria
					.equal(Multi_lang_valuesProperties.fk_multi_lang_values_02().table_name(), tableName));
			
			confMultiLang.add(ConditionalCriteria
					.equal(Multi_lang_valuesProperties.multi_lang_valuesPK().column_value(), getColumnValue(columnValue)));

			List<Multi_lang_values> searchResult = MultiLangHelper.getMulti_lang_valuesRepository()
					.findByCondition(confMultiLang);

			for (Multi_lang_values entry : searchResult) {
				if (compareValue(columnValue, entry.getMulti_lang_valuesPK().getColumn_value())) {
					getMulti_lang_valuesRepository().delete(entry);
				}
			}
			String locale = (String) ctx.getProperty("userLocale");
			try {
				getMulti_lang_valuesService().updateCachedMultiLangValues(ctx, tableName, columnName, locale);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static List<String> getMultiLangTables(ServiceContext ctx) {

		if (multiLangTables == null) {
			multiLangTables = new ArrayList<String>();

			List<Multi_lang_tablesVO> listMulti_lang_tables = null;
			try {
				listMulti_lang_tables = getMulti_lang_tablesService().getCachedMulti_lang_tables(ctx);
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (Multi_lang_tablesVO multi_lang_tablesVO : listMulti_lang_tables) {

				multiLangTables.add(multi_lang_tablesVO.getTable_name());
			}
		}
		return multiLangTables;

	}

	@Deprecated
	public static List<Multi_lang_values> getMultiLangValues(String tableName) {

		if (multi_lang_valuesMap == null || !multi_lang_valuesMap.containsKey(tableName)) {

			if (multi_lang_valuesMap == null)

				multi_lang_valuesMap = new HashMap<String, List<Multi_lang_values>>();

			List<ConditionalCriteria> confMultiLang = new ArrayList<ConditionalCriteria>();

			confMultiLang.add(ConditionalCriteria
					.equal(Multi_lang_valuesProperties.fk_multi_lang_values_02().table_name(), tableName));

			List<Multi_lang_values> listMulti_lang_tables = MultiLangHelper.getMulti_lang_valuesRepository()
					.findByCondition(confMultiLang);

			multi_lang_valuesMap.put(tableName, listMulti_lang_tables);
		}
		return multi_lang_valuesMap.get(tableName);

	}

	public static List<Multi_lang_values> getMultiLangValuesByLocale(ServiceContext ctx, String tableName,
			String columnName) {
		
		String locale = (String) ctx.getProperty("userLocale");
		List<Multi_lang_values> result = new ArrayList<Multi_lang_values>();
		try {
			result = getMulti_lang_valuesService().getCachedMultiLangValues(ctx, tableName, columnName, locale);
			return result;
		} catch (Exception e) {
			return result;
		}

	}

	public static String applyMultiLangValue(ServiceContext ctx, String tableName, String columnName,
			HashMap<String, String> columnValue) {

		if (columnValue != null && MultiLangHelper.getMultiLangTables(ctx) != null
				&& MultiLangHelper.getMultiLangTables(ctx).contains(tableName)) {

			List<Multi_lang_values> searchResult = getMultiLangValuesByLocale(ctx, tableName, columnName);
			
			for (Multi_lang_values entry : searchResult) {
				String value = entry.getMulti_lang_valuesPK().getColumn_value();
				if (compareValue(columnValue, value)) {
					return entry.getMulti_lang_value() != null ? entry.getMulti_lang_value() : "";
				}
			}
		}
		return null;
	}

	private static boolean compareValue(HashMap<String, String> properties, String str2) {

		try {
			if (str2 != null && str2.length() > 0) {
				JsonObject o1 = new JsonObject();
				for (String key : properties.keySet()) {

					o1.addProperty(key, properties.get(key));
				}
				JsonObject o2 = GsonHelper.gson.fromJson(str2, JsonObject.class);

				return o1.equals(o2);
			}
			return false;
		} catch (Exception e) {
			// logger.error("Error comparing: " + str2 + " , " + e.getMessage());
		}
		return false;

	}

	public static LocaleRepository getLocaleRepository() {

		WebApplicationContext context = ApplicationContextProcessor.getContext();

		if (localeRepository == null) {

			localeRepository = (LocaleRepository) context.getBean("localeRepository");
		}

		return localeRepository;
	}

	public static Multi_lang_valuesRepository getMulti_lang_valuesRepository() {

		WebApplicationContext context = ApplicationContextProcessor.getContext();

		if (multi_lang_valuesRepository == null) {

			multi_lang_valuesRepository = (Multi_lang_valuesRepository) context.getBean("multi_lang_valuesRepository");
		}
		return multi_lang_valuesRepository;
	}

	public static Multi_lang_tablesRepository getMulti_lang_tablesRepository() {

		WebApplicationContext context = ApplicationContextProcessor.getContext();

		if (multi_lang_tablesRepository == null) {

			multi_lang_tablesRepository = (Multi_lang_tablesRepository) context.getBean("multi_lang_tablesRepository");
		}

		return multi_lang_tablesRepository;
	}

	public static Multi_lang_tablesService getMulti_lang_tablesService() {

		WebApplicationContext context = ApplicationContextProcessor.getContext();

		if (multi_lang_tablesService == null) {

			multi_lang_tablesService = (Multi_lang_tablesService) context.getBean("multi_lang_tablesService");
		}

		return multi_lang_tablesService;
	}
	
	public static Multi_lang_valuesService getMulti_lang_valuesService() {

		WebApplicationContext context = ApplicationContextProcessor.getContext();

		if (multi_lang_valuesService == null) {

			multi_lang_valuesService = (Multi_lang_valuesService) context.getBean("multi_lang_valuesService");
		}

		return multi_lang_valuesService;
	}

}