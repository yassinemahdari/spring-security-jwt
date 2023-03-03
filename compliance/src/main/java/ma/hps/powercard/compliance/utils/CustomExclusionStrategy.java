package ma.hps.powercard.compliance.utils;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
 
public class CustomExclusionStrategy implements ExclusionStrategy {
	
	private String fields ;
 
	public CustomExclusionStrategy(String fields) {
		super();
		this.fields = fields;
	}

	public boolean shouldSkipField(FieldAttributes f) {
        return ( fields.length()>0 && !this.fields.contains(f.getName()));
    }
 
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
 
}
