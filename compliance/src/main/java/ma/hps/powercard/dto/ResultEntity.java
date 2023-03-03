package ma.hps.powercard.dto;

public class ResultEntity {

	private String key;
	private Object listEntities;

	public ResultEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ResultEntity(String key, Object listEntities) {
		super();
		this.key = key;
		this.listEntities = listEntities;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getListEntities() {
		return listEntities;
	}

	public void setListEntities(Object listEntities) {
		this.listEntities = listEntities;
	}

}
