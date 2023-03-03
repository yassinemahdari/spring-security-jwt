package ma.hps.powercard.compliance.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ma.hps.powercard.compliance.serviceapi.UsersVO;
import ma.hps.powercard.compliance.utils.typeadapter.BigDecimalTypeAdapter;
import ma.hps.powercard.compliance.utils.typeadapter.DateTypeAdapter;
import ma.hps.powercard.compliance.utils.typeadapter.DoubleTypeAdapter;
import ma.hps.powercard.compliance.utils.typeadapter.FloatTypeAdapter;
import ma.hps.powercard.compliance.utils.typeadapter.IntegerTypeAdapter;
import ma.hps.powercard.compliance.utils.typeadapter.LongTypeAdapter;

public class GsonHelper {

	public static Gson gson;

	public static Gson getGson() {

		if (gson != null)
			return gson;

		// this needs to be moves somewhere else.
		// create a new DTO inside the btdesign UserWithoutPasswordVO to ignore the password.
		ExclusionStrategy ignorePasswordFieldInUsersVO = new ExclusionStrategy() {
			@Override
			public boolean shouldSkipField(FieldAttributes field) {
				if (field.getDeclaringClass() == UsersVO.class && field.getName().equals("password")) {
					return true;
				}
				return false;
			}

			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				return false;
			}
		};

		gson = new GsonBuilder()
				.registerTypeAdapter(Date.class, new DateTypeAdapter(Date.class))
				.registerTypeAdapter(Timestamp.class, new DateTypeAdapter(Timestamp.class))
				.registerTypeAdapter(java.sql.Date.class, new DateTypeAdapter(java.sql.Date.class))
				.registerTypeAdapter(Long.class, new LongTypeAdapter())
				.registerTypeAdapter(Integer.class, new IntegerTypeAdapter())
				.registerTypeAdapter(Float.class, new FloatTypeAdapter())
				.registerTypeAdapter(Double.class, new DoubleTypeAdapter())
				.registerTypeAdapter(BigDecimal.class, new BigDecimalTypeAdapter())
				.addSerializationExclusionStrategy(ignorePasswordFieldInUsersVO)
				.create();

		return gson;
	}

}
