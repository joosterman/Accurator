package org.sealinc.accurator.server.service;

import static com.googlecode.objectify.ObjectifyService.ofy;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sealinc.accurator.server.User;
import org.sealinc.accurator.server.UserProfileEntry;
import org.sealinc.accurator.server.Utility;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cmd.Query;

public class UserProfileServlet extends HttpServlet {

	private static final long serialVersionUID = 1842380538040263274L;
	private Gson gson = new Gson();

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Utility.setNoCacheJSON(response);
		// do we have all the required parameters?
		if (!isValidRequest(request)) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Required parameters not provided");
			return;
		}

		final String user = request.getParameter("user");
		final String dimension = request.getParameter("dimension");
		final String scope = request.getParameter("scope");
		final String provider = request.getParameter("provider");

		// get the user
		LoadResult<User> dbUser = ofy().load().type(User.class).filter("URI", user).first();
		// if the user does not exist, send an empty list
		User u = dbUser.now();
		if (u == null) {
			response.getWriter().write(gson.toJson(new UserProfileEntry[] {}));
			return;
		}

		// build the query
		Query<UserProfileEntry> query = ofy().load().type(UserProfileEntry.class).ancestor(u);
		// if the dimension is given
		if (dimension != null) query = query.filter("dimension =", dimension);
		if (scope != null) query = query.filter("scope =", scope);
		if (provider != null) query = query.filter("provider =", provider);

		// execute the query
		List<UserProfileEntry> entries = query.list();

		// output the list as JSON
		response.getWriter().write(gson.toJson(entries));
	}

	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// do we have all the required parameters?
		if (!isValidRequest(request)) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format("Required parameters not provided"));
			return;
		}
		// get the params
		final String user = request.getParameter("user");
		final String dimension = request.getParameter("dimension");
		final String scope = request.getParameter("scope");
		final String provider = request.getParameter("provider");
		final String value = request.getParameter("value");

		// get the user if it exists
		LoadResult<User> refu = ofy().load().type(User.class).filter("URI =", user).first();
		final User u = refu.now();
		if (u == null) {
			// if the user does not exist the user profile entry can also not exist
			return;
		}
		else {
			// start deleting
			ofy().transact(new VoidWork() {
				@Override
				public void vrun() {
					// get existing UPE
					Query<UserProfileEntry> query = ofy().load().type(UserProfileEntry.class).ancestor(u);
					if (dimension != null) query = query.filter("dimension =", dimension);
					if (scope != null) query = query.filter("scope =", scope);
					if (provider != null) query = query.filter("provider =", provider);
					if (value != null) query = query.filter("value =", provider);

					// remove entries
					List<UserProfileEntry> entries = query.list();
					if (entries.size() > 0) {
						ofy().delete().entities(entries);
					}
				}
			});
		}
	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// do we have all the required parameters?
		if (!isValidRequest(request)) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format("Required parameters not provided"));
			return;
		}

		// get the params
		final String user = request.getParameter("user");
		final String dimension = request.getParameter("dimension");
		final String scope = request.getParameter("scope");
		final String provider = request.getParameter("provider");
		String value = request.getParameter("value");
		final String valueType = request.getParameter("valueType");

		// convert the value to the correct value (via HTTP PUT starts always as
		// string)
		Object o = null;
		try {
			// if a type is not given, store as string
			if (valueType == null || "string".equals(valueType)) {
				o = value;
			}
			else if ("int".equals(valueType)) {
				int i = Integer.parseInt(value);
				o = i;
			}
			else if ("double".equals(valueType)) {
				double d = Double.parseDouble(value);
				o = d;
			}
			else if ("date".equals(valueType)) {
				Date d = SimpleDateFormat.getDateTimeInstance().parse(value);
				o = d;
			}
			else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						String.format("Value type unknown: '%s'. Value not saved.", valueType));
				return;
			}
		}
		catch (Exception ex) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					String.format("Could not convert value '%s' as %s. Value not saved. Exception: %s", value, valueType, ex.toString()));
			return;
		}
		final Object typedVal = o;

		// get the user if it exists
		User storedUser = null;
		LoadResult<User> dbUser = ofy().load().type(User.class).filter("URI =", user).first();
		//if the user exists in the DB 
		User dbUsero = dbUser.now();
		if(dbUsero != null){
			storedUser = dbUsero;
		}
		else{
		// if user does not exist, create the user
			storedUser = new User();
			storedUser.URI = user;
			ofy().save().entity(storedUser).now();
		}
		//stored user now is a existing user in the DB 
		final User finalUser = storedUser;

		// Value is converted, start transaction
		ofy().transact(new VoidWork() {

			@Override
			public void vrun() {

				// check if the UPE already exists and create otherwise
				// build the query
				Query<UserProfileEntry> query = ofy().load().type(UserProfileEntry.class).ancestor(finalUser);
				if (dimension != null) query = query.filter("dimension =", dimension);
				if (scope != null) query = query.filter("scope =", scope);
				if (provider != null) query = query.filter("provider =", provider);

				// execute the query
				LoadResult<UserProfileEntry> refentry = query.first();
				UserProfileEntry entry = refentry.now();
				if (entry == null) {
					entry = new UserProfileEntry();
					entry.user = Key.create(User.class, finalUser.id);
					entry.dimension = dimension;
					entry.provider = provider;
					entry.scope = scope;
				}
				// set the value
				entry.value = typedVal;

				// (re)save the entity
				ofy().save().entity(entry).now();
			}
		});
	}

	/**
	 * Returns whether the request valid
	 * 
	 * @param request
	 * @return
	 */
	private boolean isValidRequest(HttpServletRequest request) {
		String user = request.getParameter("user");
		String dimension = request.getParameter("dimension");
		String value = request.getParameter("value");
		String provider = request.getParameter("provider");

		String method = request.getMethod();
		if ("GET".equals(method)) {
			return user != null && dimension != null;
		}
		else if ("PUT".equals(method)) {
			return user != null && dimension != null && value != null;
		}
		else if ("DELETE".equals(method)) {
			return user != null && dimension != null && provider != null && value!=null;
		}
		else {
			return false;
		}
	}
}
