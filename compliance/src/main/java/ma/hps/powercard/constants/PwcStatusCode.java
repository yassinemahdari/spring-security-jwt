package ma.hps.powercard.constants;

public enum PwcStatusCode {

	SUCCESS("0000"),
	PASSWORD_IN_DICTIONARY("0111"),
	PASSWORD_ALREADY_USED("0112"),
	PASSWORD_LIKE_USERNAME("0113"),
	PASSWORD_LIKE_USER_ID("0114"),
	;

	private final String status;
	
	private PwcStatusCode(final String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return status;
	}
	
}
