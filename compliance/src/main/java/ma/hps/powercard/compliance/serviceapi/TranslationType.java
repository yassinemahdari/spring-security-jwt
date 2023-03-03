package ma.hps.powercard.compliance.serviceapi;

public class TranslationType {
	String shortValue;
	String value;

	public TranslationType(String shortVal, String longVal) {
		super();
		this.shortValue = shortVal;
		this.value = longVal;
	}

	public String getShortValue() {
		return shortValue;
	}

	public void setShortValue(String shortValue) {
		this.shortValue = shortValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
