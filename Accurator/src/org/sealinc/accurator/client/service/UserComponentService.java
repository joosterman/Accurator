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
package org.sealinc.accurator.client.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.sealinc.accurator.shared.Annotation;
import org.sealinc.accurator.shared.CollectionItem;
import org.sealinc.accurator.shared.Review;
import org.sealinc.accurator.shared.View;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("UserComponentService")
public interface UserComponentService extends RemoteService {
	
	public List<Annotation> getAnnotations(String user, int nrAnnotations);
	public List<CollectionItem> getLastAnnotatedItems(String user, int nrItems);
	public boolean setViewed(String resourceURI, View view);
	public boolean setReview(String annotationURI, Review review);
	public int getTotalAnnotatedPrints(String user,Date annotatedSince);
	public Map<String,Integer> getExpertise(String user);
	
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static UserComponentServiceAsync instance;
		public static UserComponentServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(UserComponentService.class);
			}
			return instance;
		}
	}
}
