package ma.hps.powercard.compliance.serviceimpl.spec;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestWrapper extends HttpServletRequestWrapper {

	private final Map<String, String[]> modifiableParameters;

	private Map<String, String[]> allParameters = null;

	// allowed characters in some screens, for command injection prevention.
	public final static Pattern cmdInjectionPattern = Pattern.compile("^[a-zA-Z0-9./\\-_ ?%\\p{L}]+");
	
	public static Pattern[] patterns = new Pattern[]{
	            // Script fragments
	            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
	            // src='...'
	            Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	            // lonely script tags
	            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
	            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	            // eval(...)
	            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	            // expression(...)
	            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	            // javascript:...
	            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
	            // vbscript:...
	            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
	            // onload(...)=...
	            Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	            //sql_sign*1
	            Pattern.compile("/((\\%3D)|(=))[^\\n]*((\\%27)|(\')|(\\-\\-)|(\\%3B)|(;))/i",Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	            //sql_sign*2
	            Pattern.compile("/((\\%27)|(\'))union/ix",Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	            //sql_sign*3
	            Pattern.compile("/\\w*((\\%27)|(\\'))((\\%6F)|o|(\\%4F))((\\%72)|r|(\\%52))/ix",Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	            //sql_sign*4
	            Pattern.compile("insert|update|delete|having|drop|truncate|(\'|%27).(and|or).(\'|%27)|(\'|%27).%7C{0,2}|%7C{2}",Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	        };

	/**
	 * Create a new request wrapper that will merge additional parameters into the
	 * request object without prematurely reading parameters from the original
	 * request.
	 * 
	 * @param request
	 * @param additionalParams
	 */
	public RequestWrapper(final HttpServletRequest request, final Map<String, String[]> additionalParams) {
		super(request);
		modifiableParameters = new TreeMap<String, String[]>();
		modifiableParameters.putAll(additionalParams);
	}

	@Override
	public String getParameter(final String name) {
		String[] strings = getParameterMap().get(name);
		if (strings != null) {
			return strings[0];
		}
		return super.getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		if (allParameters == null) {
			allParameters = new TreeMap<String, String[]>();
			allParameters.putAll(super.getParameterMap());
			allParameters.putAll(modifiableParameters);
		}
		// Return an unmodifiable collection because we need to uphold the interface
		// contract.
		return Collections.unmodifiableMap(allParameters);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(getParameterMap().keySet());
	}

	@Override
	public String[] getParameterValues(final String name) {
		String[] parameters = getParameterMap().get(name);
		if (parameters != null) {
			int count = parameters.length;
			String[] encodedValues = new String[count];
			for (int i = 0; i < count; i++) {
				encodedValues[i] = getParameterMap().get(name)[i];
			}
			return encodedValues;
		} else {
			return null;
		}
	}

//	@Override
//	public String getHeader(String name) {
//		String value = super.getHeader(name);
//		return stripXSS(value);
//	}

//	private String stripXSS(String value) {
//		if (value != null) {
//			// NOTE: It's highly recommended to use the ESAPI library and uncomment the
//			// following line to
//			// avoid encoded attacks.
//			// value = ESAPI.encoder().canonicalize(value);
//
//			// Avoid null characters
//			value = value.replaceAll("\0", "");
//
//			// Remove all sections that match a pattern
//			for (Pattern scriptPattern : patterns) {
//				value = scriptPattern.matcher(value).replaceAll("");
//			}
//		}
//		return value;
//	}

}