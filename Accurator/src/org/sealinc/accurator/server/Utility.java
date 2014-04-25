package org.sealinc.accurator.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.sealinc.accurator.shared.Config;
import org.sealinc.accurator.shared.Namespace;
import org.sealinc.accurator.shared.RDFObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.utils.SystemProperty;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gwt.http.client.Response;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.resultset.ResultSetException;

public class Utility {

	public static final Gson gson = new Gson();
	public static final JsonParser jsonParser = new JsonParser();
	private static RDFVisitor visitor;
	private final static Logger logger = LoggerFactory.getLogger(Utility.class.getName());
	private static String cookie = null;
	private static Date cookieDate = null;

	private Utility() {}

	/**
	 * Creates triples from the RDFObject o and stores them in a new Model
	 * 
	 * @param o
	 * @return
	 */
	public static Model toRDF(RDFObject o) {
		return toRDF(o, ModelFactory.createDefaultModel());
	}

	/**
	 * Makes sure that the response from a servlet is not chached (in any browser)
	 * 
	 * @param response
	 */
	public static void setNoCacheJSON(HttpServletResponse response) {
		// set to return JSON
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		// Disable cache, also for IE
		// Set to expire far in the past.
		response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
		// Set standard HTTP/1.1 no-cache headers.
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		// Set standard HTTP/1.0 no-cache header.
		response.setHeader("Pragma", "no-cache");
	}

	private static RDFNode createRDFNode(Object o, Model m) {
		RDFNode result = null;
		// URI / string
		if (o instanceof String) {
			String s = (String) o;
			// uri
			if (s.startsWith("http")) result = m.createResource(s);
			// plain string
			else result = m.createTypedLiteral(s);
		}
		else if (o instanceof Date) {
			Date d = (Date) o;
			Calendar c = GregorianCalendar.getInstance();
			c.setTime(d);
			result = m.createTypedLiteral(c);
		}
		// other literals
		else {
			result = m.createTypedLiteral(o);
		}
		return result;
	}

	/**
	 * Creates triples from the RDFObject o and stores them in Model m
	 * 
	 * @param o
	 * @param m
	 * @return
	 */
	public static Model toRDF(RDFObject o, Model m) {
		if (m == null) {
			return null;
		}
		else if (o == null) {
			return m;
		}
		String propertyNS, propertyName;
		Property p;
		Object value;
		// create resource
		Resource r = m.createResource(o.uri);
		for (Field field : o.getClass().getDeclaredFields()) {
			// check Namespace annotation
			Namespace ns = field.getAnnotation(Namespace.class);
			if (ns != null) {
				// create property
				try {
					propertyNS = ns.value();
					propertyName = field.getName();
					p = m.createProperty(propertyNS, propertyName);
					value = field.get(o);

					if (value != null) m.add(r, p, createRDFNode(value, m));
				}
				catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return m;
	}

	private static RDFVisitor getRDFVisitor() {
		if (visitor == null) {
			visitor = new RDFVisitor() {

				@Override
				public Object visitURI(Resource r, String uri) {
					return uri;
				}

				@Override
				public Object visitLiteral(Literal l) {
					return l.getValue();
				}

				@Override
				public Object visitBlank(Resource r, AnonId id) {
					return null;
				}
			};
		}
		return visitor;
	}

	public static int getStatusCode(String url) {
		BufferedReader reader = null;
		try {
			URL u = new URL(url);

			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			int code = con.getResponseCode();
			String line;
			String result = "";
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((line = reader.readLine()) != null) {
				result += line;
			}
			reader.close();
			logger.info(String.format("Get statuscode response:(%s) %s", code, result));
			return code;
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.SC_INTERNAL_SERVER_ERROR;
		}
	}

	public static boolean register(String user, String realname, String password) {
		// encode the real name
		try {
			realname = URLEncoder.encode(realname, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}

		String url = String.format("%s?user=%s&realname=%s&password=%s", Config.adminRegisterUserURL, user, realname, password);
		int statusCode = getStatusCode(url);
		if (statusCode == 200) {
			return true;
		}
		else return false;
	}

	/**
	 * Login the Server component with the configured credentials
	 * 
	 * @return true if successfull login (or session not expired)
	 */
	public static boolean login() {
		// check if the cookie is at most 5 minutes old
		if (cookieDate != null && cookie != null) {
			long time = new Date().getTime() - cookieDate.getTime();
			if (time < (5 * 1000 * 60)) {
				return true;
			}
		}
		// get new cookie
		String url = String.format("%s?user=%s&password=%s", Config.loginURL, Config.adminUsername, Config.adminPassword);
		int code = -1;
		try {
			URL u = new URL(url);
			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("GET");
			con.setUseCaches(false);
			con.addRequestProperty("Cache-Control", "no-cache,max-age=0");
			con.addRequestProperty("Pragma", "no-cache");
			con.connect();
			code = con.getResponseCode();
			cookie = con.getHeaderField("set-cookie");
			if (code == 200 && cookie != null) {
				logger.info("Server login successful");
				cookie = cookie.substring(0, cookie.indexOf(";")).trim();
				cookieDate = new Date();
				return true;
			}
			else {
				logger.warn("Could not log in. responsecode: " + code + " Cookie: " + cookie + "\nHeaders: " + con.getHeaderFields());
				return false;
			}
		}
		catch (Exception e) {
			logger.warn("Could not log in: " + e.toString());
			return false;
		}
	}

	/**
	 * Logout the current logged in Server component.
	 * 
	 * @return If the logout was successful or was not logged on
	 */
	public static boolean logout() {
		Integer code = Utility.getStatusCode(Config.logoutURL);
		return code != null && code == 200;
	}

	public static String getHTMLContent(String url) {
		URL u;
		try {
			u = new URL(url);
			URLConnection con = u.openConnection();
			con.setConnectTimeout(0);
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				builder.append(line);
				line = reader.readLine();
			}
			reader.close();
			return builder.toString();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Performs a HTTP request using the specified method. The data is written to
	 * the outputstream or, if data is null, the provided parameters in key/value
	 * pairs.
	 * 
	 * @param method
	 * @param url
	 * @param contentType
	 * @param params
	 * @param data
	 * @return
	 */
	public static WebResponseData doRequest(HTTPMethod method, URL url, String contentType, String accepts, Map<String, String[]> params, String outputData) {
		WebResponseData data = new WebResponseData();
		try {
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(method.name());
			// write data when there are parameters or outputData
			if ((params != null && params.size() > 0) || outputData != null) {
				// indicate that we will ne writing
				con.setDoOutput(true);
				// set the right method
				con.setRequestMethod(method.name());
				String charset = "UTF-8";
				con.setRequestProperty("Content-Type", contentType);
				con.setRequestProperty("Accept", accepts);
				// stringify the request parameters
				StringBuilder builder = new StringBuilder();
				for (String key : params.keySet()) {
					for (String value : params.get(key)) {
						builder.append(key);
						builder.append("=");
						builder.append(URLEncoder.encode(value, charset));
						builder.append("&");
					}
				}

				// write either the data or the parameters
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				if (outputData == null) wr.writeBytes(builder.toString());
				else wr.writeBytes(outputData);
				wr.flush();
				wr.close();
			}
			con.connect();

			data.statusCode = con.getResponseCode();
			String line;
			StringBuilder result = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((line = reader.readLine()) != null) {
				result.append(line);
				result.append("\n");
			}
			reader.close();
			data.data = result.toString();
		}
		catch (Exception ex) {
			data.statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			data.data = ex.toString();
		}
		return data;
	}

	/**
	 * @param url
	 * @return Parsed JSON or null if JSON could not be parsed.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getParsedJSONFromURL(String url) {
		String content = getHTMLContent(url);
		if (content == null) return null;
		else {
			Type typeOfT = new TypeToken<T>() {
			}.getType();
			try {
				Object o = gson.fromJson(content, typeOfT);
				return (T) o;
			}
			catch (JsonSyntaxException e) {
				// e.printStackTrace();
				return null;
			}

		}
	}

	private static HttpURLConnection prepareDataUpload() throws IOException {
		login();
		URL u = new URL(Config.adminComponentUploadDataURL);
		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		// add cookie only on live server
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
			con.setRequestProperty("Cookie", cookie);
		}
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		con.connect();

		return con;
	}

	public static boolean uploadData(ResultSet rs) {
		Model m = toTriples(rs);
		return uploadData(m);
	}

	public static boolean uploadData(Statement st) {
		Model m = ModelFactory.createDefaultModel();
		m.add(st);
		return uploadData(m);
	}

	public static boolean uploadData(Model m) {
		logger.info("Started uploading data");
		HttpURLConnection con = null;
		PrintStream out;
		BufferedReader reader = null;
		int responseCode = 0;
		try {
			con = prepareDataUpload();

			out = new PrintStream(con.getOutputStream());
			String s = String.format("baseURI=%s&data=", Config.adminComponentBaseURI);
			out.print(s);
			logger.info("Uploading data at: " + con.getURL().toString() + s);
			RDFWriter writer = m.getWriter("RDF/XML");
			writer.write(m, out, null);
			out.close();
			// read response
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			String result = "";
			while ((line = reader.readLine()) != null) {
				result += line;
			}
			reader.close();
			responseCode = con.getResponseCode();
			logger.info(String.format("Add data response: (%s) %s", responseCode, result));
		}
		catch (IOException e) {
			e.printStackTrace();
			logger.warn("Upload data failed: " + e.toString());
			return false;
		}
		return responseCode == 200;
	}

	/**
	 * @param sparql
	 * @return The resulting ResultSet of the query or null if the query did not
	 *         result a ResultSet (query failed/wrong endpoint etc.)
	 */
	private static ResultSet getRDFFromEndpoint(String sparql) {
		try {
			URL url = new URL(String.format("%s?query=%s", Config.sparqlEndpoint, URLEncoder.encode(sparql, "UTF-8")));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.addRequestProperty("Accept", "application/sparql-results+xml");
			con.setReadTimeout(0);
			con.setRequestMethod("GET");
			con.setUseCaches(false);
			con.connect();
			ResultSet rs;
			InputStream stream = con.getInputStream();
			try {
				rs = ResultSetFactory.fromXML(stream);
			}
			catch (ResultSetException e) {
				rs = null;
			}
			return rs;
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Converts the values of first found variable from the executed sparql into a
	 * list of type T. Caller should make sure that resulting RDF can be converted
	 * to T
	 * 
	 * @param rs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getRDFAndConvertToList(String sparql) {
		ResultSet rs = getRDFFromEndpoint(sparql);
		List<T> ret = new ArrayList<T>();
		QuerySolution qs;
		RDFNode node;
		Object value;
		String varName = rs.getResultVars().get(0);
		while (rs.hasNext()) {
			qs = rs.next();
			node = qs.get(varName);
			value = node.visitWith(getRDFVisitor());
			if (value != null) ret.add((T) value);
		}
		return ret;
	}

	/**
	 * Assumes a ResultSet with at least the variables 'subject', 'predicate' and
	 * 'object'
	 * 
	 * @param rs
	 * @return Model loaded with triples from ResultSet
	 */
	private static Model toTriples(ResultSet rs) {
		QuerySolution qs;
		Model m = ModelFactory.createDefaultModel();
		while (rs.hasNext()) {
			qs = rs.next();
			Resource s = qs.getResource("subject");
			Property p = m.createProperty(qs.get("predicate").asNode().getURI());
			RDFNode o = qs.get("object");
			m.add(s, p, o);
		}
		return m;
	}

	public static <T extends RDFObject> T getObjectByURI(String uri, Class<T> clazz, String rdfType) {
		List<String> uris = new ArrayList<String>();
		uris.add(uri);
		List<T> objs = getObjectsByURI(uris, clazz, rdfType);
		if (objs.size() > 0) return objs.get(0);
		else return null;
	}

	public static <T extends RDFObject> List<T> getObjectsByURI(List<String> uris, Class<T> clazz, String rdfType) {
		// generate sparql
		StringBuilder sb = new StringBuilder();
		String lt = "<";
		String gt = ">";
		for (String uri : uris) {
			sb.append(lt);
			sb.append(uri);
			sb.append(gt);
			sb.append(" ");
		}
		String sparql = String.format(
				"%s SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object . ?subject rdf:type <%s> . VALUES ?subject { %s }}",
				Config.sparqlPrefixes, rdfType, sb.toString());
		List<T> cis = Utility.getObjects(sparql, clazz);
		return cis;
	}

	/**
	 * Returns all Object of Type T based on a sparql query
	 * 
	 * @param sparql with variables 'subject' 'predicate' and 'object'
	 * @param clazz Class of the resulting objects
	 * @return List of Objects of Type clazz or null if Objects of type T could
	 *         not created TODO: Set the value of an field based on the
	 *         namespace+name instead of only name
	 */
	private static <T extends RDFObject> List<T> getObjects(String sparql, Class<T> clazz) {
		ResultSet rs = getRDFFromEndpoint(sparql);
		List<T> objs = new ArrayList<T>();

		if (rs == null) return objs;
		QuerySolution qs = null;

		String uri, fieldName;
		Object fieldValue;
		T obj = null;
		RDFNode node;
		List<String> unknownFields = new ArrayList<String>();
		while (rs.hasNext()) {
			qs = rs.next();
			uri = qs.getResource("subject").getURI();
			// check if already exists
			try {
				obj = clazz.newInstance();
			}
			catch (InstantiationException e1) {
				e1.printStackTrace();
				return null;
			}
			catch (IllegalAccessException e1) {
				e1.printStackTrace();
				return null;
			}
			obj.uri = uri;
			int index = objs.lastIndexOf(obj);
			if (index >= 0) {
				obj = objs.get(index);
			}
			else {
				objs.add(obj);
			}
			fieldName = qs.get("predicate").asNode().getLocalName();
			node = qs.get("object");
			fieldValue = node.visitWith(getRDFVisitor());
			// add value to field of object if exists
			try {
				setFieldValue(obj, fieldName, fieldValue);
			}
			catch (Exception e) {
				if (!unknownFields.contains(fieldName)) unknownFields.add(fieldName);
			}
		}
		// notify of fields that do not exist
		logger.info("Class " + clazz.getName() + " does not have the following fields (accessable): \n" + unknownFields.toString());
		return objs;
	}

	private static Object convertValue(Object value, Type requiredType) {
		// Date
		if (requiredType.equals(Date.class)) {
			if (value instanceof XSDDateTime) {
				return ((XSDDateTime) value).asCalendar().getTime();
			}
			else if (value instanceof String) {
				DateFormat df = DateFormat.getDateTimeInstance();
				try {
					return df.parse((String) value);
				}
				catch (ParseException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return value;
	}

	/**
	 * Get URIs as String based on a sparql query. The resultset should include
	 * only one variable.
	 * 
	 * @param sparql
	 * @return List of uris string or null if more that 1 bound variable was found
	 */
	public static List<String> getURIs(String sparql) {
		ResultSet rs = getRDFFromEndpoint(sparql);
		List<String> uris = new ArrayList<String>();
		if (rs.getResultVars() == null || rs.getResultVars().size() != 1) {
			return null;
		}
		else {
			String var = rs.getResultVars().get(0);
			QuerySolution qs = null;
			RDFNode node;
			while (rs.hasNext()) {
				qs = rs.next();
				// if the variable is bound
				if (qs.contains(var)) {
					node = qs.get(var);
					// if it is literal
					if (node.isURIResource()) {
						uris.add(node.asResource().getURI());
					}
				}
			}
			return uris;
		}

	}

	/**
	 * Get literal values based on a sparql query. The resultset should include
	 * only one variable.
	 * 
	 * @param sparql
	 * @return List of the found literal values or null if more than 1 bound
	 *         variables was found
	 */
	public static List<Literal> getLiteralValue(String sparql) {
		ResultSet rs = getRDFFromEndpoint(sparql);
		List<Literal> ls = new ArrayList<Literal>();
		if (rs.getResultVars() == null || rs.getResultVars().size() != 1) {
			return null;
		}
		else {
			String var = rs.getResultVars().get(0);
			QuerySolution qs = null;
			RDFNode node;
			while (rs.hasNext()) {
				qs = rs.next();
				// if the variable is bound
				if (qs.contains(var)) {
					node = qs.get(var);
					// if it is literal
					if (node.isLiteral()) {
						ls.add(node.asLiteral());
					}
					else {
						return null;
					}

				}
			}
			return ls;
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> void setFieldValue(T obj, String fieldName, Object fieldValue) throws NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		Field field = obj.getClass().getDeclaredField(fieldName);
		Type fieldType = field.getType();
		// special scenario for lists
		if (fieldType.equals(List.class)) {
			// get the current value of the field (the list)
			Object curValue = field.get(obj);
			((List<Object>) curValue).add(fieldValue);
		}
		else {
			Object value = convertValue(fieldValue, fieldType);
			field.set(obj, value);
		}
	}
}
