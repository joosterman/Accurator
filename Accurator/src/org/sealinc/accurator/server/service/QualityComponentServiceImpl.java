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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.sealinc.accurator.client.service.QualityComponentService;
import org.sealinc.accurator.server.Utility;
import org.sealinc.accurator.shared.Annotation;
import org.sealinc.accurator.shared.Config;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class QualityComponentServiceImpl extends RemoteServiceServlet implements QualityComponentService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3313978173295317354L;

	@Override
	public double getTrustworthiness(String annotationURI) {
		String url = String.format("%s?output=%s&strategy=%s&annotation=%s",Config.getQualityComponentTrustworthinessURL(),"json",Config.getQualityComponentTrustworthinessStrategy(),annotationURI);
		double value = Utility.getParsedJSONFromURL(url);
		return value;
	}
	
	@Override
	public List<Annotation> getRecentAnnotations() {
		// first get the most recent Annotation
		// TODO: check that they are not yet reviewed
		String sparqlRecent = String.format("%s SELECT ?subject WHERE {?subject oa:annotated ?dt .} ORDER BY DESC(?dt) LIMIT %d",
				Config.getRDFPrefixes(), Config.getQualityComponentNrRecentAnnotations());
		List<String> recentAnns = Utility.getRDFAndConvertToList(sparqlRecent);
		// next get the full annotation for these URIs
		List<Annotation> fullAnns = Utility.getObjectsByURI(recentAnns, Annotation.class);
		Collections.sort(fullAnns, new CompAnnDate());
		Collections.reverse(fullAnns);
		return fullAnns;
	}
	public class CompAnnDate implements Comparator<Annotation>{

		@Override
		public int compare(Annotation a1, Annotation a2) {
			if(a1.annotated==null && a2.annotated!=null)
				return -1;
			else if(a1.annotated!=null && a2.annotated==null)
				return 1;
			else if(a1.annotated==null && a2.annotated==null)
				return 0;
			else
			return a1.annotated.compareTo(a2.annotated);
		}
		
		
	}

}
