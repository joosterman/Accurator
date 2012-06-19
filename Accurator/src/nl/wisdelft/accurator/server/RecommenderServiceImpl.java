/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nl.wisdelft.accurator.server;

import java.util.ArrayList;
import java.util.List;

import nl.wisdelft.accurator.client.service.RecommenderService;
import nl.wisdelft.accurator.shared.ResourceWithValue;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;

public class RecommenderServiceImpl extends RemoteServiceServlet implements RecommenderService {
	private static final String sparql = "PREFIX rmaschema: <http://purl.org/collections/nl/rma/schema#>" + "PREFIX rmaterms: <http://purl.org/collections/nl/rma/terms/>" + "SELECT ?resource "
			+ "WHERE" + "{ ?resource rmaschema:collection rmaterms:prenten . } " + "LIMIT 3";

	@Override
	public List<ResourceWithValue<Double>> getRankedRecommendedItems(String userURI) {
		QueryExecution qe = QueryExecutionFactory.sparqlService(Utility.getEndpoint(), sparql);		
		List<ResourceWithValue<Double>> result = new ArrayList<ResourceWithValue<Double>>();
		try {
			ResultSet rs = qe.execSelect();
			QuerySolution qs = null;
			while(rs.hasNext()){
				qs= rs.next();
				Resource r = qs.get("resource").asResource();
				result.add(new ResourceWithValue<Double>(r.getURI(),1.0));
			}			
		} finally {
			qe.close();
		}
		return result;
	}

	@Override
	public List<String> getRecommendedItems(String userURI) {
		QueryExecution qe = QueryExecutionFactory.sparqlService(Utility.getEndpoint(), sparql);		
		List<String> result = new ArrayList<String>();
		try {
			ResultSet rs = qe.execSelect();
			QuerySolution qs = null;
			while(rs.hasNext()){
				qs= rs.next();
				result.add(qs.getResource("resource").getURI());
			}			
		} finally {
			qe.close();
		}
		return result;
	}
}
