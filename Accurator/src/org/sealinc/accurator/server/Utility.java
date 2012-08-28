package org.sealinc.accurator.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.sealinc.accurator.shared.Config;
import org.sealinc.accurator.shared.RDFObject;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.graph.Node;
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
import com.hp.hpl.jena.sparql.resultset.ResultSetException;

public class Utility {

	public static final Gson gson = new Gson();
	public static final JsonParser jsonParser = new JsonParser();
	private static RDFVisitor visitor;

	private Utility() {}

	private static RDFVisitor getRDFVisitor() {
		if (visitor == null) {
			visitor = new RDFVisitor() {

				@Override
				public Object visitURI(Resource r, String uri) {
					return uri;
				}

				@Override
				public Object visitLiteral(Literal l) {
					// TODO Auto-generated method stub
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

	private static Integer getStatusCode(String url) {
		Integer code = null;
		try {
			URL u = new URL(url);

			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			code = con.getResponseCode();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return code;
	}

	/**
	 * Login the Server component with the configured credentials
	 * 
	 * @return If the logon was successful
	 */
	public static boolean login() {
		String url = String.format("%s?user=%s&password=%s", Config.getLoginURL(), Config.getAdminUsername(), Config.getAdminPassword());
		Integer code = Utility.getStatusCode(url);
		return code != null && code == 200;
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

	/**
	 * Checks if the Server component is currently logged on
	 * 
	 * @return
	 */
	public static boolean isLoggedIn() {
		String url = Config.getLoginCheckURL();
		Integer code = Utility.getStatusCode(url);
		return code != null && code == 200;
	}

	public static String getContent(String url) {
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
		String content = getContent(url);
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
	
	private static void adminPrecondition(){
		login();
	}

	public static boolean uploadData(Model m) {
		adminPrecondition();
		String url = Config.getAdminComponentUploadDataURL();
		RDFWriter writer = m.getWriter("RDF/XML");
		URL u;
		
		HttpURLConnection con;
		int responseCode = 0;
		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			PrintStream out = new PrintStream(con.getOutputStream());
			out.print(String.format("baseURI=%s&",Config.getAdminComponentBaseURI()));
			out.print("data=");
			writer.write(m, out, null);
			out.close();
			responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;		
			String output = "";
			while ((line = in.readLine()) != null) {
				output+=line;
			}
			System.out.println(output);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return responseCode==200;
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
			con.setReadTimeout(10000);
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
		String comparison = "?subject=";
		String or = " || ";
		String lt = "<";
		String gt = ">";
		for (String uri : uris) {
			if (sb.length() != 0) sb.append(or);
			sb.append(comparison);
			sb.append(lt);
			sb.append(uri);
			sb.append(gt);
		}
		String sparql = String.format("%s SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object . FILTER (%s) . }  ",
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
	 *         not created
	 */
	public static <T extends RDFObject> List<T> getObjects(String sparql, Class<T> clazz) {
		ResultSet rs = getRDFFromEndpoint(sparql);
		QuerySolution qs = null;
		List<T> objs = new ArrayList<T>();
		String uri, fieldName;
		Object fieldValue;
		Node node;
		T obj = null;
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
			if (index >= 0) obj = objs.get(index);
			else objs.add(obj);
			fieldName = qs.get("predicate").asNode().getLocalName();
			fieldValue = qs.get("object").visitWith(getRDFVisitor());
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

	private static <T> void setFieldValue(T obj, String fieldName, Object fieldValue) throws NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		Field field = obj.getClass().getDeclaredField(fieldName);
		Type fieldType = field.getType();
		// special scenario for lists
		if (fieldType.equals(List.class)) {
			// get the current value of the field (the list)
			Object curValue = field.get(obj);
			((List) curValue).add(fieldValue);
		}
		else {
			Object value = convertValue(fieldValue, fieldType);
			field.set(obj, value);
		}
	}
}
