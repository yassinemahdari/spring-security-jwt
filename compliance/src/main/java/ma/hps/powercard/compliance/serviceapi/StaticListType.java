package ma.hps.powercard.compliance.serviceapi;

public class StaticListType {
	private String value_id;
	private String value_label;
	private String value_short_label;
	private String value_order;
	public StaticListType(String value_id, String value_label, String value_short_label, String value_order) {
		super();
		this.value_id = value_id;
		this.value_label = value_label;
		this.value_short_label = value_short_label;
		this.value_order = value_order;
	}
	public String getValue_id() {
		return value_id;
	}
	public void setValue_id(String value_id) {
		this.value_id = value_id;
	}
	public String getValue_label() {
		return value_label;
	}
	public void setValue_label(String value_label) {
		this.value_label = value_label;
	}
	public String getValue_short_label() {
		return value_short_label;
	}
	public void setValue_short_label(String value_short_label) {
		this.value_short_label = value_short_label;
	}
	public String getValue_order() {
		return value_order;
	}
	public void setValue_order(String value_order) {
		this.value_order = value_order;
	}
	
	
}
