/**
 * 
 */
package org.sealinc.accurator.server.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sealinc.accurator.server.Utility;
import org.sealinc.accurator.server.WebResponseData;
import com.google.appengine.api.urlfetch.HTTPMethod;

/**
 * @author oosterman
 */
public class UtilityServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String proxyUrlParameter = "proxy_url";
	private static final List<String> allowedHosts = Arrays.asList(new String[] { "localhost", "127.0.0.1" });

	private Map<String, String[]> getParams(HttpServletRequest request) {
		// get the immutable parameters
		@SuppressWarnings("unchecked")
		Map<String, String[]> params = request.getParameterMap();
		// make a new map that is mutable
		Map<String, String[]> params2 = new HashMap<String, String[]>(params);
		// remove the proxy url parameter
		params2.remove(proxyUrlParameter);
		return params2;
	}

	private boolean isSameDomain(HttpServletRequest request) {
		String host = request.getRemoteHost();
		return allowedHosts.contains(host);
	}

	private void proxyRequest(HttpServletRequest request, HttpServletResponse response, HTTPMethod method) throws IOException {
		try {
			if (!isSameDomain(request)) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			Utility.setNoCacheJSON(response);
			String url = request.getParameter(proxyUrlParameter);
			Map<String, String[]> params = getParams(request);

			// read in the stuff, if able
			String outputData = null;
			StringBuilder builder = new StringBuilder();
			try {
				BufferedReader reader = request.getReader();
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					builder.append("\n");
				}
				if (builder.length() > 0) outputData = builder.toString();
			}
			catch (Exception ex) {
				// no data has been sent to read
			}

			if (url != null) {
				URL u = new URL(url);
				WebResponseData data = Utility.doRequest(method, u, request.getContentType(), request.getHeader("Accept"), params, outputData);
				response.setStatus(data.statusCode);
				response.getWriter().write(data.data);
			}
			else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		catch (Exception ex) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		proxyRequest(request, response, HTTPMethod.GET);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		proxyRequest(request, response, HTTPMethod.POST);
	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		proxyRequest(request, response, HTTPMethod.PUT);
	}

	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		proxyRequest(request, response, HTTPMethod.DELETE);
	}
}
