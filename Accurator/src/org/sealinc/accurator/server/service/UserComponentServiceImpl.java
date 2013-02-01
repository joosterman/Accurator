/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *******************************************************************************/
package org.sealinc.accurator.server.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;
import org.sealinc.accurator.client.service.UserComponentService;
import org.sealinc.accurator.server.Utility;
import org.sealinc.accurator.shared.Annotation;
import org.sealinc.accurator.shared.CollectionItem;
import org.sealinc.accurator.shared.Config;
import org.sealinc.accurator.shared.NS;
import org.sealinc.accurator.shared.Review;
import org.sealinc.accurator.shared.View;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class UserComponentServiceImpl extends RemoteServiceServlet implements UserComponentService {

	private static final long serialVersionUID = 4303074923419143275L;
	private static Logger logger = Logger.getLogger(UserComponentService.class.getName());

	@Override
	public List<Annotation> getAnnotations(String user, int nrAnnotations) {
		String userURI = Config.userComponentUserURI + user;
		String sparql = String.format(
				"%s SELECT ?subject WHERE { ?subject rdf:type oa:Annotation . ?subject oa:annotated ?date . ?subject oa:annotator <%s> } ORDER BY DESC(?date) LIMIT %s",
				Config.sparqlPrefixes, userURI, nrAnnotations);
		List<String> uris = Utility.getURIs(sparql);
		List<Annotation> anns = null;
		anns = Utility.getObjectsByURI(uris, Annotation.class, Annotation.rdfType);
		return anns;
	}

	@Override
	public boolean setViewed(String resourceURI, View view) {
		if (resourceURI == null) return false;
		logger.info(view.viewer + " viewed " + resourceURI);
		// create the view URI
		String uri = String.format("%sview/%s-%s-%s", Config.adminComponentBaseURI, resourceURI.hashCode(), view.viewer.hashCode(),
				GregorianCalendar.getInstance().get(Calendar.DAY_OF_YEAR));
		view.uri = uri;
		// create new model
		Model m = ModelFactory.createDefaultModel();
		// add view resource
		Resource resourceView = m.createResource(uri);
		// add the review properties
		m = Utility.toRDF(view, m);
		// create the hasReviews property
		Property p = m.createProperty(NS.accurator, "hasView");
		// create the annotation resource
		Resource resource = m.createResource(resourceURI);
		// add the hasReview triple
		m.add(resource, p, resourceView);

		// store the data
		return Utility.uploadData(m);
	}

	@Override
	public boolean setReview(String annotationURI, Review review) {
		logger.info(review.reviewer + " reviewed " + annotationURI);
		// create the review URI
		String uri = String.format("%sreview/%s-%s-%s", Config.adminComponentBaseURI, annotationURI.hashCode(),
				review.reviewer.hashCode(), GregorianCalendar.getInstance().get(Calendar.DAY_OF_YEAR));
		review.uri = uri;
		// create new model
		Model m = ModelFactory.createDefaultModel();
		// add review resource
		Resource resourceReview = m.createResource(uri);
		// add the review properties
		m = Utility.toRDF(review, m);
		// create the hasReviews property
		Property p = m.createProperty(NS.review, "hasReview");
		// create the annotation resource
		Resource resourceAnnotation = m.createResource(annotationURI);
		// add the hasReview triple
		m.add(resourceAnnotation, p, resourceReview);

		// store the data
		return Utility.uploadData(m);
	}

	@Override
	public int getTotalAnnotatedPrints(String user, Date annotatedSince) {
		String userURI = Config.userComponentUserURI + user;
		String sparql = String.format(
				"%s SELECT DISTINCT ?target WHERE { ?subject rdf:type oa:Annotation . ?subject oa:hasTarget ?target . ?subject oa:annotator <%s> . ?target rdf:type <%s> }",
				Config.sparqlPrefixes, userURI, CollectionItem.rdfType);

		// count unique uris
		List<String> uris = Utility.getURIs(sparql);
		return uris.size();
	}

	@Override
	public List<CollectionItem> getLastAnnotatedItems(String user, int nrItems) {
		// get all
		String userURI = Config.userComponentUserURI + user;
		String sparql = String.format(
				"%s SELECT DISTINCT ?target WHERE { ?subject rdf:type oa:Annotation . ?subject oa:annotated ?date . ?subject oa:hasTarget ?target . ?subject oa:annotator <%s> . ?target rdf:type <%s> } ORDER BY DESC(?date)",
				Config.sparqlPrefixes, userURI, CollectionItem.rdfType);
		List<String> uris = Utility.getURIs(sparql);
		if (uris.size() > nrItems) uris = uris.subList(0, nrItems);
		List<CollectionItem> items = Utility.getObjectsByURI(uris, CollectionItem.class, CollectionItem.rdfType);
		return items;
	}
}
