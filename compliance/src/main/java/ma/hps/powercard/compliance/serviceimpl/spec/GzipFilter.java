/**
 *  Copyright 2003-2007 Luck Consulting Pty Ltd
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * modified from ehcache-web 
 * 
 * we should be able to toggle the gzip filter in runtime, based on 
 * the value of env variable EnvVariables.DISABLE_GZIP_RESPONSE_PROPERTY_NAME
 * 
 * */
package ma.hps.powercard.compliance.serviceimpl.spec;

import net.sf.ehcache.constructs.web.GenericResponseWrapper;
import net.sf.ehcache.constructs.web.ResponseUtil;
import net.sf.ehcache.constructs.web.filter.Filter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;


import ma.hps.powercard.utils.EnvVariables;

/**
 * Provides GZIP compression of responses.
 * <p/>
 * See the filter-mappings.xml entry for the gzip filter for the URL patterns which will be gzipped. At present this
 * includes .jsp, .js and .css.
 * <p/>
 * 
 * @author <a href="mailto:gluck@thoughtworks.com">Greg Luck</a>
 * @author <a href="mailto:amurdoch@thoughtworks.com">Adam Murdoch</a>
 * @version $Id: GzipFilter.java 744 2008-08-16 20:10:49Z gregluck $
 */

public class GzipFilter extends Filter {

    /**
     * Performs initialization.
     * @param filterConfig
     */
    protected void doInit(FilterConfig filterConfig) throws Exception {
        //nothing required.
    }


    /**
     * A template method that performs any Filter specific destruction tasks.
     * Called from {@link #destroy()}
     */
    protected void doDestroy() {
        //noop
    }

    /**
     * Performs the filtering for a request.
     */
    protected void doFilter(final HttpServletRequest request, final HttpServletResponse response,
                            final FilterChain chain) throws Exception {

        if (gzipEnabled() && acceptsEncoding(request, "gzip")) {

            // Create a gzip stream
            final ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            final GZIPOutputStream gzout = new GZIPOutputStream(compressed);

            // Handle the request
            final GenericResponseWrapper wrapper = new GenericResponseWrapper(response, gzout);
            chain.doFilter(request, wrapper);
            wrapper.flush();

            gzout.close();

            //return on error or redirect code, because response is already committed
            int statusCode = wrapper.getStatus();
            if (statusCode != HttpServletResponse.SC_OK) {
                return;
            }

            //Saneness checks
            byte[] compressedBytes = compressed.toByteArray();
            boolean shouldGzippedBodyBeZero = ResponseUtil.shouldGzippedBodyBeZero(compressedBytes, request);
            boolean shouldBodyBeZero = ResponseUtil.shouldBodyBeZero(request, wrapper.getStatus());
            if (shouldGzippedBodyBeZero || shouldBodyBeZero) {
                compressedBytes = new byte[0];
            }

            // Write the zipped body
            ResponseUtil.addGzipHeader(response);
            response.setContentLength(compressedBytes.length);

            response.getOutputStream().write(compressedBytes);
            
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * Determine whether the user agent accepts GZIP encoding. This feature is part of HTTP1.1.
     * If a browser accepts GZIP encoding it will advertise this by including in its HTTP header:
     * <p/>
     * <code>
     * Accept-Encoding: gzip
     * </code>
     * <p/>
     * Requests which do not accept GZIP encoding fall into the following categories:
     * <ul>
     * <li>Old browsers, notably IE 5 on Macintosh.
     * <li>Internet Explorer through a proxy. By default HTTP1.1 is enabled but disabled when going
     * through a proxy. 90% of non gzip requests seen on the Internet are caused by this.
     * </ul>
     * As of September 2004, about 34% of Internet requests do not accept GZIP encoding.
     *
     * @param request
     * @return true, if the User Agent request accepts GZIP encoding
     */
    protected boolean acceptsGzipEncoding(HttpServletRequest request) {
        return acceptsEncoding(request, "gzip");
    }

    /**
     * @return true, if the environment variable of EnvVariables.DISABLE_GZIP_REPONSE_PROPERTY_NAME is set to "false" or not set.
     * 		   false otherwise.
     */
    private boolean gzipEnabled() {
    	try {
    		return !"true".equals(System.getProperty(EnvVariables.DISABLE_GZIP_RESPONSE_PROPERTY_NAME));
    	} catch (Exception e) {}
    	return false;
    }
    

}
