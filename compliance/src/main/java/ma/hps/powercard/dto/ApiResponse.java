package ma.hps.powercard.dto;

import java.io.Serializable;

import com.google.gson.JsonObject;

import ma.hps.powercard.compliance.utils.GsonHelper;

public class ApiResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected boolean EXCEPTION;
	
	protected Object RESULT;

	public ApiResponse() {}
	
	public ApiResponse(boolean exception, Object result) {
		setEXCEPTION(exception);
		setRESULT(result);
	}
	
	/**
	 * Returns the JSON string representation of the object ApiResponse
	 * @return String representing the JSON object.
	 */
	public String toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("EXCEPTION", this.isEXCEPTION());
        json.addProperty("RESULT", GsonHelper.getGson().toJson(this.getRESULT()));
        return GsonHelper.getGson().toJson(json);
	}

	public void setEXCEPTION(boolean exception) {
		EXCEPTION = exception;
	}
	
	public boolean isEXCEPTION() {
		return EXCEPTION;
	}

	public Object getRESULT() {
		return RESULT;
	}
	
	public void setRESULT(Object result) {
		RESULT = result;
	}
	
}
