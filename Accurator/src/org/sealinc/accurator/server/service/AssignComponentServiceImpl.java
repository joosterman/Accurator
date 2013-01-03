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
package org.sealinc.accurator.server.service;

import java.util.List;

import org.sealinc.accurator.client.service.AssignComponentService;
import org.sealinc.accurator.server.Utility;
import org.sealinc.accurator.shared.CollectionItem;
import org.sealinc.accurator.shared.Config;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AssignComponentServiceImpl extends RemoteServiceServlet implements AssignComponentService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1666793749883291043L;

	@Override
	public List<String> getNextItemsToAnnotate(int nritems,String matches) {
		String url = String.format("%s?output=%s&strategy=%s&nritems=%d&string=%s&predicate=http://purl.org/dc/terms/description",Config.getAssignComponentURL(), "json",Config.getAssignStrategy(),nritems,matches);
		return Utility.getParsedJSONFromURL(url);
	}

	@Override
	public List<CollectionItem> search(String text) {
		String url = String.format("%s&string=%s",Config.getRecommendationComponentSearchURL(),text);
		List<String> items = Utility.getParsedJSONFromURL(url);
		return Utility.getObjectsByURI(items, CollectionItem.class,CollectionItem.rdfType);
	}
}
