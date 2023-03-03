package ma.hps.powercard.compliance.utils.typeadapter;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class DateTypeAdapter extends TypeAdapter<Date> {

	private final Class<? extends Date> dateType;

	private final SimpleDateFormat formatter0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
	private final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	private final SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	public DateTypeAdapter(Class<? extends Date> dateType) {
		this.dateType = dateType;
		TimeZone UTC = TimeZone.getTimeZone("UTC");
		formatter0.setTimeZone(UTC);
		formatter1.setTimeZone(UTC);
		formatter2.setTimeZone(UTC);
	}
	
	@Override
	public void write(JsonWriter writer, Date value) throws IOException {
		if (value == null) {
			writer.nullValue();
			return;
		}

		writer.value(formatter0.format(value));
	}

	@Override
	public Date read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
        	reader.nextNull();
        	return null;
        }
		
		Date date = deserializeToDate(reader.nextString());
		
		if (dateType == Date.class) {
			return date;
		} else if (dateType == Timestamp.class) {
			return new Timestamp(date.getTime());
		} else if (dateType == java.sql.Date.class) {
			return new java.sql.Date(date.getTime());
		} else {
			// This must never happen: dateType is guarded in the primary constructor
			throw new AssertionError();
		}

	}
	
	private Date deserializeToDate(String dateStr) {

		try {
			String date = dateStr;
			
			try {
				return formatter0.parse(date);
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
			try {
				return formatter1.parse(date);
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
			try {
				return formatter2.parse(date);
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
			return ISO8601Utils.parse(date, new ParsePosition(0));
		} catch (ParseException e) {
			return null;
		}
		
	}

}
