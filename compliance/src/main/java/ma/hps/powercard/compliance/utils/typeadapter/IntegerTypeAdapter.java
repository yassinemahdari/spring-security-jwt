package ma.hps.powercard.compliance.utils.typeadapter;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class IntegerTypeAdapter extends TypeAdapter<Integer> {
	
	@Override
	public Integer read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
        	reader.nextNull();
        	return null;
        }
        String stringValue = reader.nextString();
        try {
        	Integer value = Integer.valueOf(stringValue);
        	return value;
        } catch(NumberFormatException e) {
        	return null;
        }
    }
    
	@Override
    public void write(JsonWriter writer, Integer value) throws IOException {
		if (value == null) {
			writer.nullValue();
			return;
		}
		writer.value(value);
	}

}
