package org.sealinc.accurator.server;

import java.io.BufferedReader;
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
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.sealinc.accurator.shared.Config;
import org.sealinc.accurator.shared.Namespace;
import org.sealinc.accurator.shared.RDFObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
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
			return -1;
		}
	}

	public static boolean register(String user, String realname, String password) {	
		//encode the real name
		try {
			realname = URLEncoder.encode(realname,"UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
		
		String url = String.format("%s?user=%s&realname=%s&password=%s", Config.getAdminRegisterUserURL(), user, realname, password);
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
		String url = String.format("%s?user=%s&password=%s", Config.getLoginURL(), Config.getAdminUsername(), Config.getAdminPassword());
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
		String url = Config.getLogoutURL();
		Integer code = Utility.getStatusCode(url);
		return code != null && code == 200;
	}

	public static String getHTMLContent(String url) {
		URL u;
		try {
			u = new URL(url);
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(u.openStream()));
			String line = reader.readLine();
			while (line != null) {
				builder.append(line);
				line = reader.readLine();
			}
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
		String url = Config.getAdminComponentUploadDataURL();
		URL u = new URL(url);
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
			String s = String.format("baseURI=%s&data=", Config.getAdminComponentBaseURI());
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
			URL url = new URL(String.format("%s?query=%s", Config.getSparqlEndpoint(), URLEncoder.encode(sparql, "UTF-8")));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.addRequestProperty("Accept", "application/sparql-results+xml");
			con.setReadTimeout(60000);
			con.setRequestMethod("GET");
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

	public static <T extends RDFObject> List<T> getObjectsByURI(List<String> uris, Class<T> clazz) {
		// generate sparql
		StringBuilder sb = new StringBuilder();
		String comparison1 = "?subject = ";
		String union = " || ";
		String lt = "<";
		String gt = ">";
		for (String uri : uris) {
			if (sb.length() != 0) sb.append(union);
			sb.append(comparison1);
			sb.append(lt);
			sb.append(uri);
			sb.append(gt);
		}
		String sparql = String.format("%s SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object . FILTER( %s )}",
				Config.getRDFPrefixes(), sb.toString());
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
	public static <T extends RDFObject> List<T> getObjects(String sparql, Class<T> clazz) {
		ResultSet rs = getRDFFromEndpoint(sparql);
		if (rs == null) return null;
		QuerySolution qs = null;
		List<T> objs = new ArrayList<T>();
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
		System.err.println("Class " + clazz.getName() + " does not have the following fields (accessable): \n" + unknownFields.toString());
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
	 * Get literal values based on a sparql query. The resultset should include
	 * only one variable.
	 * 
	 * @param sparql
	 * @return null if there are more variables or the resulting values are not
	 *         literal values
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
