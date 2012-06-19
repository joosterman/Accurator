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

import nl.wisdelft.accurator.client.service.UserInformationService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class UserInformationServiceImpl extends RemoteServiceServlet implements UserInformationService {
	private final String sparql = "PREFIX oac: <http://www.openannotation.org/ns/> PREFIX dcterms: <http://purl.org/dc/terms/> SELECT ?body  WHERE { ?x oac:hasBody ?body .  }";
	
	@Override
	public List<String> GetAnnotations(String userURI) {
		QueryExecution qe = QueryExecutionFactory.sparqlService(Utility.getEndpoint(), sparql);
		List<String> annotations = new ArrayList<String>();
		try {
			ResultSet rs = qe.execSelect();
			QuerySolution qs = null;
			while(rs.hasNext()){
				qs= rs.next();			
				annotations.add(qs.get("body").toString());
			}			
		} finally {
			qe.close();
		}
		return annotations;
	}
}
