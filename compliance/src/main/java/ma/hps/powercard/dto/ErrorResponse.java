package ma.hps.powercard.dto;

public class ErrorResponse extends ApiResponse {

	private static final long serialVersionUID = 1L;

	public ErrorResponse() {
		EXCEPTION = true;
	}
	
	public ErrorResponse(Object result) {
		this();
		setRESULT(result);
	}

	public static ErrorResponse from(Object result) {
		return new ErrorResponse(result);
	}

	@Deprecated
	@Override
	public void setEXCEPTION(boolean exception) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Use ApiResponse instead");
	}
	
}
