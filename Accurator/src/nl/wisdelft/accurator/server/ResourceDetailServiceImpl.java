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

import nl.wisdelft.accurator.client.service.ResourceDetailService;
import nl.wisdelft.accurator.shared.ResourceDetail;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class ResourceDetailServiceImpl extends RemoteServiceServlet implements ResourceDetailService {
	private final String sparql = "PREFIX rmaschema: <http://purl.org/collections/nl/rma/schema#> PREFIX rmaterms: <http://purl.org/collections/nl/rma/terms/> SELECT ?predicate ?subject WHERE { <%1$s> ?predicate ?subject .  }";
	private final String description = "http://purl.org/dc/terms/description";
	private final String title = "http://purl.org/dc/terms/title";
	private final String imageURL = "http://purl.org/collections/nl/rma/schema#imageURL";
	
	
	@Override
	public ResourceDetail GetResourceDetail(String resourceURI) {
		QueryExecution qe = QueryExecutionFactory.sparqlService(Utility.getEndpoint(), String.format(sparql, resourceURI));
		ResourceDetail detail = new ResourceDetail(resourceURI);
		try {
			ResultSet rs = qe.execSelect();
			QuerySolution qs = null;
			while(rs.hasNext()){
				qs= rs.next();
				String uri = qs.getResource("predicate").getURI();
				String value = qs.get("subject").toString();
				if(uri.equals(description))
					detail.setDescription(value);
				else if(uri.equals(title))
					detail.setTitle(value);
				else if(uri.equals(imageURL))
					detail.setImageURL(value);
			}			
		} finally {
			qe.close();
		}
		return detail;
	}


	@Override
	public List<ResourceDetail> GetResourcesDetail(List<String> resourceURIs) {
		List<ResourceDetail> details = new ArrayList<ResourceDetail>();
		for(String uri:resourceURIs){
			details.add(GetResourceDetail(uri));
		}
		return details;
	}
}
