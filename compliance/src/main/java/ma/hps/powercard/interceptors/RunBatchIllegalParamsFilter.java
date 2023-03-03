package ma.hps.powercard.interceptors;

import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.google.gson.JsonElement;
import ma.hps.powercard.compliance.utils.GsonHelper;

// HACK: Instead of addressing the issue here, it needs to be handled in the code that uses `task_name` naively.
public class RunBatchIllegalParamsFilter implements Filter {
	
	private final Logger LOG = Logger.getLogger(RunBatchIllegalParamsFilter.class);

	private static final String BAD_REQUEST_MSG = "Malicious parameter caught";
	private static final String GUARDED_ENDPOINT = "administration/Pcrd_flex_call_batch_Service/execute_from_screen";
    private static final Pattern SAFE_STRING_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;

		if (isEndointGuarded(req.getPathInfo()) && !paramsValid(req)) {
			LOG.error(BAD_REQUEST_MSG + " while accessing: " + req.getPathInfo());
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_REQUEST_MSG);
			return;
		}
		
		chain.doFilter(request, response);
	}
	
	private static boolean isEndointGuarded(String path) {
		return path.contains(GUARDED_ENDPOINT);
	}

	private boolean paramsValid(HttpServletRequest request) {

		final String paramName = "execute_from_screenInVO";
		final String param = request.getParameter(paramName);
		
		if (!StringUtils.hasText(param))
			return true;
		
		// {
		//   "pcard_tasks_reVO": {
		//     "bank_code":"000001",
		//     "task_name":"load_visa_rawdata", << This is what we're after.
		//     "task_wording":"VISA RAWDATA"
		//     ...
		//   },
		//   ...
		// }
		
		try {
			
			final JsonElement json = GsonHelper.getGson().fromJson(param, JsonElement.class);
			if (json.isJsonNull())
				return true;

			final JsonElement pcardTaskCursorVO = json.getAsJsonObject().get("pcard_tasks_reVO");
			
			if (pcardTaskCursorVO.isJsonNull())
				return true;
			
			final JsonElement taskName = pcardTaskCursorVO.getAsJsonObject().get("task_name");

			if (taskName.isJsonNull())
				return true;
			
			return isTextValid(taskName.getAsString());
			
		} catch (Exception e) {
			LOG.error("Could not validate DTO => passing through");
			LOG.debug(e);
		}
		
		return true;
	}

	boolean isTextValid(String value) {

		if (!StringUtils.hasText(value))
			return true;
		
		return SAFE_STRING_PATTERN.matcher(value).matches();
	}

	@Override
	public void destroy() {
	}

}
