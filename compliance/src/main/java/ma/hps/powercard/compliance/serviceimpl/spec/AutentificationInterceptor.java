package ma.hps.powercard.compliance.serviceimpl.spec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.auth0.jwt.exceptions.JWTVerificationException;


public class AutentificationInterceptor extends HandlerInterceptorAdapter {
	
	private static final String ACCESS_REFRESH   = "Token is not valid. Please authenticate again!";
	private static final String ACCESS_FORBIDDEN = "Access forbidden!";

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
        
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS, HEAD");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, Authorization");
		
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if ( request.getMethod().equals("OPTIONS") )
			return true;
		
		// get request headers to extract jwt token
		String authorization_str = request.getHeader(HttpHeaders.AUTHORIZATION);

		if ( request.getPathInfo().contains("compliance/getSessionKey") || 
				request.getPathInfo().contains("compliance/login")         || 
				request.getPathInfo().contains("compliance/logout") || 
				request.getPathInfo().contains("compliance/User_passwordsService/changePassword")
			)
		{
			return true;
		}

		// block access if no authorization information is provided
		if( authorization_str == null || authorization_str.isEmpty() )
		{
			// return HTTP 401 if jwt is not provider
			//throw new WebApplicationException(ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, ACCESS_REFRESH));
			return false;
		}
		// Get Algorithm
		String jwt = authorization_str.substring("Bearer".length()).trim();
		
		if( jwt == null || jwt.isEmpty() )
		{
			// return HTTP 401 if jwt is not provider
			throw new WebApplicationException(ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, ACCESS_REFRESH));
		}
		
		HttpSession session = CustomSessionListener.sessions.get(jwt);
		
		if( session == null)
		{
			// return HTTP 401 if jwt is not provider
			throw new WebApplicationException(ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, ACCESS_REFRESH));
		}

		RestServiceContextStore restServiceContext =(RestServiceContextStore)(session.getAttribute("RestServiceContext"));

		// try to decode the jwt - deny access if no valid token provided
		try {
			SecurityKeysProvider.verifyJwtToken(restServiceContext.getKey().getJwtAlgorithm(), jwt);
		} catch ( JWTVerificationException e) {
			// return HTTP 403 if jwt is not provider
			CustomSessionListener.sessions.remove(jwt);
			session.invalidate();
			throw new WebApplicationException(ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, ACCESS_FORBIDDEN));
		}
		
		InputStream input=addCustoFormParam(request.getInputStream(), "jwt", jwt);
		
//		request.set
		
		return true;
	}
	private InputStream addCustoFormParam(InputStream entityInputStream, String paramName, String paramValue) {
    	
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        final StringBuilder stringCustoParam = new StringBuilder();
        try {
            if (entityInputStream.available() > 0) {
                ReaderWriter.writeTo(entityInputStream, out);
		        stringCustoParam.append( new String(out.toByteArray())).append("&").append(paramName).append("=").append(paramValue);
                entityInputStream = new ByteArrayInputStream(String.valueOf(stringCustoParam).getBytes());
            }
            else {
            	stringCustoParam.append(paramName).append("=").append(paramValue);
            	entityInputStream = new ByteArrayInputStream(String.valueOf(stringCustoParam).getBytes());
            }
        } catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
        }
        return entityInputStream;
    }
	
	
	
}