package org.sealinc.accurator.server.service;

import static com.googlecode.objectify.ObjectifyService.begin;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sealinc.accurator.server.Utility;
import org.sealinc.accurator.shared.UserProfileEntry;
import com.google.gson.Gson;
import com.googlecode.objectify.Ref;

public class UserProfileServlet extends HttpServlet {

	private static final long serialVersionUID = 1842380538040263274L;
	private Gson gson = new Gson();

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Utility.setNoCacheJSON(response);
		if (!isValidRequest(request)) response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Required parameters not provided");

		String user = request.getParameter("user");
		String type = request.getParameter("type");
		String topic = request.getParameter("topic");

		Ref<UserProfileEntry> ref = begin().load().type(UserProfileEntry.class).filter("user", user).filter("type", type).filter("topic", topic).first();
		if (ref.getValue() == null) response.getWriter().write(gson.toJson(null));
		else {
			response.getWriter().write(gson.toJson(ref.getValue().value));
		}

	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Utility.setNoCacheJSON(response);
		if (!isValidRequest(request)) response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Required parameters not provided");
		String user = request.getParameter("user");
		String type = request.getParameter("type");
		String topic = request.getParameter("topic");
		String value = request.getParameter("value");
		
		// check if exists
		Ref<UserProfileEntry> ref = begin().load().type(UserProfileEntry.class).filter("user", user).filter("type", type).filter("topic", topic).first();
		UserProfileEntry entry = ref.getValue();
		if (entry == null) {
			entry = new UserProfileEntry();
			entry.user = user;
			entry.topic = topic;
			entry.type = type;
		}
		//determine type based on up-type
		Object typedVal = null;;
		if ("expertise".equals(type)) typedVal = Integer.parseInt(value);
		entry.value = typedVal;
		begin().save().entity(entry).now();
		response.setStatus(HttpServletResponse.SC_OK);

	}

	/**
	 * Returns the message to output to the user if the request was not valid or
	 * null if the request was not valid
	 * 
	 * @param request
	 * @return
	 */
	private boolean isValidRequest(HttpServletRequest request) {
		String user = request.getParameter("user");
		String type = request.getParameter("type");
		String topic = request.getParameter("topic");
		String value = request.getParameter("value");
		
		String method= request.getMethod();
		if("GET".equals(method)){
			return user != null && type != null && topic != null;
		}
		else if ("PUT".equals(method)){
			return user != null && type != null && topic != null && value != null;
		}
		else
		{
			return false;
		}
	}
}
