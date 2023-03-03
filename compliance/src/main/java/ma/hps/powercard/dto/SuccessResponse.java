package ma.hps.powercard.dto;

public class SuccessResponse extends ApiResponse {
	
	private static final long serialVersionUID = 1L;

	public SuccessResponse() {
		EXCEPTION = false;
	}

	public SuccessResponse(Object result) {
		this();
		setRESULT(result);
	}

	public static SuccessResponse from(Object result) {
		return new SuccessResponse(result);
	}

	@Deprecated
	@Override
	public void setEXCEPTION(boolean exception) {
		throw new UnsupportedOperationException("Use ApiResponse instead");
	}
	
}
