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

import java.util.List;
import org.sealinc.accurator.client.service.UserComponentService;
import org.sealinc.accurator.server.Utility;
import org.sealinc.accurator.shared.Annotation;
import org.sealinc.accurator.shared.Config;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class UserComponentServiceImpl extends RemoteServiceServlet implements UserComponentService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4303074923419143275L;

	@Override
	public List<Annotation> getAnnotations(String userURI) {
		String sparql = String.format("%s SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object . ?subject rdf:type oa:Annotation . ?subject oa:annotator <%s> } ", Config.getRDFPrefixes(), userURI);
		List<Annotation> anns = null;
		anns = Utility.getObjects(sparql, Annotation.class);

		return anns;
	}

}
