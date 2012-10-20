package org.sealinc.accurator.server.service;

import static com.googlecode.objectify.ObjectifyService.ofy;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sealinc.accurator.server.Utility;
import org.sealinc.accurator.shared.UserProfileEntry;
import com.google.gson.Gson;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;

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

		// get list if values from all from all topics
		if (topic == null) {
			Query<UserProfileEntry> q = ofy().load().type(UserProfileEntry.class).filter("user", user).filter("type", type);
			List<UserProfileEntry> entries = q.list();
			response.getWriter().write(gson.toJson(entries));
		}
		// get for the one topic
		else {
			Query<UserProfileEntry> q = ofy().load().type(UserProfileEntry.class).filter("user", user).filter("type", type).filter("topic", topic);
			Ref<UserProfileEntry> ref = q.first();
			if (ref.getValue() == null) response.getWriter().write(gson.toJson(null));
			else {
				response.getWriter().write(gson.toJson(ref.getValue().value));
			}
		}
	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (!isValidRequest(request)) response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Required parameters not provided");
		String user = request.getParameter("user");
		String type = request.getParameter("type");
		String topic = request.getParameter("topic");
		String value = request.getParameter("value");

		// check if exists
		Query<UserProfileEntry> q;
		if (topic == null) {
			q = ofy().load().type(UserProfileEntry.class).filter("user", user).filter("type", type);
		}
		else {
			q = ofy().load().type(UserProfileEntry.class).filter("user", user).filter("type", type).filter("topic", topic);
		}
		List<UserProfileEntry> entries = q.list();
		// Do not put if there are more than 1 of these entities
		if (entries.size() > 1) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not update. Multiple entities found this user and type.");
		}
		else {
			UserProfileEntry entry = null;
			// update or create
			if (entries.isEmpty()) {
				entry = new UserProfileEntry();
				entry.user = user;
				entry.topic = topic;
				entry.type = type;
			}
			else {
				entry = entries.get(0);
			}

			// determine type based on up-type
			Object typedVal = value;

			if ("expertise".equals(type)) {
				typedVal = Double.parseDouble(value);
			}
			entry.value = typedVal;
			// asynchronous save
			ofy().save().entity(entry);
			response.setStatus(HttpServletResponse.SC_OK);
		}
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
		String value = request.getParameter("value");

		String method = request.getMethod();
		if ("GET".equals(method)) {
			return user != null && type != null;
		}
		else if ("PUT".equals(method)) {
			return user != null && type != null && value != null && !value.isEmpty();
		}
		else {
			return false;
		}
	}
}
