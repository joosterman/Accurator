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
package nl.wisdelft.accurator.client.service;

import java.util.List;

import nl.wisdelft.accurator.shared.ResourceDetail;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ResourceDetailService")
public interface ResourceDetailService extends RemoteService {
	public ResourceDetail GetResourceDetail(String resourceURI);
	public List<ResourceDetail> GetResourcesDetail(List<String> resourceURIs);
	
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static ResourceDetailServiceAsync instance;
		public static ResourceDetailServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(ResourceDetailService.class);
			}
			return instance;
		}
	}
}
